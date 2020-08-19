package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<UploadFile, Integer> {
    UploadFile findById (String id);
}
