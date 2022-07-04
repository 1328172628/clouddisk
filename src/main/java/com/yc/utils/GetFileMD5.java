package com.yc.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;


/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-27 14:36
 */
public class GetFileMD5 {

    private static Logger log = Logger.getLogger(GetFileMD5.class);

    /**
     * 获取上传文件的md5
     * @param file
     * @return
     * @throws
     */
    public static String getMd5(MultipartFile file) {
        //TODO: 超大文件怎么办 在内存中计算吗？
        // 因为 md5 是每 512 bit 作为一个 chunk 进行计算的。
        // 所以可以每次读取一部分的内容（最少 512 bit，比较合适是 st_blksize），
        // 进行那些 chunk 部分的计算，之后再读取下一部分内容继续计算。
        // MD5算法本身是分块的，其他很多类似的算法比如SHA-1也是的，
        // 所以可以支持流式计算，读一块算一块，最后再一次性生成完整hash，完全没有内存爆炸的可能。
        try {
            //获取文件的byte信息
            byte[] uploadBytes = file.getBytes();
            // 拿到一个MD5转换器
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            //转换为16进制
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    public static String getMD5(MultipartFile file) {

        //DigestUtils.md5Hex(file.getInputStream());  与下面代码同样功能

        InputStream inputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            inputStream = file.getInputStream();
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }

}
