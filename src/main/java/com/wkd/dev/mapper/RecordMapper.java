package com.wkd.dev.mapper;

import com.wkd.dev.entity.Record;
import com.wkd.dev.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper {

    List<Record> selectAllRecords();

    void insertRecord(Record record);

    int getCountByConditionalGroup(Map<String, Object> sbjGroup);

    List<Student> getStudentsByConditionalGroup(Map<String, Object> sbjGroup);
}
