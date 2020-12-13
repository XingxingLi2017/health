package com.xing.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xing.constant.MessageConstant;
import com.xing.entity.Result;
import com.xing.service.MemberService;
import com.xing.service.ReportService;
import com.xing.service.SetmealService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;
    @Reference
    private ReportService reportService;

    @GetMapping("/getMemberReport")
    public Result getMemberReport(){
        /*
            Frontend Data Format
            {
                months:['yyyy.MM', 'yyyy.MM'],
                memberCount:[int, int , int]
            }
        */
        Calendar calendar = Calendar.getInstance();

        Map<String, Object> map = new HashMap<>();
        calendar.add(Calendar.MONTH, -12);
        List<String> months = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM");
        Date time = null;
        for(int i = 0 ; i < 12 ; i++) {
            calendar.add(Calendar.MONTH, 1);
            time = calendar.getTime();
            months.add(formatter.format(time));
        }
        map.put("months", months);
        try {
            map.put("memberCount", memberService.findMemberCountByMonths(months));
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
//        map.put("months", Arrays.asList("2020.05", "2020.06","2020.07","2020.08","2020.09"));
//        map.put("memberCount", Arrays.asList(100, 150, 180, 200, 300));
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    @GetMapping("/getSetmealReport")
    public Result getSetmealReport(){
        /*
            Frontend Data Format
            {
                setmealNames:['' , ''],
                setmealCount:[
                    {value: 34 , name:''},
                    {value: 34 , name:''}
                ]
            }
        */
        Map<String, Object> map = null;
        try {
            map = new HashMap<>();
            List<Map<String, Object>> setmealCount = setmealService.getSetmealReport();
            List<String> setmealNames = new ArrayList();
            for(Map<String,Object> setmeal : setmealCount) {
                setmealNames.add(setmeal.get("name").toString());
            }
            map.put("setmealNames", setmealNames);
            map.put("setmealCount", setmealCount);
        } catch (Exception e) {
            return new Result(false, MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
        return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, map);
    }

    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
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
        try {
            Map<String, Object> data = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @GetMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletResponse response , HttpServletRequest request) {
        try {
            Map<String, Object> map = reportService.getBusinessReportData();
            String reportDate = (String)map.get("reportDate");
            Integer todayNewMember = (Integer)map.get("todayNewMember");
            Integer totalMember = (Integer)map.get("totalMember");
            Integer thisWeekNewMember = (Integer)map.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer)map.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer)map.get("todayOrderNumber");
            Integer todayVisitsNumber = (Integer)map.get("todayVisitsNumber");
            Integer thisWeekOrderNumber = (Integer)map.get("thisWeekOrderNumber");
            Integer thisWeekVisitsNumber = (Integer)map.get("thisWeekVisitsNumber");
            Integer thisMonthOrderNumber = (Integer)map.get("thisMonthOrderNumber");
            Integer thisMonthVisitsNumber = (Integer)map.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>)map.get("hotSetmeal");

            // request.getServletContext() only exists after servlet 3.0
            ServletContext context = request.getSession().getServletContext();
            String realPath = context.getRealPath("template");
            realPath += File.separator + "report_template.xlsx";

            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(realPath)));
            Sheet sheet = workbook.getSheetAt(0);
            sheet.getRow(2).getCell(5).setCellValue(reportDate);

            sheet.getRow(4).getCell(5).setCellValue(todayNewMember);
            sheet.getRow(4).getCell(7).setCellValue(totalMember);

            sheet.getRow(5).getCell(5).setCellValue(thisWeekNewMember);
            sheet.getRow(5).getCell(7).setCellValue(thisMonthNewMember);

            sheet.getRow(7).getCell(5).setCellValue(todayOrderNumber);
            sheet.getRow(7).getCell(7).setCellValue(todayVisitsNumber);

            sheet.getRow(8).getCell(5).setCellValue(thisWeekOrderNumber);
            sheet.getRow(8).getCell(7).setCellValue(thisWeekVisitsNumber);

            sheet.getRow(9).getCell(5).setCellValue(thisMonthOrderNumber);
            sheet.getRow(9).getCell(7).setCellValue(thisMonthVisitsNumber);

            int rowNum = 12;
            for(Map row : hotSetmeal) {
                Row tmp = sheet.getRow(rowNum++);
                tmp.getCell(4).setCellValue(row.get("name").toString());
                tmp.getCell(5).setCellValue((Long)row.get("setmeal_count"));
                tmp.getCell(6).setCellValue(((BigDecimal)row.get("proportion")).doubleValue());
                tmp.getCell(7).setCellValue(((String)row.get("remark")));
            }
            response.setHeader("Content-Disposition", "attachment;filename=business_report.xlsx");
            response.setContentType("application/vnd.ms-excel");
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
            workbook.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/exportBusinessReport4Pdf")
    public Result exportBusinessReport4Pdf(HttpServletResponse response , HttpServletRequest request) {
        try {
            Map<String, Object> map = reportService.getBusinessReportData();
            List<Map> hotSetmeal = (List<Map>)map.get("hotSetmeal");
            String dirPath = request.getSession().getServletContext().getRealPath("template");
            String jrxmlPath = dirPath + File.separator + "report_template.jrxml";
            String jasperPath = dirPath + File.separator + "report_template.jasper";
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, map, new JRBeanCollectionDataSource(hotSetmeal));
            OutputStream os = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=business_report.pdf");
            JasperExportManager.exportReportToPdfStream(jasperPrint, os);
            os.flush();
            os.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
