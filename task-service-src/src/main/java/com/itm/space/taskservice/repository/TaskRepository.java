package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Task findTaskById(UUID ID);

    @Query("SELECT t FROM Task t JOIN TaskToTopic tt ON t.id = tt.taskId WHERE tt.topicId = :topicId AND t.isArchived = false")
    List<Task> findAllByTopicIdAndNotArchived(UUID topicId);
}