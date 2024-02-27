package com.wkd.dev.entity;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Student {

    private Long student_id;
    private String school_number;   // 학번
    private String student_name; // 학생명
    private String gender;  // 성별
    private String department;  // 계열: 인문계열/자연계열
    private double score;  // 점수
    private int rank;   // 전교등수
    private Long class_id;   // 학급 fk
    //private Long grade_id;   // 학년 fk

    public Student(String school_number) {
        this.school_number = school_number;
    }

    public static Student of(Long id, String department, double score, int rank) {
        return new Student(id, null, null, null, department, score, rank, null);
    }
}
