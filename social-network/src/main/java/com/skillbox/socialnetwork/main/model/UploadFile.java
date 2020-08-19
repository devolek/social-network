package com.skillbox.socialnetwork.main.model;

import com.skillbox.socialnetwork.main.model.enumerated.FileType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "files")
public class UploadFile {
    @Id
    private String id;

    private int ownerId;

    private String fileName;

    private String relativeFilePath;

    @Column(name = "raw_file_URL")
    private String rawFileURL;

    private String fileFormat;

    private long bytes;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @CreationTimestamp
    private Date createdAt;

}
