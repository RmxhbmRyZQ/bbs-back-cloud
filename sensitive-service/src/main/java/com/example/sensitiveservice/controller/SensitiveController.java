package com.example.sensitiveservice.controller;

import com.example.sensitiveservice.domain.po.SensitiveWord;
import com.example.sensitiveservice.service.SensitiveWordService;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-ctrl")
public class SensitiveController {
    @PostMapping("/sensitiveWord")
    public Response<Map<String, SensitiveWord>> addSensitiveWord(@RequestBody SensitiveWord sensitiveWord) {
        return null;
    }

    @GetMapping("/sensitiveWord")
    public Response<Map<String, List<SensitiveWord>>> getSensitiveWords() {
        return null;
    }

    @PutMapping("/sensitiveWord")
    public Response<Map<String, SensitiveWord>> updateSensitiveWord(@RequestBody SensitiveWord sensitiveWord) {
        return null;
    }
}
