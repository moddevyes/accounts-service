package com.kinandcarta.ecommerce;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Entity
@Table(name = "address")
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 200, message = "Address must be between 2 and 200 characters")
    @Column(name = "street_address", columnDefinition = "varchar(200) default ''", nullable = false)
    private String address1;

    @Size(max = 200, message = "Address must be between 2 and 200 characters")
    @Column(name="second_address", columnDefinition = "varchar(200) default ''")
    private String address2;

    @NotNull
    @Size(min = 2, max = 200, message = "City must be between 2 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false)
    private String city;

    @NotNull
    @Size(max = 2, message = "State only allows an abbreviation of 2 characters")
    @Column(columnDefinition = "varchar(2) default ''", nullable = false)
    private String state;

    @Size(max = 200, message = "Province must be between 0 and 200 characters")
    @Column(columnDefinition = "varchar(200) null default ''")
    private String province;

    @Size(min = 5, max = 10, message = "Postal Code must be between 5 and 10 numbers")
    @Column(columnDefinition = "varchar(10) not null default ''", nullable = false)
    @NotNull private String postalCode;

    @Size(max = 200, message = "Country must be between 0 and 200 characters")
    @Column(columnDefinition = "varchar(100) not null default ''")
    @NotNull private String country;
}
