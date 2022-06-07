package com.keepseung.s3uploader.dto.multipart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class S3UploadSignedUrlDto {
    // initiateUpload에서 얻어온 upload ID
    private String uploadId;

    // initiateUpload에서 얻어온 새 파일명
    private String fileName;

    // 업로드할 파일 조각 Number ( 1부터 시작 )
    private int partNumber;
}
