package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.TaskToUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.time.LocalDateTime;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskToUserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    TaskToUserRepository taskToUserRepository;

    @Test
    @DisplayName("Тест на сохранение сущности")
    @DataSet(value = "datasets/commentRepositoryTest/tasks.yml", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets/commentRepositoryTest/taskToUserExpected.yml", ignoreCols = {"created"})
    void shouldSaveTaskToUser() {

        TaskToUser taskToUser = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "TaskToUserEntity.json",
                TaskToUser.class);
        TaskToUser doneTaskToUser = taskToUserRepository.save(taskToUser);
        assertThat(doneTaskToUser.getCreated()).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение сущности")
    @DataSet(value = {"datasets/commentRepositoryTest/taskToUser.yml", "datasets/commentRepositoryTest/tasks.yml"},
            cleanAfter = true, cleanBefore = true)
    void shouldFindTaskToUser() {

        TaskToUser taskToUser = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "TaskToUserEntity.json",
                TaskToUser.class);

        UUID taskToUserId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        TaskToUser findTaskToUser = taskToUserRepository.findById(taskToUserId).get();

        assertThat(findTaskToUser).usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(taskToUser);
    }

    @Test
    @DisplayName("Дополнительный тест на обновление сущности")
    @DataSet(value = {"datasets/commentRepositoryTest/taskToUser.yml", "datasets/commentRepositoryTest/tasks.yml"},
            cleanAfter = true, cleanBefore = true)
    void shouldUpdateTaskToUser() {

        UUID taskToUserId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

        TaskToUser findTaskToUser = taskToUserRepository.findById(taskToUserId).get();

        findTaskToUser.setUserId(UUID.randomUUID());
        taskToUserRepository.save(findTaskToUser);

        TaskToUser updatedTaskToUser = taskToUserRepository.findById(taskToUserId).get();
        LocalDateTime lastUpdated = updatedTaskToUser.getUpdated();
        LocalDateTime now = LocalDateTime.now();

        assertThat(lastUpdated).isNotNull();
        assertThat(lastUpdated).isBeforeOrEqualTo(now);
    }
}
