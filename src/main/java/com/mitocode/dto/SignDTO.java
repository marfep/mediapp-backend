package com.mitocode.dto;

import com.mitocode.model.Patient;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SignDTO {

    @EqualsAndHashCode.Include
    private Integer idSign;

    @NotNull
    private Patient patient;

    @NotNull
    private LocalDateTime registerDate;

    @NotNull
    private String temperature;

    @NotNull
    private String pulse;

    @NotNull
    private String respiratoryRate;
}
