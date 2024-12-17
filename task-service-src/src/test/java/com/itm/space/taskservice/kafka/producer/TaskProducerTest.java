package com.itm.space.taskservice.kafka.producer;

import com.itm.space.itmplatformcommonmodels.kafka.TaskEvent;
import com.itm.space.taskservice.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class TaskProducerTest extends BaseIntegrationTest {

    @Autowired
    private TaskProducer taskProducer;

    @Value(value = "${spring.kafka.topic.task-events}")
    private String topic;

    @Test
    @DisplayName("Проверка отправления и получения сообщений")
    void shouldSendMessage() {
        TaskEvent event = jsonParserUtil.getObjectFromJson("json/kafka/producer/TaskProducer.json",
                TaskEvent.class);

        taskProducer.handle(event);
        testConsumerService.consumeAndValidate(topic, event);
    }
    
    @Test
    @DisplayName("Проверка отправления и получения сообщений на завершение задачи студентом")
    void shouldSendMsgProducerCompletionTask (){
        TaskEvent event = jsonParserUtil
                .getObjectFromJson("json/kafka/producer/TaskEventProducerForStudentСompletion.json", TaskEvent.class);
        taskProducer.handle(event);
        testConsumerService.consumeAndValidate(topic, event);
    }
}
