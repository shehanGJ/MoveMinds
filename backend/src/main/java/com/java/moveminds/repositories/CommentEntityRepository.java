package com.java.moveminds.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.CommentEntity;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {
    Page<CommentEntity> findAllByFitnessProgramId(Integer fitnessProgramId, Pageable pageable);
}
