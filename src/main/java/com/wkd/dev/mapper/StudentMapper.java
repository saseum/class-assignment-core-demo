package com.wkd.dev.mapper;

import com.wkd.dev.entity.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    List<Student> selectAllStudents();

    int selectAllStudentsCount();

    void initializeScoreAndRank(Student student);
}
