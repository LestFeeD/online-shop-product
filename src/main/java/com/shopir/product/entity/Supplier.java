package com.shopir.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupplier;
    private String nameSupplier;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<MailSupplier> mailSuppliers;
}
