package com.shopir.product.entity;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail_supplier")
public class MailSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMailSupplier;
    private String nameMailSupplier;

    @ManyToOne
    @JoinColumn(name = "id_supplier")
    private Supplier supplier;
}
