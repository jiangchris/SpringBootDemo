package com.doart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketController {

    @GetMapping("/testWebSocket")
    public String index2() {

        return "testWebSocket";
    }
}
