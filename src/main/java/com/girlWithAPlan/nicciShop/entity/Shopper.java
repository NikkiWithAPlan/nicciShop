package com.girlWithAPlan.nicciShop.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "shopper")
@Builder
@RequiredArgsConstructor
@ToString
@Getter
@Setter
public class Shopper {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotNull
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @Past
    private LocalDate dateOfBirth;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 10, fraction = 4)
    private BigDecimal balance;
}
