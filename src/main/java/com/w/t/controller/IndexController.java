package com.w.t.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * @Packagename com.w.t
 * @Classname IndexController
 * @Description
 * @Authors Mr.Wu
 * @Date 2020/10/12 09:07
 * @Version 1.0
 */
@Controller
public class IndexController {

//    @ResponseBody
    @RequestMapping("/index")
    public String toIndex(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("Authorization","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1MjE2IiwiaWF0IjoxNjIxMjIwMTkwLCJzdWIiOiJ7XCJyVHlwZVwiOlwiMFwiLFwidVR5cGVcIjpcIjBcIixcInBSSWRcIjpcIjY5NlwiLFwib3JnTGV2ZWxcIjpcIjFcIixcImlkXCI6XCI1MjE2XCIsXCJySWRcIjpcIjY5NlwiLFwib3JnSWRcIjpcIjc4MlwiLFwidVNvdXJjZVwiOlwiMFwifSIsImV4cCI6MTYyMTIyMzc5MH0.iQO82VFIwCT9IYJESGWxK1oYkO0q0X8xZkv3UQkgH0c");
        return "redirect:http://jimu.smartsteps.com/#/app/insight/crowdHeat";
    }
}
