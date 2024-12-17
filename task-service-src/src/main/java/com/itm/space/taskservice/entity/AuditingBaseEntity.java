package com.itm.space.taskservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditingBaseEntity extends BaseEntity {

    @CreatedDate
    @Column(name = "created", nullable = false)
    protected LocalDateTime created;

    @LastModifiedDate
    @Column(name = "updated")
    protected LocalDateTime updated;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    protected UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    protected UUID updatedBy;
}
