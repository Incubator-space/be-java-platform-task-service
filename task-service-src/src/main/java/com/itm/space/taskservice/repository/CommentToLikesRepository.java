package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.CommentToLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentToLikesRepository extends JpaRepository<CommentToLikes, UUID> {

    @Query("select c from CommentToLikes c where c.commentId= :commentId and c.createdBy= :userId")
    Optional<CommentToLikes> findByCommentIdAndCreatedBy(UUID commentId, UUID userId);

    @Query("SELECT COUNT(c) FROM CommentToLikes c WHERE c.commentId = :commentId")
    int countByCommentId(UUID commentId);
}
