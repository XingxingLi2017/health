package com.xing.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.xing.constant.MessageConstant;
import com.xing.constant.RedisConstant;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.entity.Result;
import com.xing.pojo.QiniuKey;
import com.xing.pojo.Setmeal;
import com.xing.service.SetmealService;
import com.xing.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    private SetmealService setmealService;
    @Autowired
    private JedisPool jedisPool;

    @PreAuthorize("hasAnyAuthority('SETMEAL_ADD', 'SETMEAL_EDIT')")
    @PostMapping("/upload")
    public Result uploadImage(@RequestParam("imgFile") MultipartFile imgFile) {
        QiniuKey key = setmealService.getQiniuKey();
        QiniuUtils utils = new QiniuUtils(key.getAccessKey(), key.getSecretKey(), "xingxingli");

        String originalFileName = imgFile.getOriginalFilename();
        int idx = originalFileName.lastIndexOf(".");
        String extention = originalFileName.substring(idx);
        String fileName = UUID.randomUUID().toString() + extention;

        Jedis jedis = null;
        try {
            utils.upload2Qiniu(imgFile.getBytes(), fileName);
            // put file name into redis databases
            jedis = jedisPool.getResource();
            jedis.sadd(RedisConstant.SETMEAL_PIC_RESOURCES, fileName);
        } catch (Exception e) {
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        finally {
            if(jedis != null) jedis.close();
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, fileName);
    }

    @PreAuthorize("hasAuthority('SETMEAL_DELETE')")
    @DeleteMapping("/deleteImgs")
    public Result deleteImages(String[] fileNames) {
        QiniuKey key = setmealService.getQiniuKey();
        QiniuUtils utils = new QiniuUtils(key.getAccessKey(), key.getSecretKey(), "xingxingli");
        utils.deleteFiles(fileNames);
        return new Result(true, null, null);
    }

    @PreAuthorize("hasAuthority('SETMEAL_ADD')")
    @PostMapping("/add")
    public Result addSetmeal(@RequestBody Map<String, Object> params) {
        Setmeal setmeal = JSON.parseObject(JSON.toJSONString(params.get("formData")) , Setmeal.class);
        Integer[] checkgroupIds = JSON.parseObject(JSON.toJSONString(params.get("checkgroupIds")), Integer[].class);
        try {
            setmealService.add(setmeal , checkgroupIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true , MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @PreAuthorize("hasAuthority('SETMEAL_EDIT')")
    @PutMapping("/update")
    public Result updateSetmeal(@RequestBody Map<String, Object> params) {
        Setmeal setmeal = JSON.parseObject(JSON.toJSONString(params.get("formData")) , Setmeal.class);
        Integer[] checkgroupIds = JSON.parseObject(JSON.toJSONString(params.get("checkgroupIds")), Integer[].class);
        try {
            setmealService.updateSetMeal(setmeal , checkgroupIds);
        } catch (Exception e) {
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true , MessageConstant.ADD_SETMEAL_SUCCESS);
    }

    @PreAuthorize("hasAuthority('SETMEAL_DELETE')")
    @DeleteMapping("/delete")
    public Result deleteSetmeal(int id) {
        try {
            setmealService.deleteById(id);
        } catch (Exception e) {
            return new Result(false, "");
        }
        return new Result(true, "");
    }


    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        PageResult pageResult = null;
        try {
            pageResult = setmealService.findPage(queryPageBean);
        } catch (Exception e) {
            return new PageResult(0L, new ArrayList<Setmeal>());
        }
        return pageResult;
    }


    @GetMapping("/findById")
    public Result findById(int id) {
        Setmeal setmeal = null;
        try {
            setmeal = setmealService.findById(id);
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS , setmeal);
    }

    @GetMapping("/findAll")
    public Result findAll(){
        try {
            return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS, setmealService.findAll());
        } catch (Exception e) {
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }

    @GetMapping("/findCheckgroupIdsBySetmealId")
    public Result findCheckgroupIdsBySetmealId(int id) {
        List<Integer> ret = null;
        try {
            ret = setmealService.findCheckgroupIdsBySetmealId(id);
        } catch (Exception e) {
            return new Result(false, MessageConstant.QUERY_SETMEAL_FAIL);
        }
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS , ret);
    }
}
