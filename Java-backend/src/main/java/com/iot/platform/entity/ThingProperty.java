package com.iot.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "thing_property", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "identifier"}))
public class ThingProperty {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "identifier", length = 50, nullable = false)
    private String identifier;

    @Column(name = "data_type", length = 20, nullable = false)
    private String dataType;

    @Column(name = "min_value", length = 50)
    private String minValue;

    @Column(name = "max_value", length = 50)
    private String maxValue;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "access_mode", length = 20, nullable = false)
    private String accessMode;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
