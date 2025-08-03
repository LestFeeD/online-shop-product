package com.shopir.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "characteristic")
public class Characteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCharacteristic;
    private String name;

    @OneToMany(mappedBy = "characteristic", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ProductCharacteristics> productCharacteristics;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Characteristic that = (Characteristic) o;
        return Objects.equals(idCharacteristic, that.idCharacteristic) && Objects.equals(name, that.name) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCharacteristic, name);
    }

    @Override
    public String toString() {
        return "Characteristics{" +
                "idCharacteristics=" + idCharacteristic +
                ", name='" + name + '\'' +
                '}';
    }
}
