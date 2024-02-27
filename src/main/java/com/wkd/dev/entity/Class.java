package com.wkd.dev.entity;

import lombok.*;

@ToString
@NoArgsConstructor
@Getter @Setter
public class Class {

    private Long class_id;   // pk

    private int class_name; // 반이름  // 301,302,303,304,....

    private String department;  // 계열

    private int class_size;   // 학급 수용인원

    //private int grade_id;   // fk, Grade 테이블 grade_id를 외래키로 참조

    @Builder
    public Class(int class_name, String department, int class_size) {
        this.class_name = class_name;
        this.department = department;
        this.class_size = class_size;
    }
}
