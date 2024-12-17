package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.CommentToAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface CommentToAttachmentRepository extends JpaRepository<CommentToAttachment, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CommentToAttachment cta WHERE cta.commentId = :commentId")
    void deleteAllByCommentId(UUID commentId);
}
