package com.mitocode.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IdClass(ConsultExamPK.class)
@Entity
public class ConsultExam {

    @Id
    private Consult consult;

    @Id
    private Exam exam;
}
