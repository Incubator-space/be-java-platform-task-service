package com.itm.space.taskservice.mapper;

import com.itm.space.taskservice.api.request.TaskSolutionDTO;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public interface SolutionToTaskMapper {

    SolutionToTask toSolutionToTask(UUID taskId, TaskSolutionDTO taskSolutionDTO);

    SolutionToTaskResponse toSolutionToTaskResponse(SolutionToTask solutionToTask);
}
