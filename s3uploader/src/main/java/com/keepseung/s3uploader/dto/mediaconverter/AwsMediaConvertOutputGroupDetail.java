package com.keepseung.s3uploader.dto.mediaconverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AwsMediaConvertOutputGroupDetail {
    private List<AwsMediaConvertOutputDetail> outputDetails;
}
