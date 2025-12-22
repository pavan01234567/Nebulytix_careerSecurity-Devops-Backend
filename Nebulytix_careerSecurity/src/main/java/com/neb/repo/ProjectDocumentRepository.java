package com.neb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.neb.entity.ProjectDocument;

public interface ProjectDocumentRepository
        extends JpaRepository<ProjectDocument, Long> {
}
