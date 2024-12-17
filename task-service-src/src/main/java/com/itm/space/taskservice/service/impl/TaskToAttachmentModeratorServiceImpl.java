package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import com.itm.space.taskservice.api.response.TaskToAttachmentDeleteResponse;
import com.itm.space.taskservice.api.response.TaskToAttachmentModeratorResponse;
import com.itm.space.taskservice.entity.TaskToAttachment;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskToAttachmentModeratorMapper;
import com.itm.space.taskservice.repository.TaskToAttachmentRepository;
import com.itm.space.taskservice.service.TaskService;
import com.itm.space.taskservice.service.TaskToAttachmentModeratorService;
import com.itm.space.taskservice.service.TaskToAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskToAttachmentModeratorServiceImpl implements TaskToAttachmentModeratorService {

    private final TaskToAttachmentRepository taskToAttachmentRepository;

    private final TaskService taskService;

    private final TaskToAttachmentService attachmentService;

    private final TaskToAttachmentModeratorMapper mapper;

    @Transactional
    public TaskToAttachmentModeratorResponse saveTaskToAttachment(TaskToAttachmentModeratorRequest request) {
        UUID taskId = request.getTaskId();
        UUID attachmentId = request.getAttachmentId();

        if (!taskService.existsById(taskId)) {
            throw new TaskException("Задача не найдена", HttpStatus.NOT_FOUND);
        }
        if (attachmentService.existsByTaskIdAndAttachmentId(taskId, attachmentId)) {
            throw new TaskException("Неправильные параметры запроса", HttpStatus.BAD_REQUEST);
        }
        TaskToAttachment attachment = mapper.toEntity(request);
        taskToAttachmentRepository.save(attachment);

        return mapper.toAttachmentResponse(request);
    }

    @Transactional
    @Override
    public TaskToAttachmentDeleteResponse deleteAttachmentToTask(UUID attachmentId) {
        log.info("Удаление аттачмента {}", attachmentId);
        if (!attachmentService.existsById(attachmentId)) {
            throw new TaskException("Аттачмент для удаления не найден", HttpStatus.NOT_FOUND);
        }
        taskToAttachmentRepository.deleteAttachment(attachmentId);
         return mapper.toEntity(attachmentId);

    }
}
