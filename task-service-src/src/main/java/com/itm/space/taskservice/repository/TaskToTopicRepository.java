package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.TaskToTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskToTopicRepository extends JpaRepository<TaskToTopic, UUID> {

    @Query("SELECT t.topicId FROM TaskToTopic t WHERE t.taskId = :taskId")
    Optional<UUID> findTopicIdByTaskId(@Param("taskId") UUID taskId);

    boolean existsByTopicId(UUID topicId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TaskToTopic t WHERE t.topicId = :topicId")
    boolean existsById(@Param("topicId") UUID topicId);
}