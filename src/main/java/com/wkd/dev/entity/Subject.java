package com.wkd.dev.entity;

import lombok.*;

@ToString
@NoArgsConstructor
@Getter @Setter
public class Subject {

    private Long subject_id;
    private String subject_name;

    /*
    private String sbj_area;
    private String sbj_group;
    private String sbj_type;
    private int unit;
     */

    public Subject(String subject_name) {
        this.subject_name = subject_name;
    }
}
