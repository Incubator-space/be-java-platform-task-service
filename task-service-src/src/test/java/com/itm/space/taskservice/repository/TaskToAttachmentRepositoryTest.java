package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.TaskToAttachment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;

class TaskToAttachmentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    TaskToAttachmentRepository taskToAttachmentRepository;

    @Test
    @DisplayName("Тест на сохранение сущности")
    @DataSet(value = "datasets/commentRepositoryTest/tasks.yml", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets.taskToAttachmentRepositoryTest/taskToAttachmentExpected.yml", ignoreCols = {"created"})
    void shouldCreateTaskToAttachment() {

        TaskToAttachment taskToAttachment = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY+
                "TaskToAttachmentEntity.json", TaskToAttachment.class);
        taskToAttachmentRepository.save(taskToAttachment);
    }

    @Test
    @DisplayName("Тест на получение сущности")
    @DataSet(value = {"datasets.taskToAttachmentRepositoryTest/taskToAttachment.yml", "datasets/commentRepositoryTest/tasks.yml"}, cleanAfter = true, cleanBefore = true)
    void shouldFindTaskToAttachmentById() {

        TaskToAttachment taskToAttachment = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY+
                "TaskToAttachmentEntity.json", TaskToAttachment.class);

        UUID id = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        TaskToAttachment foundTaskToAttachment = taskToAttachmentRepository.findById(id).get();

        assertThat(foundTaskToAttachment).usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(taskToAttachment);
    }

    @Test
    @DisplayName("Тест на обновление сущности")
    @DataSet(value = {"datasets.taskToAttachmentRepositoryTest/taskToAttachment.yml", "datasets/commentRepositoryTest/tasks.yml"},
            cleanAfter = true, cleanBefore = true)
        void shouldUpdateTaskToUser() {

        UUID taskToAttachmentId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

        TaskToAttachment findTaskToAttachment = taskToAttachmentRepository.findById(taskToAttachmentId).get();

        findTaskToAttachment.setAttachmentId(UUID.randomUUID());
        taskToAttachmentRepository.save(findTaskToAttachment);

        TaskToAttachment updatedTaskToAttachment = taskToAttachmentRepository.findById(taskToAttachmentId).get();
        LocalDateTime lastUpdated = updatedTaskToAttachment.getUpdated();
        LocalDateTime now = LocalDateTime.now();

        assertThat(lastUpdated).isNotNull();
        assertThat(lastUpdated).isBeforeOrEqualTo(now);
    }
}