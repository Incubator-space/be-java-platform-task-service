package com.itm.space.taskservice.mapper;

import com.itm.space.itmplatformcommonmodels.kafka.TaskEvent;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TaskMapper {

    Task taskRequestToTask(TaskRequest request);

    void updateTaskFromRequest(UpdateTaskRequest request, @MappingTarget Task task);

    @Mapping(source = "topicId", target = "topicId")
    @Mapping(source = "attachments", target = "attachments")
    @Mapping(source = "solutions", target = "solutions")
    TaskResponse toTaskResponse(Task task, UUID topicId, List<UUID> attachments, List<SolutionToTaskResponse> solutions);

    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "topicId", source = "topicId")
    TaskEvent mapToTaskEvent (UUID studentId, UUID taskId, UUID topicId);
}
