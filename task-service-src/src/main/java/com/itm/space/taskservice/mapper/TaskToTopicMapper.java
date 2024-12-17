package com.itm.space.taskservice.mapper;

import com.itm.space.taskservice.entity.TaskToTopic;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public interface TaskToTopicMapper {

    TaskToTopic taskToTopic(UUID taskId, UUID topicId);
}