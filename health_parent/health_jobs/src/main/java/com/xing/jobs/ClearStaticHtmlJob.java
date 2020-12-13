package com.xing.jobs;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.pojo.Setmeal;
import com.xing.service.SetmealService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClearStaticHtmlJob {
    @Reference
    private SetmealService setmealService;

    @Value("${out_put_path}")
    private String templateOutputPath;

    /***
     * clear trash mobile_setmeal_detail_id.html files in health_mobile
     */
    public void clearMobileSetmealHtml(){
        File dir = new File(templateOutputPath);
        if(dir.exists()) {
            // get all the existing setmeal ids
            List<Setmeal> setmeals = setmealService.findAll();
            Set<Integer> ids = getSetmealsIds(setmeals);
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
                int id = getIdFromName(file.getName());
                // delete file if it is not in the existing setmeal id set
                if(!ids.contains(id)) {
                    if(file.delete()) {
                        System.out.println("ClearStaticHtmlJob.clearMobileSetmealHtml() delete "+file.getName()+" successfully!");
                    } else {
                        System.out.println("ClearStaticHtmlJob.clearMobileSetmealHtml() delete "+file.getName()+" fail!");
                    }

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
    private Set<Integer> getSetmealsIds(List<Setmeal> list) {
        Set<Integer> ids = new HashSet<>();
        for(Setmeal setmeal : list) {
            ids.add(setmeal.getId());
        }
        return ids;
    }
}
