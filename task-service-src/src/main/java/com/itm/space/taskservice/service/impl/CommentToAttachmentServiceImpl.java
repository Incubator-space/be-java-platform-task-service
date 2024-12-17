package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.entity.CommentToAttachment;
import com.itm.space.taskservice.repository.CommentToAttachmentRepository;
import com.itm.space.taskservice.service.CommentToAttachmentService;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentToAttachmentServiceImpl implements CommentToAttachmentService {

    private final CommentToAttachmentRepository repository;

    @Override
    public void deleteAllByCommentId(@NonNull UUID commentId) {
        repository.deleteAllByCommentId(commentId);
    }

    @Override
    @Transactional
    public void updateCommentToAttachments(@NonNull UUID commentId, @NotEmpty List<UUID> listAttachmentsId) {
        repository.deleteAllByCommentId(commentId);
        List<CommentToAttachment> savedList = listAttachmentsId.stream()
                .map(attachmentId -> new CommentToAttachment(commentId, attachmentId))
                .toList();
        repository.saveAll(savedList);
    }
}
