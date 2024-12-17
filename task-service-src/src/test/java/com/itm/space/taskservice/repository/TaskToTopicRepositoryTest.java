package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.TaskToTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskToTopicRepositoryTest extends BaseIntegrationTest {

    @Autowired
    TaskToTopicRepository taskToTopicRepository;

    @Test
    @DisplayName("Тест на сохранение сущности")
    @DataSet(value = "datasets/commentRepositoryTest/tasks.yml", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets.taskToTopicRepositoryTest/taskToTopicExpected.yml", ignoreCols = {"created"})
    void shouldCreateTaskToTopic() {

       TaskToTopic taskToTopic = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY +
                "TaskToTopicEntity.json", TaskToTopic.class);
        TaskToTopic save = taskToTopicRepository.save(taskToTopic);
        assertThat(save.getCreated()).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение сущности")
    @DataSet(value = {"datasets.taskToTopicRepositoryTest/taskToTopic.yml", "datasets/commentRepositoryTest/tasks.yml"}, cleanAfter = true, cleanBefore = true)
    void shouldFindTaskToTopicById() {

        TaskToTopic taskToTopic = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY+
                "TaskToTopicEntity.json", TaskToTopic.class);

        UUID id = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        TaskToTopic foundTaskToTopic = taskToTopicRepository.findById(id).get();

        assertThat(foundTaskToTopic).usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(taskToTopic);
    }

    @Test
    @DisplayName("Тест на обновление сущности")
    @DataSet(value = {"datasets.taskToTopicRepositoryTest/taskToTopic.yml", "datasets/commentRepositoryTest/tasks.yml"},
            cleanAfter = true, cleanBefore = true)
    void shouldUpdateTaskToTopic() {

        UUID taskToTopicId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

        TaskToTopic taskToTopic = taskToTopicRepository.findById(taskToTopicId).get();
        taskToTopic.setTopicId(UUID.randomUUID());
        taskToTopicRepository.save(taskToTopic);

        TaskToTopic updatedTaskToAttachment = taskToTopicRepository.findById(taskToTopicId).get();
        LocalDateTime lastUpdated = updatedTaskToAttachment.getUpdated();
        LocalDateTime now = LocalDateTime.now();

        assertThat(lastUpdated).isBeforeOrEqualTo(now);
    }
}
