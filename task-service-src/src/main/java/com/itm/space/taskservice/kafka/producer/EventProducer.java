package com.itm.space.taskservice.kafka.producer;

public interface EventProducer<T> {

    void handle(T event);
}
