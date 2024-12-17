package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.entity.TaskToAttachment;
import com.itm.space.taskservice.mapper.TaskToAttachmentMapper;
import com.itm.space.taskservice.repository.TaskToAttachmentRepository;
import com.itm.space.taskservice.service.TaskToAttachmentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskToAttachmentServiceImpl implements TaskToAttachmentService {

    private final TaskToAttachmentRepository taskToAttachmentRepository;

    private final TaskToAttachmentMapper taskToAttachmentMapper;

    @Override
    public List<UUID> getAttachmentUUIDsByTaskId(UUID taskId) {

        List<UUID> attachment = taskToAttachmentRepository.findAttachmentUUIDsByTaskId(taskId);

        return attachment;
    }

    @Override
    public boolean existsByTaskIdAndAttachmentId(UUID taskId, UUID attachmentId) {
        return taskToAttachmentRepository.existsByTaskIdAndAttachmentId(taskId, attachmentId);
    }

    @Override
    public Map<UUID, List<UUID>> findTaskToAttachmentsById(List<UUID> taskIds) {
        return taskToAttachmentRepository.findTaskToAttachmentsById(taskIds)
                .stream()
                .collect(Collectors.groupingBy(
                        TaskToAttachment::getTaskId,
                        Collectors.mapping(TaskToAttachment::getAttachmentId, Collectors.toList())
                ));
    }

    @Transactional
    @Override
    public void saveAll(List<TaskToAttachment> attachmentList) {
        taskToAttachmentRepository.saveAll(attachmentList);
    }

    @Override
    @Transactional
    public void deleteAllAttachmentsByTaskId(UUID taskId) {
        taskToAttachmentRepository.deleteAllAttachmentsByTaskId(taskId);
    }

    @Override
    @Transactional
    public List<UUID> updateAttachmentListById(@NonNull UpdateTaskRequest request, @NonNull UUID taskId) {
        if (request.getAttachments() != null) {
            deleteAllAttachmentsByTaskId(taskId);

            saveAll(request.getAttachments().stream()
                    .map(attachmentId ->
                            taskToAttachmentMapper.taskToAttachment(taskId, attachmentId))
                    .toList()
            );
        }

        List<UUID> updatedAttachmentListById = getAttachmentUUIDsByTaskId(taskId);

        return updatedAttachmentListById;
    }

    @Transactional
    @Override
    public boolean existsById(UUID attachmentId) {
        return taskToAttachmentRepository.existsById(attachmentId);
    }
}