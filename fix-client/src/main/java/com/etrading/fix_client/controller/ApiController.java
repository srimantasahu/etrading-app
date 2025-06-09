package com.etrading.fix_client.controller;

import com.etrading.fix_client.service.FixClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private FixClientService fixClientService;

    @GetMapping("/send")
    public String sendFixMessage(@RequestParam String fixMessage) {
        return fixClientService.sendMessage(fixMessage);
    }

}
