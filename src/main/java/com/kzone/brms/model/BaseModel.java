package com.kzone.brms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BaseModel implements Serializable {

    @Id
    @Column(name = "id",length = 36)
    @GenericGenerator(name = "uuid2",strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "created_date",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    @Column(name = "modified_date",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date modifiedDate;


}
