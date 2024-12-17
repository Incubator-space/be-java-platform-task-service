package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.TaskToTopic;

import java.util.List;
import java.util.UUID;

public interface TaskToTopicService {

    List<TaskResponse> getTasksToTopicId(UUID topicId);

    UUID getTopicIdByTaskId(UUID taskId);

    void save(TaskToTopic taskToTopic);

    boolean existsByTopicId(UUID topicId);
}
