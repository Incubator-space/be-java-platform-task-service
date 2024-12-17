package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.SolutionToTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SolutionToTaskRepositoryTest extends BaseIntegrationTest {

    @Autowired
    SolutionToTaskRepository solutionToTaskRepository;

    @Test
    @DisplayName("Тест на сохранение сущности")
    @DataSet(value = "datasets/commentRepositoryTest/tasks.yml",
            cleanAfter = true,
            cleanBefore = true
    )
    @ExpectedDataSet(
            value = "datasets.solutionToTaskRepositoryTest/solutionToTaskExpected.yml",
            ignoreCols = {"created"}
    )
    void shouldSaveSolutionToTask() {

        SolutionToTask solutionToTask = jsonParserUtil.getObjectFromJson(
                TASK_SERVICE_ENTITY + "SolutionToTaskEntity.json",
                SolutionToTask.class
        );
        SolutionToTask doneSolutionToTask = solutionToTaskRepository.save(solutionToTask);
        assertThat(doneSolutionToTask.getCreated()).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение сущности")
    @DataSet(value = {
            "datasets.solutionToTaskRepositoryTest/solutionToTask.yml",
            "datasets/commentRepositoryTest/tasks.yml"
            },
            cleanAfter = true,
            cleanBefore = true
    )
    void shouldFindSolutionToTask() {

        SolutionToTask solutionToTask = jsonParserUtil.getObjectFromJson(
                TASK_SERVICE_ENTITY + "SolutionToTaskEntity.json",
                SolutionToTask.class
        );

        UUID solutionToTaskId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        SolutionToTask findSolutionToTask = solutionToTaskRepository.findById(solutionToTaskId).get();

        assertThat(findSolutionToTask).usingRecursiveComparison()
                .ignoringFields("id", "created")
                .isEqualTo(solutionToTask);
    }

    @Test
    @DisplayName("Дополнительный тест на обновление сущности")
    @DataSet(value = {
            "datasets.solutionToTaskRepositoryTest/solutionToTask.yml",
            "datasets/commentRepositoryTest/tasks.yml"
            },
            cleanAfter = true,
            cleanBefore = true
    )
    void shouldUpdateSolutionToTask() {

        UUID solutionToTaskId = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

        SolutionToTask findSolutionToTask = solutionToTaskRepository.findById(solutionToTaskId).get();

        findSolutionToTask.setText("updatedText");
        solutionToTaskRepository.save(findSolutionToTask);

        SolutionToTask updatedSolutionToTask = solutionToTaskRepository.findById(solutionToTaskId).get();
        LocalDateTime lastUpdated = updatedSolutionToTask.getUpdated();
        LocalDateTime now = LocalDateTime.now();

        assertThat(lastUpdated).isNotNull();
        assertThat(lastUpdated).isBeforeOrEqualTo(now);
    }
}
