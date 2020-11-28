package com.kzone.brms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "rule_set")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleSet extends BaseModel{

    @Column(name = "rule_set_name",nullable = false,unique = true)
    private String name;

    @Column(name = "package",nullable = false,unique = true)
    private String packageName;

    @Column(name = "description",nullable = false)
    private String description;

}
