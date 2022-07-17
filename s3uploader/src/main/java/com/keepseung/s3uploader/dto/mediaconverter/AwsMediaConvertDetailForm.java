package com.keepseung.s3uploader.dto.mediaconverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class AwsMediaConvertDetailForm {
    private String status;
    private AwsMediaConvertDetailUserMetadataForm userMetadata;
    private AwsMediaConvertJobProgressForm jobProgress;
    private List<AwsMediaConvertOutputGroupDetail> outputGroupDetails;
}
