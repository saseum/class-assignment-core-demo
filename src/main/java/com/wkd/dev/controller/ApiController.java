package com.wkd.dev.controller;

import com.wkd.dev.service.ExcelManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ApiController {

    private final ExcelManageService excelManageService;

    @PostMapping("/upload/excel")
    public ResponseEntity<Boolean> upload(@RequestParam("file") MultipartFile file) {

        boolean result = false;

        try {
            result = excelManageService.upload(file);

        } catch (IOException e) {
            log.debug(e.getMessage());
        }

        log.info("==== API 연결 END ====");

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
