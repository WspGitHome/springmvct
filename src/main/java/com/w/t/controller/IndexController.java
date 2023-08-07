package com.w.t.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.w.t.dao.HospitalDao;
import com.w.t.dao.ViewJgmlDao;
import com.w.t.entity.Hospital;
import com.w.t.entity.ViewJgml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Packagename com.w.t
 * @Classname IndexController
 * @Description
 * @Authors Mr.Wu
 * @Date 2020/10/12 09:07
 * @Version 1.0
 */
@Controller
@RequestMapping("index")
public class IndexController {

    @Autowired
    ViewJgmlDao viewJgmlDao;

    @Autowired
    HospitalDao hospitalDao;

    //    @ResponseBody
    @RequestMapping("/page")
    public String toIndex(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1MjE2IiwiaWF0IjoxNjIxMjIwMTkwLCJzdWIiOiJ7XCJyVHlwZVwiOlwiMFwiLFwidVR5cGVcIjpcIjBcIixcInBSSWRcIjpcIjY5NlwiLFwib3JnTGV2ZWxcIjpcIjFcIixcImlkXCI6XCI1MjE2XCIsXCJySWRcIjpcIjY5NlwiLFwib3JnSWRcIjpcIjc4MlwiLFwidVNvdXJjZVwiOlwiMFwifSIsImV4cCI6MTYyMTIyMzc5MH0.iQO82VFIwCT9IYJESGWxK1oYkO0q0X8xZkv3UQkgH0c");
        return "redirect:http://jimu.smartsteps.com/#/app/insight/crowdHeat";
    }

    @ResponseBody
    @RequestMapping("/getData")
    public String toIndex(@RequestParam Integer pageSize, @RequestParam(required = true) Integer pageNum) {
        QueryWrapper<ViewJgml> queryWrapper = new QueryWrapper<>();
        if (pageSize <= 0)
            pageSize = 100;
        if (pageNum <= 0)
            pageNum = 1;
        Integer pageIndex = (pageNum - 1) * pageSize;
        queryWrapper.last("limit " + pageSize + " offset " + pageIndex);
        List<ViewJgml> viewJgmls = viewJgmlDao.selectList(queryWrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("mesaage", "SUCCESS");
        result.put("data", viewJgmls);
        return JSONUtil.toJsonStr(result);
    }

    @ResponseBody
    @RequestMapping("/test")
    public String test() throws Exception {
        QueryWrapper<Hospital> queryWrapper = new QueryWrapper<>();
        queryWrapper.groupBy("userId").select("userId");
        List<Hospital> hospitals = hospitalDao.selectList(queryWrapper);
        Set<String> stringSet = new HashSet<>();
        List<String> peopleList = hospitals.stream().map(e -> e.getUserId()).collect(Collectors.toList());
        stringSet.addAll(peopleList);
        if (stringSet.size() != peopleList.size()) {
            throw new Exception("数据异常！");
        }
        for (int i = 0; i < peopleList.size(); i++) {
            List<String> toDealId = new ArrayList<>();
            QueryWrapper<Hospital> singlePersQuery = new QueryWrapper<>();
            singlePersQuery.eq("userId", peopleList.get(i)).orderByDesc("date");
            List<Hospital> singleList = hospitalDao.selectList(singlePersQuery);
            Map<Date, List<Hospital>> collect = singleList.stream().collect(Collectors.groupingBy(Hospital::getDate));
            List<Date> dayList = new ArrayList<>();
            dayList.addAll(collect.keySet());
            for (int j = 0; j < dayList.size(); j++) {
                boolean isBreak = false;
                if (isBreak) {
                    break;
                }
                List<Hospital> dayRecord = collect.get(dayList.get(j));
                List<String> addIds = new ArrayList<>();
                for (int t = 0; t < dayRecord.size(); t++) {
                    if (!dayRecord.get(t).getName().contains("新冠") && !dayRecord.get(t).getName().contains("病毒采样管")) {
                        System.out.println(dayRecord.get(t).getUserId() + dayRecord.get(t).getDate() + "当日有非新冠项目");
                        isBreak = true;
                        addIds.clear();
                        break;
                    } else {
                        addIds.add(dayRecord.get(t).getMainId());
                    }
                }
                toDealId.addAll(addIds);
            }
            UpdateWrapper<Hospital> updateWrapper = new UpdateWrapper();
            if (toDealId.isEmpty()) {
                updateWrapper.eq("userId", peopleList.get(i));
            } else {
                updateWrapper.eq("userId", peopleList.get(i)).notIn("mainId", toDealId);
            }
            hospitalDao.delete(updateWrapper);
        }
        return "DONE";
    }

    @ResponseBody
    @RequestMapping("/checkParam")
    public String check(HttpServletRequest httpServletRequest) {
        List<String> header = new ArrayList<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            header.add(s+"："+httpServletRequest.getHeader(s));
        }
        System.out.println("当前请求的header内容："+JSONUtil.toJsonStr(header));
        return JSONUtil.toJsonStr(header);
    }

