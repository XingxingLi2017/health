package com.xing.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xing.dao.MemberDao;
import com.xing.dao.OrderDao;
import com.xing.dao.SetmealDao;
import com.xing.service.ReportService;
import com.xing.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service(interfaceClass = com.xing.service.ReportService.class)
@Transactional
public class ReportServiceImpl implements ReportService {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private OrderDao orderDao;

    /***
     * Business Statistics Report
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getBusinessReportData() throws Exception {
        /*
            reportData:{
                    reportDate:null,
                    todayNewMember :0,
                    totalMember :0,
                    thisWeekNewMember :0,
                    thisMonthNewMember :0,
                    todayOrderNumber :0,
                    todayVisitsNumber :0,
                    thisWeekOrderNumber :0,
                    thisWeekVisitsNumber :0,
                    thisMonthOrderNumber :0,
                    thisMonthVisitsNumber :0,
                    hotSetmeal :[
                        {name:'Test Combination 1',setmeal_count:200,proportion:0.222},
                        {name:'Test Combination 2',setmeal_count:200,proportion:0.222}
                    ]
                }
        */
        Map<String,Object> map = new HashMap<>();
        String today = DateUtils.parseDate2String(new Date());
        map.put("reportDate", today);
        map.put("todayNewMember", memberDao.findMemberCountByDate(today));
        map.put("totalMember", memberDao.findMemberTotalCount());

        String dateOfMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        map.put("thisWeekNewMember", memberDao.findMemberCountAfterDate(dateOfMonday));

        String firstDayOfMonth = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        map.put("thisMonthNewMember", memberDao.findMemberCountAfterDate(firstDayOfMonth));

        map.put("todayOrderNumber", orderDao.findOrderCountByDate(today));
        map.put("todayVisitsNumber", orderDao.findVisitsCountByDate(today));

        map.put("thisWeekOrderNumber", orderDao.findOrderCountAfterDate(dateOfMonday));
        map.put("thisWeekVisitsNumber", orderDao.findVisitsCountAfterDate(dateOfMonday));

        map.put("thisMonthOrderNumber", orderDao.findOrderCountAfterDate(firstDayOfMonth));
        map.put("thisMonthVisitsNumber", orderDao.findVisitsCountAfterDate(firstDayOfMonth));
        map.put("hotSetmeal", orderDao.findHotSetmeal());

        return map;
    }
}
