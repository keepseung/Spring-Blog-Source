package com.keepseung.s3uploader.dto.mediaconverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class AwsMediaConvertDetailUserMetadataForm {
    private String objectKey;
    private String targetId;
}
