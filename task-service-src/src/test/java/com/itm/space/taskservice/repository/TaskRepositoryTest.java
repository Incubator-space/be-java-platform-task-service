package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @DisplayName("Тест на сохранение сущности")
    @Test
    void saveTask() {

        Task task = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "TaskEntity.json", Task.class);

        Task savedTask = taskRepository.save(task);
        UUID taskId = savedTask.getId();

        assertThat(taskId).isNotNull();
    }

    @DisplayName("Тест на получение сущности")
    @Test
    void findTask() {

        Task task = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "TaskEntity.json", Task.class);

        Task savedTask = taskRepository.save(task);
        UUID taskId = savedTask.getId();

        Task findTask = taskRepository.findTaskById(taskId);
        assertThat(findTask).isEqualTo(savedTask);
    }
}