package com.itm.space.taskservice.repository;

import com.itm.space.taskservice.entity.SolutionToTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SolutionToTaskRepository extends JpaRepository<SolutionToTask, UUID> {

    @Query("SELECT st FROM SolutionToTask st WHERE st.taskId = :taskId")
    List<SolutionToTask> findSolutionsByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT s FROM SolutionToTask s WHERE s.taskId IN :taskIds")
    List<SolutionToTask> findSolutionToTaskById(@Param("taskIds") List<UUID> taskIds);

    @Modifying
    @Query(value = "DELETE FROM SolutionToTask t WHERE t.taskId = :taskId")
    void deleteAllSolutionsByTaskId(UUID taskId);
}
