package com.girlWithAPlan.nicciShop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "address")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @NotBlank(message = "Address line is mandatory")
    private String addressLine;

    @NotNull
    @NotBlank(message = "City is mandatory")
    private String city;

    @NotNull
    @NotBlank(message = "Post code is mandatory")
    private String postCode;

    @NotNull
    @NotBlank(message = "Country is mandatory")
    private String country;

    @OneToOne(mappedBy = "address")
    private Shopper shopper;
}
