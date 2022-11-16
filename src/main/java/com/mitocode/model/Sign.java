package com.mitocode.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Sign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSign;

    @ManyToOne
    @JoinColumn(name = "id_patient", nullable = false, foreignKey = @ForeignKey(name = "FK_SIGN_PATIENT"))
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime registerDate;

    @Column(nullable = false, length = 150)
    private String temperature;

    @Column(nullable = false, length = 150)
    private String pulse;

    @Column(nullable = false, length = 150)
    private String respiratoryRate;
}
