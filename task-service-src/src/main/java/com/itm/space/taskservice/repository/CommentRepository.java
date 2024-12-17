package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("select c from Comment c where c.taskId = :taskId and c.moderated = true order by c.id")
    Page<Comment> findModeratedCommentsByTaskId(UUID taskId, Pageable pageable);

    Optional<Comment> findCommentById(UUID commentId);
}
