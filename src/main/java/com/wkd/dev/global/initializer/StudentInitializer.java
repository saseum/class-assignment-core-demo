package com.wkd.dev.global.initializer;

import com.wkd.dev.entity.Student;
import com.wkd.dev.mapper.StudentMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

//@Component
public class StudentInitializer {

    private final StudentMapper studentMapper;
    private static double criterionScore = 100.1;
    private static int criterionRank = 0;
    private static String criterionDepartment = "human";

    public StudentInitializer(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    //@PostConstruct
    public void initStudentsScoreAndRank() {
        int count = studentMapper.selectAllStudentsCount(); // 239

        // 점수는 100 시작  : -0.1 씩
        // 등수는 1에서 시작 : +1 씩

        for(long i = 1; i <= count; i++) {
            criterionScore -= 0.1;
            criterionRank += 1;

            if(i % 64 == 0) {
                if("human".equals(criterionDepartment)) {
                    criterionDepartment = "nature";
                } else {
                    criterionDepartment = "human";
                }
            }

            studentMapper.initializeScoreAndRank(
                    Student.of(i, criterionDepartment, criterionScore, criterionRank)
            );
        }
    }
}
