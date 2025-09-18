package com.java.moveminds.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.java.moveminds.dto.response.CommentResponse;
import com.java.moveminds.entities.CommentEntity;
import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.repositories.CommentEntityRepository;
import com.java.moveminds.repositories.FitnessProgramEntityRepository;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.CommentService;
import com.java.moveminds.services.LogService;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentEntityRepository commentRepository;
    private final UserEntityRepository userRepository;
    private final FitnessProgramEntityRepository programRepository;
    private final LogService logService;

    @Override
    public CommentResponse addComment(Principal principal, Integer programId, String comment) {
        String username = principal.getName();
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        Optional<FitnessProgramEntity> programOpt = programRepository.findById(programId);

        if (userOpt.isPresent() && programOpt.isPresent()) {
            UserEntity user = userOpt.get();
            FitnessProgramEntity program = programOpt.get();

            CommentEntity newComment = new CommentEntity();
            newComment.setContent(comment);
            newComment.setPostedAt(Timestamp.valueOf(LocalDateTime.now()));
            newComment.setUser(user);
            newComment.setFitnessProgram(program);

            commentRepository.saveAndFlush(newComment);

            logService.log(principal, "Adding a comment");

            return new CommentResponse(
                    newComment.getId(),
                    user.getId(),
                    user.getUsername(),
                    user.getAvatarUrl(),
                    newComment.getContent(),
                    newComment.getPostedAt().toLocalDateTime()
            );
        }

        throw new RuntimeException("User or program not found.");
    }

    @Override
    public Page<CommentResponse> getComments(Integer programId, Pageable pageable) {
        Page<CommentEntity> commentPage = commentRepository.findAllByFitnessProgramId(programId, pageable);

        logService.log(null, "View comments");
        return commentPage.map(comment ->
                new CommentResponse(
                        comment.getId(),
                        comment.getUser().getId(),
                        this.getUsername(comment.getUser()),
                        comment.getUser().getAvatarUrl(),
                        comment.getContent(),
                        comment.getPostedAt().toLocalDateTime()
                ));
    }

    private String getUsername(UserEntity user) {
        if (user == null) {
            return "";
        }
        String displayName;
        if (user.getFirstName() != null && user.getLastName() != null) {
            displayName = user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            displayName = user.getFirstName();
        } else if (user.getLastName() != null) {
            displayName = user.getLastName();
        } else {
            displayName = user.getUsername();
        }

        return displayName;
    }
}
