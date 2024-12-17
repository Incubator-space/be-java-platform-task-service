package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.TaskToAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskToAttachmentRepository extends JpaRepository<TaskToAttachment, UUID> {

    @Query("SELECT ta.attachmentId FROM TaskToAttachment ta WHERE ta.taskId = :taskId")
    List<UUID> findAttachmentUUIDsByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT ta FROM TaskToAttachment ta WHERE ta.taskId IN  :taskIds")
    List<TaskToAttachment> findTaskToAttachmentsById(@Param("taskIds") List<UUID> taskIds);

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END 
            FROM TaskToAttachment t 
            WHERE t.taskId = :taskId 
            AND t.attachmentId = :attachmentId
            """)
    boolean existsByTaskIdAndAttachmentId(UUID taskId, UUID attachmentId);

    @Modifying
    @Query(value = "DELETE FROM TaskToAttachment t WHERE t.taskId = :taskId")
    void deleteAllAttachmentsByTaskId(UUID taskId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TaskToAttachment t WHERE t.attachmentId = :attachmentId")
    boolean existsById(@Param("attachmentId") UUID attachmentId);

    @Modifying
    @Query(value = "DELETE FROM TaskToAttachment t WHERE t.attachmentId = :attachmentId")
    void deleteAttachment(@Param("attachmentId") UUID attachmentId);
}