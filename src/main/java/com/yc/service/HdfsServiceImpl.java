package com.yc.service;

import com.yc.utils.MfFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-12 15:37
 */
@Service
public class HdfsServiceImpl implements HdfsService{

    @Value("${hdfs.path}")
    private String path;
    @Value("${hdfs.username}")
    private String username;
    @Value("${hdfs.nameservices}")
    private String nameservices;
    @Value("${hdfs.namenodes}")
    private String namenodes;
    @Value("${hdfs.node1}")
    private String node1;
    @Value("${hdfs.node2}")
    private String node2;
    @Value("${hdfs.provider}")
    private String provider;

    private final int bufferSize = 1024 * 1024 * 64;

    public Configuration getConf(){
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFs", "hdfs://yc/");
        configuration.set("dfs.nameservices", "yc");
        configuration.set("dfs.ha.namenodes.yc","nn1,nn2");
        configuration.set("dfs.namenode.rpc-address.yc.nn1","node1:8020");
        configuration.set("dfs.namenode.rpc-address.yc.nn2","node2:8020");
        configuration.set("dfs.client.failover.proxy.provider.yc","org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        return configuration;
    }

    public FileSystem getFS() throws URISyntaxException, IOException, InterruptedException {
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://yc/"), getConf(),"root");
        return fileSystem;
    }

    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFs", path);
        configuration.set("dfs.nameservices", nameservices);
        configuration.set("dfs.ha.namenodes.yc",namenodes);
        configuration.set("dfs.namenode.rpc-address.yc.nn1",node1);
        configuration.set("dfs.namenode.rpc-address.yc.nn2",node2);
        configuration.set("dfs.client.failover.proxy.provider.yc",provider);

        return configuration;
    }

    public FileSystem getFileSystem() throws URISyntaxException, IOException, InterruptedException {
        // 客户端去操作hdfs时是有一个用户身份的，默认情况下hdfs客户端api会从jvm中获取一个参数作为自己的用户身份
        // DHADOOP_USER_NAME=hadoop
        // 1.在windos的环境变量中添加一个变量  DHADOOP_USER_NAME=root
        // 2.也可以在构造客户端fs对象时，通过参数传递过去
        FileSystem fileSystem = FileSystem.get(new URI(path), getConfiguration(),username);
        return fileSystem;
    }

    @Override
    public Map<String, String> getConfigurationInfoAsMap() throws Exception {
        FileSystem fileSystem = getFileSystem();
        Configuration conf = fileSystem.getConf();
        Iterator<Map.Entry<String, String>> ite = (Iterator<Map.Entry<String, String>>) conf.iterator();
        Map<String, String> map = new HashMap<>();
        while (ite.hasNext()) {
            Map.Entry<String, String> entry = ite.next();
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @Override
    public void createFile(String path, MultipartFile file) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return;
        }

        String fileName = file.getOriginalFilename();
        FileSystem fs = getFileSystem();
        Path newPath = null;
        if ("/".equals(path)) {
            newPath = new Path(path + fileName);
        }else{
            newPath = new Path(path + "/" + fileName);
        }

        FSDataOutputStream outputStream = fs.create(newPath);
        outputStream.write(file.getBytes());
        outputStream.close();
        fs.close();
    }

    /**
     * 在HDFS创建文件夹
     * @param path
     * @return
     * @throws Exception
     */
    @Override
    public boolean mkdir(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (existFile(path)) {
            return true;
        }
        FileSystem fs = getFileSystem();
        Path srcPath = new Path(path);
        boolean isOk = fs.mkdirs(srcPath);
        fs.close();
        return isOk;
    }

    @Override
    public boolean delete(String path) throws Exception {
        if (StringUtils.isEmpty(path)){
            return false;
        }if (!existFile(path)) {
            return false;
        }
        FileSystem fileSystem = getFileSystem();
        Path srcPath = new Path(path);
        boolean isDelete = fileSystem.delete(srcPath,true);

        fileSystem.close();
        return isDelete;
    }

    @Override
    public boolean rename(String path,String newName) throws Exception {
        if (StringUtils.isEmpty(path)){
            return false;
        }if (!existFile(path)) {
            return false;
        }
        FileSystem fileSystem = getFileSystem();
        Path srcPath = new Path(path);
        Path newPath = new Path(newName);
        boolean isDelete = fileSystem.rename(srcPath,newPath);

        fileSystem.close();
        return isDelete;
    }

