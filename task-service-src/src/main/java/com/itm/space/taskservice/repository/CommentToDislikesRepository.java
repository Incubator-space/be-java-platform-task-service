package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.CommentToDislikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentToDislikesRepository extends JpaRepository<CommentToDislikes, UUID> {

    @Query("select c from CommentToDislikes c where c.commentId= :commentId and c.createdBy= :userId")
    Optional<CommentToDislikes> findByCommentIdAndCreatedBy(UUID commentId, UUID userId);

    @Query("SELECT COUNT(c) FROM CommentToDislikes c WHERE c.commentId = :commentId")
    int countByCommentId(UUID commentId);
}
