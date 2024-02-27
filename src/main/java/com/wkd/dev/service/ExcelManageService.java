package com.wkd.dev.service;

import com.wkd.dev.mapper.RecordMapper;
import com.wkd.dev.mapper.StudentMapper;
import com.wkd.dev.mapper.SubjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExcelManageService {

    private final RecordMapper recordMapper;
    private final StudentMapper studentMapper;
    private final SubjectMapper subjectMapper;

    public boolean upload(MultipartFile file) throws IOException {

        Map<String, Object> readResult = readFile(file);

        List<String> subjects = (List<String>) readResult.get("subjects");
        List<Map<String, Object>> students = (List<Map<String, Object>>) readResult.get("students");

        // TODO: Insert excel data Into Database

        return !readResult.isEmpty();
    }

    private Map<String, Object> readFile(MultipartFile file) throws IOException {

        String fileExtns = FilenameUtils.getExtension(file.getOriginalFilename());

        Workbook workbook = null;

        InputStream is = file.getInputStream();

        if (fileExtns.equals("xls")) workbook = new HSSFWorkbook(is);
        else workbook = new XSSFWorkbook(is);

        Sheet workSheet = workbook.getSheetAt(0);
        int rowCount = workSheet.getPhysicalNumberOfRows();
        List<String> subjects = new ArrayList<>();
        List<Map<String, Object>> students = new ArrayList<>();

        for(int i = 0; i < rowCount; i++) {
            Row row = workSheet.getRow(i);

            if(!ObjectUtils.isEmpty(row)) {
                // 학생 Map
                Map<String, Object> std = new HashMap<>();
                List<String> sbjList = new ArrayList<>();

                for(int j = 0; j < 32; j++) {
                    Cell cell = row.getCell(j);

                    if(i == 0) { // 첫번째 행(과목 정보)
                        if(j > 1) {
                            subjects.add(cell.getStringCellValue());
                        }
                    } else { // 두번째 행부터(학생 정보)

                        if(j == 0) {
                            std.put("school_number", String.valueOf((int) cell.getNumericCellValue()));
                        } else if(j > 1){
                            sbjList.add(cell.getNumericCellValue() == 1.0 ? "1" : "0");
                        }
                    }
                }
                std.put("sbjList", sbjList);
                students.add(std);
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("students", students);
        resultMap.put("subjects", subjects);

        is.close();

        return resultMap;
    }
}
