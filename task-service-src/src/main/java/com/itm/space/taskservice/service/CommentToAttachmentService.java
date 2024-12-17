package com.itm.space.taskservice.service;

import java.util.List;
import java.util.UUID;

public interface CommentToAttachmentService {

    void deleteAllByCommentId(UUID commentId);

    void updateCommentToAttachments(UUID commentId, List<UUID> listAttachmentsId);

}
