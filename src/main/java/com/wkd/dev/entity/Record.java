package com.wkd.dev.entity;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
public class Record {

    private Long record_id;

    private String school_number;

    private String subject_name;

    @Builder
    public Record(String school_number, String subject_name) {
        this.school_number = school_number;
        this.subject_name = subject_name;
    }
}
