package com.xing.jobs;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.constant.RedisConstant;
import com.xing.pojo.QiniuKey;
import com.xing.pojo.Setmeal;
import com.xing.service.SetmealService;
import com.xing.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClearImgJob {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private SetmealService setmealService;

    /***
     * clear cached img files in qiniu image server
     */
    public void clearImg(){
        try(Jedis jedis = jedisPool.getResource()) {
            // get pic names that didn't save into DB
            Set<String> trash = jedis.sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
            if( trash != null && trash.size() > 0) {
                QiniuKey key = setmealService.getQiniuKey();
                QiniuUtils utils = new QiniuUtils(key.getAccessKey(), key.getSecretKey(), "xingxingli");
                String[] filenames = new String[trash.size()];
                trash.toArray(filenames);
                utils.deleteFiles(filenames);
                jedis.srem(RedisConstant.SETMEAL_PIC_RESOURCES , filenames);
                System.out.println("ClearImgJob.clearImg method is completed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * clear redis cached file names
     */

    public void clearRedisCache(){
        try(Jedis jedis = jedisPool.getResource()) {
            Set<String> trash = jedis.sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
            if( trash != null && trash.size() > 0) {
                QiniuKey key = setmealService.getQiniuKey();
                QiniuUtils utils = new QiniuUtils(key.getAccessKey(), key.getSecretKey(), "xingxingli");
                String[] filenames = new String[trash.size()];
                trash.toArray(filenames);
                utils.deleteFiles(filenames);
                jedis.srem(RedisConstant.SETMEAL_PIC_RESOURCES , filenames);
            }
            jedis.del(RedisConstant.SETMEAL_PIC_DB_RESOURCES);
            jedis.del(RedisConstant.SETMEAL_PIC_RESOURCES);
            System.out.println("ClearImgJob.clearRedisCache method is completed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearQiniuImageServer(){
        List<Setmeal> setmeals = setmealService.findAll();
        Set<String> picNames = new HashSet<>();
        for(Setmeal setmeal : setmeals) {
            picNames.add(setmeal.getImg());
        }

        QiniuKey key = setmealService.getQiniuKey();
        QiniuUtils utils = new QiniuUtils(key.getAccessKey(), key.getSecretKey(), "xingxingli");
        Set<String> fileNames = utils.getAllFileNames("");
        List<String> trashFiles = new ArrayList<>();

        for(String fileName : fileNames) {
            if(!picNames.contains(fileName)) {
                trashFiles.add(fileName);
            }
        }
        String[] param = new String[trashFiles.size()];
        trashFiles.toArray(param);
        utils.deleteFiles(param);
    }
}
