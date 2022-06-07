package com.keepseung.s3uploader.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class S3UploadDto {

    // S3 UploadId
    private String uploadId;
    // 서버에서 생성한 파일 이름
    private String fileName;

    public S3UploadDto(String uploadId, String fileName) {
        this.uploadId = uploadId;
        this.fileName = fileName;
    }
}
