package com.itm.space.taskservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Entity
@Builder
@Table(name = "comments")
public class Comment extends AuditingBaseEntity {

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "actual", nullable = false, columnDefinition = "boolean default true")
    private boolean actual = true;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "likes", columnDefinition = "integer default 0")
    private int likes = 0;

    @Column(name = "dislikes", columnDefinition = "integer default 0")
    private int dislikes = 0;

    @Column(name = "moderated", nullable = false, columnDefinition = "boolean default false")
    private boolean moderated = false;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column(name = "parent_id")
    private UUID parentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
