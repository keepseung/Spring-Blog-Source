package com.keepseung.s3uploader.service;

import com.keepseung.s3uploader.dto.mediaconverter.AwsMediaConvertForm;
import com.keepseung.s3uploader.dto.mediaconverter.AwsMediaConvertOutputDetail;
import com.keepseung.s3uploader.dto.mediaconverter.AwsMediaConvertOutputGroupDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class MediaConverterService {
    public static final String AWS_S3_URL = "https://test-video-m.s3.ap-northeast-2.amazonaws.com/";
    public static final String OUTPUT_FOLDER = "output";
    public void subConvertComplete(AwsMediaConvertForm form) {

        String targetId = form.getDetail().getUserMetadata().getTargetId();
        String objectKey = form.getDetail().getUserMetadata().getObjectKey();
        String streamUrl = AWS_S3_URL + String.format("%s/%s/Default/HLS/%s.m3u8", OUTPUT_FOLDER, objectKey, objectKey);
        String status = form.getDetail().getStatus();
        int jobPercentComplete = form.getDetail().getJobProgress().getJobPercentComplete();
        log.info("============= 미디어 컨버터 비디오 변환 결과를 수신함 ==================");
        log.info("status: {}, jobPercentComplete:{}", status, jobPercentComplete);
        log.info("targetId: {}, objectKey:{}, streamUrl: {}", targetId, objectKey, streamUrl);

        if (status.equals("COMPLETE")) {
            double videoLength = -1;
            if (!ObjectUtils.isEmpty(form.getDetail().getOutputGroupDetails())) {
                AwsMediaConvertOutputGroupDetail outputGroupDetail = form.getDetail().getOutputGroupDetails().get(0);
                if (!ObjectUtils.isEmpty(outputGroupDetail.getOutputDetails())) {
                    AwsMediaConvertOutputDetail outputDetail = outputGroupDetail.getOutputDetails().get(0);
                    videoLength = outputDetail.getDurationInMs();
                    log.info("videoLength: {}", videoLength);
                }
            }

        }
        log.info("============= 미디어 컨버터 비디오 변환 결과를 수신 종료 ==================");


    }
}
