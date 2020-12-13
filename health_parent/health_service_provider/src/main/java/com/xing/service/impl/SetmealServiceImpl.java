package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xing.constant.RedisConstant;
import com.xing.dao.CheckGroupDao;
import com.xing.dao.SetmealDao;
import com.xing.entity.PageResult;
import com.xing.entity.QueryPageBean;
import com.xing.pojo.QiniuKey;
import com.xing.pojo.Setmeal;
import com.xing.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.*;

@Service(interfaceClass = com.xing.service.SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private CheckGroupDao checkGroupDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Value("${out_put_path}")
    private String templateOutputPath;


    /***
     * add setmeal and associate check groups with setmeal
     * @param setmeal
     * @param checkgroupIds
     */
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        setSetmealAndCheckGroup(setmeal.getId(), checkgroupIds);
//        addPicName2Redis(setmeal.getImg(), true);

        // generate mobile static page after DML operations
        generateMobileStaticHtml();
    }

    /***
     * generate mobile static web pages using FreeMarker
     */
    public void generateMobileStaticHtml(){
        List<Setmeal> list = findAll();
        generateMobileSetmealListHtml(list);
        generateMobileSetmealDetailHtml(list);
    }

    /***
     * generate setmeal html page, one page
     * @param list
     */
    private void generateMobileSetmealListHtml(List<Setmeal> list) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("setmealList" , list);
        this.generateHtml("mobile_setmeal.ftl" , "mobile_setmeal.html", dataMap);
    }

    /***
     * generate setmeal_detail.html , every setmeal have a page for detail info
     * @param list
     */
    private void generateMobileSetmealDetailHtml(List<Setmeal> list) {
        for(Setmeal setmeal : list) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("setmeal" , this.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl" ,
                    "mobile_setmeal_detail_"+setmeal.getId()+".html",
                    dataMap);
        }
    }

    /***
     * use FreeMarkerConfig to get Template and bind dataMap with template
     * generate static html files to health_mobile modules
     * @param templateName
     * @param htmlPageName
     * @param dataMap
     */
    private void generateHtml(String templateName, String htmlPageName, Map<String, Object> dataMap) {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        File docFile = new File(templateOutputPath + "/" + htmlPageName);
        try(Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile), "UTF-8"))) {
            Template template = configuration.getTemplate(templateName);
            template.process(dataMap, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        int currentPage = queryPageBean.getCurrentPage();
        int pageSize = queryPageBean.getPageSize();
        String query = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.findByCondition(query);
        PageResult ret = new PageResult( page.getTotal(),page.getResult());
        return ret;
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    @Override
    public Setmeal findByIdWithoutDetail(Integer id) {
        return setmealDao.findByIdWithoutDetail(id);
    }

    @Override
    public List<Map<String, Object>> getSetmealReport() {
        return setmealDao.getSetmealReport();
    }

    @Override
    public QiniuKey getQiniuKey() {
        return setmealDao.getQiniuKeyById(1);
    }

    @Override
    public void deleteById(int id) {
        setmealDao.deleteAssocication(id);
        Setmeal setmeal = findById(id);
        if(setmeal != null) {
//            removePicNameFromRedisDBCache(setmeal.getImg());
//            addPicName2Redis(setmeal.getImg() , false);
//            clearMobileSetmealDetailHtmlById(setmeal.getId());
            setmealDao.deleteById(id);
            generateMobileStaticHtml();
        }

    }
    /***
     * clear trash mobile_setmeal_detail_id.html files in health_mobile
     */
    public void clearMobileSetmealDetailHtmlById(int id){
        File dir = new File(templateOutputPath);
        if(dir.exists()) {
            File[] list = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.startsWith("mobile_setmeal_detail")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            for(File file : list) {
                int setmealId = getIdFromName(file.getName());
                if(setmealId == id) {
                    if(file.delete()) {
                        System.out.println("ClearStaticHtmlJob.clearMobileSetmealHtml() delete "+file.getName()+" successfully!");
                    } else {
                        System.out.println("ClearStaticHtmlJob.clearMobileSetmealHtml() delete "+file.getName()+" fail!");
                    }
                    break;
                }
            }
        }
    }
    private Integer getIdFromName(String name) {
        int idx = name.lastIndexOf(".");
        name = name.substring(0, idx);
        String[] units = name.split("_");
        return Integer.parseInt(units[3]);
    }

    @Override
    public List<Integer> findCheckgroupIdsBySetmealId(int id) {
        return setmealDao.findCheckgroupIdsBySetmealId(id);
    }

    @Override
    public void updateSetMeal(Setmeal setmeal , Integer[] checkgroupIds) {
        // delete relationship and add old img name into Redis cache
        setmealDao.deleteAssocication(setmeal.getId());
//        Setmeal originSetmeal = findById(setmeal.getId());
//        addPicName2Redis(originSetmeal.getImg() , false);

        setmealDao.updateSetMeal(setmeal);
        setSetmealAndCheckGroup(setmeal.getId() , checkgroupIds);

        generateMobileStaticHtml();
    }

    private void addPicName2Redis(String picName, boolean inDB) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if(inDB) {
                jedis.sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, picName);
            } else {
                jedis.sadd(RedisConstant.SETMEAL_PIC_RESOURCES, picName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null ){
                jedis.close();
            }
        }
    }
    private void removePicNameFromRedisDBCache(String picName){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES, picName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null ){
                jedis.close();
            }
        }
    }
    private void setSetmealAndCheckGroup(int setmealId, Integer[] checkgroupIds){
        if(checkgroupIds != null && checkgroupIds.length > 0) {
            Map<String , Integer> map = null;
            for(Integer id : checkgroupIds) {
                map = new HashMap<>();
                map.put("checkgroupId" , id);
                map.put("setmealId", setmealId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }
}