    @Override
    public ResponseEntity<byte[]> downloadDirectory(String path, String fileName) throws Exception {
        ByteArrayOutputStream out = null;
        try {
            FileSystem fs = getFileSystem();
            out = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(out);
            compress(path, zos, fs);
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
        byte[] bs = out.toByteArray();
        out.close();
        return MfFileUtil.downLoadDirectory(bs,MfFileUtil.genFileName());
    }

    private void compress(String baseDir, ZipOutputStream zipOutputStream, FileSystem fs) {
        try {
            FileStatus[] fileStatulist = fs.listStatus(new Path(baseDir));
            String[] strs = baseDir.split("/");
            //LastName代表路径最后的单词
            String lastName = strs[strs.length - 1];
            for (int i = 0; i < fileStatulist.length; i++) {
                String name = fileStatulist[i].getPath().toString();
                name = name.substring(name.indexOf("/" + lastName));
                if (fileStatulist[i].isFile()) {
                    Path path = fileStatulist[i].getPath();
                    FSDataInputStream inputStream = fs.open(path);
                    zipOutputStream.putNextEntry(new ZipEntry(name.substring(1)));
                    IOUtils.copyBytes(inputStream, zipOutputStream, this.bufferSize);
                    inputStream.close();
                } else {
                    //是目录则递归调用：
                    zipOutputStream.putNextEntry(new ZipEntry(fileStatulist[i].getPath().getName() + "/"));
                    compress(fileStatulist[i].getPath().toString(), zipOutputStream, fs);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(String path, String fileName) throws Exception {
        FileSystem fs = getFileSystem();
        Path p = new Path(path);
        FSDataInputStream inputStream = fs.open(p);
        return MfFileUtil.downloadFile(inputStream, fileName);
    }

    @Override
    public void copyFile(String sourcePath, String targetPath) throws Exception {
        if (StringUtils.isEmpty(sourcePath) || StringUtils.isEmpty(targetPath)) {
            return;
        }
        FileSystem fs = getFileSystem();
        Path oldPath = new Path(sourcePath);
        Path newPath = new Path(targetPath + sourcePath);

        boolean isDirectory = fs.isDirectory(oldPath);

        FSDataInputStream inputStream = null;
        FSDataOutputStream outputStream = null;
        try{
            outputStream = fs.create(newPath);
            if (!isDirectory) {
                inputStream = fs.open(oldPath);
                IOUtils.copyBytes(inputStream, outputStream, bufferSize, false);
            }
        }finally {
            if (inputStream != null) {
                inputStream.close();
            }
            outputStream.close();
            fs.close();
        }
    }

    @Override
    public boolean existFile(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        FileSystem fileSystem = null;
        if ("".equals(this.path) || this.path == null) {
            fileSystem = getFS();
        }else{
            fileSystem = getFileSystem();
        }
        Path srcPath = new Path(path);
        boolean isExists = fileSystem.exists(srcPath);

        fileSystem.close();
        return isExists;
    }

    @Override
    public List<Map<String, String>> liatStatus(String path) throws Exception {
        if (StringUtils.isEmpty(path)){
            return null;
        }if (!existFile(path)) {
            return null;
        }
        FileSystem fs = getFileSystem();
        //目标路径
        Path srcPath = new Path(path);
        FileStatus[]fileStatuses = fs.listStatus(srcPath);

        if(fileStatuses == null || fileStatuses.length <= 0){
            return null;
        }
        List<Map<String,String>> returnList = new ArrayList<>();
        for(FileStatus file:fileStatuses) {
            Map<String, String> map = new HashMap<String, String>();
            Path p = file.getPath();
            map.put("fileName", p.getName());
            map.put("filePath", p.toUri().toString());
            if ("/".equalsIgnoreCase(path)) {
                map.put("relativePath", srcPath + p.getName());
            } else {
                map.put("relativePath", srcPath + "/" + p.getName());
            }
            map.put("parentPath", p.getParent().toUri().toString());
            map.put("owner", file.getOwner());
            map.put("group", file.getGroup());
            map.put("isFile", file.isFile() + "");
            map.put("duplicates", file.getReplication() + "");

            map.put("size", MfFileUtil.formatFileSize(file.getLen()));
            //map.put("size", String.valueOf(file.getLen()));

            map.put("rights", file.getPermission().toString());

            map.put("modifyTime",MfFileUtil.formatTime(file.getModificationTime()));
            //map.put("modifyTime", String.valueOf(file.getModificationTime()));
            returnList.add(map);
        }
        fs.close();
        return returnList;
    }

    @Override
    public List<Map<String, Object>> readPathInfo(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
        FileSystem fs = getFileSystem();
        Path newPath = new Path(path);
        FileStatus[] statusList = fs.listStatus(newPath);
        List<Map<String, Object>> list = new ArrayList<>();
        if (null != statusList && statusList.length > 0) {
            for (FileStatus fileStatus : statusList) {
                Map<String, Object> map = new HashMap<>();
                map.put("filePath", fileStatus.getPath());
                map.put("fileStatus", fileStatus.toString());
                list.add(map);
            }
            return list;
        } else {
            return null;
        }
    }

    @Override
    public List<Map<String, String>> listFile(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        if (!existFile(path)) {
            return null;
        }
        FileSystem fileSystem = null;
        if ("".equals(this.path) || this.path == null) {
            fileSystem = getFS();
        }else {
            fileSystem = getFileSystem();
        }
        RemoteIterator<LocatedFileStatus> listFiles = fileSystem.listFiles(new Path(path), true);
        List<Map<String, String>> returnList = new ArrayList<>();
        while (listFiles.hasNext()) {
            LocatedFileStatus next = listFiles.next();
            String fileName = next.getPath().getName();
            Path filePath = next.getPath();
            Map<String, String> map = new HashMap<>();
            map.put("fileName", fileName);
            map.put("filePath", filePath.toString());
            returnList.add(map);
        }
        fileSystem.close();
        return returnList;
    }
}
