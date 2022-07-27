package com.seanai.video.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: qizhiyuan
 * @CreateTime: 2022-07-20  09:41
 * @Description: index
 * @Version: 1.0
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(){
        return  "index";
    }


}
