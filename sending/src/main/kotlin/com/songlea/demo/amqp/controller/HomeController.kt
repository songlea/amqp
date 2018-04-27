package com.songlea.demo.amqp.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 主页Controller
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/home")
class HomeController {

    @RequestMapping("/index")
    fun index() = "home"
}