package com.itm.space.taskservice.kafka.producer;

import com.itm.space.itmplatformcommonmodels.kafka.TaskEvent;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.service.TaskToTopicService;
import com.itm.space.taskservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskProducer implements EventProducer<TaskEvent> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final TaskToTopicService taskToTopicService;

    private final TaskMapper taskMapper;

    private final SecurityUtil securityUtil;

    @Value(value = "${spring.kafka.topic.task-events}")
    private String topic;

    @Override
    public void handle(TaskEvent event) {
        kafkaTemplate.send(topic, event);
    }

    public void sendingMessageToKafka (UUID taskId){

        UUID topicId = taskToTopicService.getTopicIdByTaskId(taskId);

        UUID studentId = Optional.ofNullable(securityUtil.getCurrentUserId())
                .orElseThrow(() -> new TaskException("Пользователь не найден", HttpStatus.NOT_FOUND));

        TaskEvent event = taskMapper.mapToTaskEvent(studentId, taskId, topicId);
        handle(event);
    }
}
