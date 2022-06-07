package com.keepseung.s3uploader.dto.multipart;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class S3UploadResultDto {

    // S3 URL
    private String url;

    // 파일 명
    private String name;

    // 원본 파일 사이즈 (바이트)
    private long size;

    @Builder
    public S3UploadResultDto(String url, String name, long size) {
        this.url = url;
        this.name = name;
        this.size = size;
    }
}
