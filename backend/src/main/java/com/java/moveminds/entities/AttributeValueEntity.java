package com.java.moveminds.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "attribute_value")
public class AttributeValueEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", referencedColumnName = "id", nullable = false)
    private AttributeEntity attribute;
    @JsonIgnore
    @OneToMany(mappedBy = "attributeValue")
    private List<ProgramAttributeEntity> programAttributes;

}
