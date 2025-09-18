package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.dto.response.CommentResponse;
import com.java.moveminds.services.CommentService;

import java.security.Principal;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Endpoint for getting comments
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @RequestParam Integer programId,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        return ResponseEntity.ok(this.commentService.getComments(programId, PageRequest.of(page, size)));
    }

    // Endpoint for adding comment
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            Principal principal,
            @RequestParam Integer programId,
            @RequestBody String content) {
        CommentResponse commentResponse = commentService.addComment(principal, programId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }
}
