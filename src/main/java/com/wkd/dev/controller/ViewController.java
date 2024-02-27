package com.wkd.dev.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ViewController {

    @GetMapping(value =  {"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/feat/upload/excel")
    public String uploadExcel() {
        return "feature/uploadExcel";
    }

}
