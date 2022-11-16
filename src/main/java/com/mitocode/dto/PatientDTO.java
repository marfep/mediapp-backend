package com.mitocode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PatientDTO {

    private Integer idPatient;

    @NotNull
    @Size(min = 3, message = "{firstname.size}")
    //@JsonProperty(value = "nombre")
    private String firstName;

    @NotEmpty
    @Size(min = 3, message = "{lastname.size}")
    private String lastName;
    private String dni;
    private String address;
    private String phone;
    private String email;
}