    @ResponseBody
    @RequestMapping("/apimock")
    public String mockApi() {
     String jsonStr ="{\n" +
             "    \"Code\": 0,\n" +
             "    \"Message\": \"\",\n" +
             "    \"Occ\": \"202205111720\",\n" +
             "    \"LocalName\": \"西单大悦城\",\n" +
             "    \"Sum\": 128,\n" +
             "    \"Age\": [\n" +
             "        {\n" +
             "            \"Key\": \"04\",\n" +
             "            \"Value\": 2\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"05\",\n" +
             "            \"Value\": 28\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"06\",\n" +
             "            \"Value\": 24\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"07\",\n" +
             "            \"Value\": 32\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"08\",\n" +
             "            \"Value\": 20\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"09\",\n" +
             "            \"Value\": 8\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"10\",\n" +
             "            \"Value\": 6\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"11\",\n" +
             "            \"Value\": 2\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"12\",\n" +
             "            \"Value\": 2\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"13\",\n" +
             "            \"Value\": 2\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"14\",\n" +
             "            \"Value\": 2\n" +
             "        }\n" +
             "    ],\n" +
             "    \"Sex\": [\n" +
             "        {\n" +
             "            \"Key\": \"01\",\n" +
             "            \"Value\": 65\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"02\",\n" +
             "            \"Value\": 63\n" +
             "        }\n" +
             "    ],\n" +
             "    \"AgeSex\": [\n" +
             "        {\n" +
             "            \"Key\": \"01_01\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"01_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"02_01\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"02_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"03_01\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"03_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"04_01\",\n" +
             "            \"Value\": 1\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"04_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"05_01\",\n" +
             "            \"Value\": 13\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"05_02\",\n" +
             "            \"Value\": 12\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"06_01\",\n" +
             "            \"Value\": 18\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"06_02\",\n" +
             "            \"Value\": 8\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"07_01\",\n" +
             "            \"Value\": 17\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"07_02\",\n" +
             "            \"Value\": 15\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"08_01\",\n" +
             "            \"Value\": 8\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"08_02\",\n" +
             "            \"Value\": 11\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"09_01\",\n" +
             "            \"Value\": 1\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"09_02\",\n" +
             "            \"Value\": 6\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"10_01\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"10_02\",\n" +
             "            \"Value\": 6\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"11_01\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"11_02\",\n" +
             "            \"Value\": 1\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"12_01\",\n" +
             "            \"Value\": 1\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"12_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"13_01\",\n" +
             "            \"Value\": 1\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"13_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"14_01\",\n" +
             "            \"Value\": 1\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"14_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"15_01\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"15_02\",\n" +
             "            \"Value\": 0\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"other\",\n" +
             "            \"Value\": 8\n" +
             "        }\n" +
             "    ],\n" +
             "    \"WorkingResident\": null,\n" +
             "    \"MCC\": null,\n" +
             "    \"Province\": [\n" +
             "        {\n" +
             "            \"Key\": \"河北省\",\n" +
             "            \"Value\": 32,\n" +
             "            \"ExtKey\": \"130000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"北京市\",\n" +
             "            \"Value\": 22,\n" +
             "            \"ExtKey\": \"110000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"山西省\",\n" +
             "            \"Value\": 14,\n" +
             "            \"ExtKey\": \"140000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"山东省\",\n" +
             "            \"Value\": 10,\n" +
             "            \"ExtKey\": \"370000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"黑龙江省\",\n" +
             "            \"Value\": 9,\n" +
             "            \"ExtKey\": \"230000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"辽宁省\",\n" +
             "            \"Value\": 7,\n" +
             "            \"ExtKey\": \"210000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"内蒙古自治区\",\n" +
             "            \"Value\": 5,\n" +
             "            \"ExtKey\": \"150000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"福建省\",\n" +
             "            \"Value\": 4,\n" +
             "            \"ExtKey\": \"350000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"吉林省\",\n" +
             "            \"Value\": 4,\n" +
             "            \"ExtKey\": \"220000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"河南省\",\n" +
             "            \"Value\": 4,\n" +
             "            \"ExtKey\": \"410000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"甘肃省\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"620000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"四川省\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"510000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"安徽省\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"340000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"湖北省\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"420000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"云南省\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"530000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"天津市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"120000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"湖南省\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"430000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"江西省\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"360000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"新疆维吾尔自治区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"650000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        }\n" +
             "    ],\n" +
             "    \"City\": [\n" +
             "        {\n" +
             "            \"Key\": \"北京市\",\n" +
             "            \"Value\": 22,\n" +
             "            \"ExtKey\": \"110100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"邯郸市\",\n" +
             "            \"Value\": 12,\n" +
             "            \"ExtKey\": \"130400\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"保定市\",\n" +
             "            \"Value\": 7,\n" +
             "            \"ExtKey\": \"130600\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"邢台市\",\n" +
             "            \"Value\": 7,\n" +
             "            \"ExtKey\": \"130500\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"大庆市\",\n" +
             "            \"Value\": 4,\n" +
             "            \"ExtKey\": \"230600\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"朝阳市\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"211300\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"吕梁市\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"141100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"运城市\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"140800\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"德州市\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"371400\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"伊春市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"230700\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"莆田市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"350300\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"乌兰察布市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"150900\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"张家口市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130700\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"安阳市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"410500\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"沈阳市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"210100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"呼伦贝尔市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"150700\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"龙岩市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"350800\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"庆阳市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"621000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"芜湖市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"340200\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"通化市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"220500\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"淄博市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"370300\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"大同市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"140200\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"长春市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"220100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"潍坊市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"370700\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"十堰市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"420300\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"广元市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"510800\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"泰安市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"370900\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"临汾市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"141000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"平顶山市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"410400\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"石河子市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"659001\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"大连市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"210200\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"湘西土家族苗族自治州\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"433100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"唐山市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130200\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"黑河市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"231100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"滁州市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"341100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"曲靖市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"530300\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"晋中市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140700\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"济宁市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"370800\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"晋城市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140500\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"玉溪市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"530400\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"哈尔滨市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"230100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"辽阳市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"211000\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"阳泉市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140300\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"沧州市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130900\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"承德市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130800\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"德阳市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"510600\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"石家庄市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"忻州市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140900\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"九江市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"360400\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"佳木斯市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"230800\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"酒泉市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"620900\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"通辽市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"150500\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"天津市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"120100\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        }\n" +
             "    ],\n" +
             "    \"District\": [\n" +
             "        {\n" +
             "            \"Key\": \"西城区\",\n" +
             "            \"Value\": 8,\n" +
             "            \"ExtKey\": \"110102\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"磁县\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"130427\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"定州市\",\n" +
             "            \"Value\": 3,\n" +
             "            \"ExtKey\": \"130682\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"魏县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130434\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"丰台区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"110106\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"伊美区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"230717\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"东城区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"110101\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"翼城县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"141022\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"宁县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"621026\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"于洪区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"210114\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"奎文区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"370705\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"荔城区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"350304\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"无为市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"340281\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"滑县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"410526\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"峰峰矿区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130406\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"大兴区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"110115\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"闻喜县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"140823\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"涞源县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130630\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"内丘县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130523\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"肇州县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"230621\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"扎兰屯市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"150783\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"通州区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"110112\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"北票市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"211381\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"鸡泽县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130431\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"任县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130526\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"永定区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"350803\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"南和县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130527\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"张湾区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"420303\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"鲁山县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"410423\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"利州区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"510802\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"商都县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"150923\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"龙凤区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"230603\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"徐水区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130609\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"武安市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"130481\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"孝义市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"141181\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"淄川区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"370302\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"梅河口市\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"220581\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"宁津县\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"371422\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"房山区\",\n" +
             "            \"Value\": 2,\n" +
             "            \"ExtKey\": \"110111\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"兴隆县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130822\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"永顺县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"433127\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"双阳区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"220112\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"滦州市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130284\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"文水县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"141121\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"瓦房店市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"210281\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"敦煌市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"620982\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"华宁县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"530424\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"东平县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"370923\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"左云县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140226\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"忻府区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140902\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"榆树市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"220182\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"平城区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140213\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"长安区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130102\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"陵城区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"371403\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"永年区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130408\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"朝阳区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"110105\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"科尔沁区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"150502\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"五大连池市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"231182\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"建平县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"211322\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"顺义区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"110113\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"昔阳县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140724\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"宁阳县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"370921\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"怀来县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130730\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"石景山区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"110107\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"任城区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"370811\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"灯塔市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"211081\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"香坊区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"230110\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"沽源县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130724\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"河北区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"120105\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"永修县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"360425\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"盂县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140322\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"海淀区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"110108\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"同江市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"230881\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"南谯区\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"341103\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"平陆县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140829\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"中江县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"510623\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"石河子市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"659001\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"柏乡县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130524\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"高平市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"140581\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"任丘市\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"130982\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        },\n" +
             "        {\n" +
             "            \"Key\": \"陆良县\",\n" +
             "            \"Value\": 1,\n" +
             "            \"ExtKey\": \"530322\",\n" +
             "            \"ParentKey\": \"\"\n" +
             "        }\n" +
             "    ],\n" +
             "    \"DataConsumption\": null,\n" +
             "    \"MonthlySpend\": null,\n" +
             "    \"DeviceBrand\": null,\n" +
             "    \"Transportation\": null,\n" +
             "    \"Subway\": null,\n" +
             "    \"StaytimeHours\": null,\n" +
             "    \"StaytimeDays\": null,\n" +
             "    \"StaySumHours\": null,\n" +
             "    \"StaySumDays\": null,\n" +
             "    \"CrowdID\": 1\n" +
             "}";
        return JSONUtil.toJsonStr(jsonStr);
    }
}
