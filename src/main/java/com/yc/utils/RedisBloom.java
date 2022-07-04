package com.yc.utils;


import io.rebloom.client.Client;
import redis.clients.jedis.Jedis;

/**
 * @program: clouddisk
 * @description:
 * @author: MF
 * @create: 2022-06-24 20:34
 */
public class RedisBloom {


    public static Jedis getRedis(){
        return getRedis("192.168.2.2", 6379);
    }

    public static Jedis getRedis(String host, int port){
        return new Jedis(host, port);
    }

    public static Client getClient() {
        return getClient("192.168.2.2", 6379);
    }

    public static Client getClient(String host, int port) {
        return new Client(getRedis(host, port));
    }

}
