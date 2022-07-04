package com.yc;

import com.yc.service.HdfsServiceImpl;
import com.yc.utils.RedisBloom;
import io.rebloom.client.Client;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.List;
import java.util.Map;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-12 15:58
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

    private static Logger logger = Logger.getLogger(Application.class);

    public static void main(String[] args) {
        //initRedis();
        SpringApplication.run(Application.class, args);
    }

    /**
     * 将hdfs 文件加载到 redis bloom filter 中
     */
    public static void initRedis(){
        //TODO:  能用名字来做标识吗？ 重名但是文件不一样 所以用名字不合理  那么怎么处理？
        // 对文件 md5 加密  将md5放入 布隆过滤器
        // 实际不会有这一段
        HdfsServiceImpl hdfsService = new HdfsServiceImpl();
        Client client = null;
        try {
            List<Map<String, String>> listFile = hdfsService.listFile("/");
            client = RedisBloom.getClient();
            for (Map<String, String> file : listFile) {
                if (!client.exists("fileName",file.get("fileName"))) {
                    client.add("fileName", file.get("fileName"));
                }
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
