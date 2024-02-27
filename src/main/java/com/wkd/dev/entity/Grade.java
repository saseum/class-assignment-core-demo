package com.wkd.dev.entity;

import lombok.*;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Grade {

    private Long grade_id;   // pk

    private int grade_name; // 학년 이름. ex) 1학년, 2학년, 3학년
}
