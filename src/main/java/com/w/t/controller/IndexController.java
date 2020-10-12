package com.w.t.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @ResponseBody
    @RequestMapping("/index")
    public String toIndex() {
        return "Welcome !";
    }
}
