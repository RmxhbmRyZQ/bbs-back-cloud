package com.example.monitorservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.Map;

@RestController
public class StatusController {

    @GetMapping("/systemInfo")
    public Response<Object> getSystemInfo() {
        return null;
    }

    @GetMapping("/forumInfo")
    public Response<Map<String, Integer>> getForumInfo() {
        return null;
    }
}
