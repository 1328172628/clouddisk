package com.yc.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-12 15:33
 */
public interface HdfsService {

    /**
     * 获取配置文件 以map形式返回
     *
     * @return
     * @throws Exception
     */
    public Map<String, String> getConfigurationInfoAsMap() throws Exception;

    /**
     * 创建文件
     * @param path
     * @return
     * @throws Exception
     */
    public void createFile(String path, MultipartFile file) throws Exception;

    /**
     * 在HDFS中创建文件夹
     *
     * @param path
     * @return
     * @throws Exception
     */
    public boolean mkdir(String path) throws Exception;

    /**
     * 在HDFS中删除文件
     * @param path
     * @return
     * @throws Exception
     */
    public boolean delete(String path) throws Exception;

    /**
     * 在HDFS中修改文件名
     * @param path
     * @param newPath
     * @return
     * @throws Exception
     */
    public boolean rename(String path,String newPath) throws Exception;

    /**
     * 下载目录
     * @param path
     * @param fileName
     * @return
     * @throws Exception
     */
    public ResponseEntity<byte[]> downloadDirectory(String path, String fileName) throws Exception;

    /**
     * 下载文件
     * @param path
     * @param fileName
     * @return
     * @throws Exception
     */
    public ResponseEntity<InputStreamResource> downloadFile(String path, String fileName) throws Exception;

    /**
     * 文件复制
     * @param sourcePath
     * @param targetPath
     * @throws Exception
     */
    public void copyFile(String sourcePath,String targetPath) throws  Exception;

    /**
     * 判断HDFS文件夹是否存在
     *
     * @param path
     * @return
     * @throws Exception
     */
    public boolean existFile(String path) throws Exception;

    public List<Map<String, String>> liatStatus(String path) throws Exception;


    public List<Map<String, Object>> readPathInfo(String path) throws Exception;

    /**
     * 读取hdfs文件列表
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> listFile(String path) throws Exception;


}
