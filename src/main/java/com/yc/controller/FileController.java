package com.yc.controller;

import com.yc.service.HdfsService;
import com.yc.utils.GetFileMD5;
import com.yc.utils.RedisBloom;
import com.yc.vo.JsonModel;
import io.rebloom.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-16 17:15
 */
@Controller
public class FileController {

    @Autowired
    private HdfsService hdfsService;

    private Client client = null;

    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    @ResponseBody
    public JsonModel uploadData(MultipartFile file, String currentPath, HttpServletRequest request, JsonModel jm) {
        try {
            client = RedisBloom.getClient();
            // file.getOriginalFilename()  这个方法才能获取 filename  getName 不行
            //TODO:  上传文件 会判断是否已经存在  使用文件名判断 不靠谱
            //          可以用文件计算MD5 将MD5存入布隆过滤器
            //        每次上传 计算MD5 到过滤器中比较
            // hdfs上原本文件 怎么计算？  实际生产不用考虑  因为所有文件上传时已经计算MD5 并保存
            //              从hdfs获取文件 然后在java中计算MD5 文件大/多 性能差
            //             在linux上计算 hdfs dfs -cat /core-site.xml | md5sum  需要连接到linux

            String md5 = GetFileMD5.getMD5(file);
            String path = currentPath + file.getOriginalFilename();
            if (currentPath.lastIndexOf("/") != currentPath.length()-1) {
                path = currentPath + "/" + file.getOriginalFilename();
            }

            if (!client.exists("MD5", md5)){
                if (!hdfsService.existFile(path )) {
                    //TODO: 需要确保 同时成功
                    hdfsService.createFile(currentPath, file);
                    jm.setCode(1);
                    jm.setObj("上传成功！");
                    client.add("MD5", md5);
                }else {
                    jm.setCode(-1);
                    jm.setMsg("当前目录有重名文件！");
                }

            }else {
                jm.setCode(-1);
                jm.setMsg("文件已经存在！");
            }


        } catch (Exception e) {
            e.printStackTrace();
            jm.setCode(0);
            jm.setMsg(e.getMessage());
        }finally {
            if (client != null) {
                client.close();
            }
        }
        return jm;
    }

    @RequestMapping(value = "/downLoadDirectory", method = RequestMethod.GET)
    public ResponseEntity<byte[]> downLoadDirectory(String path,String fileName) {
        ResponseEntity<byte[]> result = null;
        try {
            result = hdfsService.downloadDirectory(path, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @RequestMapping(value = "/downLoadFile", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downLoadFile(String path, String fileName) {
        ResponseEntity<InputStreamResource> result = null;
        try {
            result = hdfsService.downloadFile(path, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
