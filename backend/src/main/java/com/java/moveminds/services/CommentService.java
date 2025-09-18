package com.java.moveminds.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.java.moveminds.dto.response.CommentResponse;

import java.security.Principal;

@Service
public interface CommentService {
    CommentResponse addComment(Principal principal, Integer programId, String comment);
    Page<CommentResponse> getComments(Integer programId, Pageable pageable);
}
