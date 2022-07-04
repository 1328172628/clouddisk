package com.yc.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-16 18:55
 */
public class MfFileUtil {

    public static String genFileName(){
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(d) + ".zip";
    }

    public static ResponseEntity<byte[]> downLoadDirectory(byte[] bs, String fileName) {
        try {
            //http的响应协议
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache,no-store,must-revalidate");
            //件下截的方式：Content-Disposition:Xxx.txt
            headers.add("Content-Disposition", String.format("attachment;filename=\"%s\"", fileName));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Language", "UTF-8");
            return ResponseEntity.ok().headers(headers).contentLength(bs.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(bs);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static ResponseEntity<InputStreamResource> downloadFile(InputStream in, String fileName){
        try {
            byte[] testBytes = new byte[in.available()];
            //http的响应协议
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache,no-store,must-revalidate");
            //文件下载的方式：Content-Disposition:Xxx.txt
            headers.add("Content-Disposition", String.format("attachment;filename=\"%s\"", fileName));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Language", "UTF-8");
            //最终这句，让文件内容以流的形式输出
            // 0K:200
            // HTTP 200 OK
            // Cache-Control xx
            // Content-Disposition:attachment;filename=xxx.txt
            // content-length:xxx
            // 字节码
            return ResponseEntity.ok().headers(headers).contentLength(testBytes.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(in));
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;

    }

}
