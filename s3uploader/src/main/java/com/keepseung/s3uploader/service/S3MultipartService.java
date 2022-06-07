package com.keepseung.s3uploader.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.keepseung.s3uploader.dto.S3UploadDto;
import com.keepseung.s3uploader.dto.multipart.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3MultipartService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AmazonS3Client amazonS3Client;

    public S3UploadDto initiateUpload(String originFileName, String targetBucket, String targetObjectDir) {

        // 사용자가 보낸 파일 확장자와 현재 시간을 이용해 새로운 파일 이름을 만든다.
        String fileType = originFileName.substring(originFileName.lastIndexOf(".")).toLowerCase();
        String newFileName = System.currentTimeMillis() + fileType;
        Instant now = Instant.now();

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(targetBucket) // 버킷 설정
                .key(targetObjectDir + "/" + newFileName) // 업로드될 경로 설정
                .acl(ObjectCannedACL.PUBLIC_READ) // public_read로 acl 설정
                .expires(now.plusSeconds(60 * 20)) // 객체를 더 이상 캐시할 수 없는 날짜 및 시간
                .build();

        // Amazon S3는 멀티파트 업로드에 대한 고유 식별자인 업로드 ID가 포함된 응답을 반환합니다.
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);

        return new S3UploadDto(response.uploadId(), newFileName);
    }

    public S3PresignedUrlDto getUploadSignedUrl(S3UploadSignedUrlDto s3UploadSignedUrlDto, String targetBucket, String targetObjectDir) {

        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(targetBucket)
                .key(targetObjectDir + "/" + s3UploadSignedUrlDto.getFileName())
                .uploadId(s3UploadSignedUrlDto.getUploadId())
                .partNumber(s3UploadSignedUrlDto.getPartNumber())
                .build();

        // 미리 서명된 URL 요청
        UploadPartPresignRequest uploadPartPresignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .uploadPartRequest(uploadPartRequest)
                .build();

        // 클라이언트에서 S3로 직접 업로드하기 위해 사용할 인증된 URL을 받는다.
        PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner.presignUploadPart(uploadPartPresignRequest);

        return new S3PresignedUrlDto(presignedUploadPartRequest.url().toString());
    }

    public S3UploadResultDto completeUpload(S3UploadCompleteDto s3UploadCompleteDto, String targetBucket, String targetObjectDir) {
        List<CompletedPart> completedParts = new ArrayList<>();
        // 모든 한 영상에 대한 모든 부분들에 부분 번호와 Etag를 설정함
        for (S3UploadPartsDetailDto partForm : s3UploadCompleteDto.getParts()) {
            CompletedPart part = CompletedPart.builder()
                    .partNumber(partForm.getPartNumber())
                    .eTag(partForm.getAwsETag())
                    .build();
            completedParts.add(part);
        }

        // 멀티파트 업로드 완료 요청을 AWS 서버에 보냄
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        String fileName = s3UploadCompleteDto.getFileName();
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(targetBucket) // 버킷 설정
                .key(targetObjectDir + "/" + fileName) // 파일이름 설정
                .uploadId(s3UploadCompleteDto.getUploadId()) // 업로드 아이디
                .multipartUpload(completedMultipartUpload) // 영상의 모든 부분 번호, Etag
                .build();

        CompleteMultipartUploadResponse completeMultipartUploadResponse =
                s3Client.completeMultipartUpload(completeMultipartUploadRequest);
        // s3에 업로드된 파일 이름
        String objectKey = completeMultipartUploadResponse.key();
        // s3에 업로드된 Url
        String url = amazonS3Client.getUrl(targetBucket, objectKey).toString();
        String bucket = completeMultipartUploadResponse.bucket();

        // 영상 사이즈를 구함
        long fileSize = getFileSizeFromS3Url(bucket, objectKey);

        return S3UploadResultDto.builder()
                .name(fileName)
                .url(url)
                .size(fileSize)
                .build();
    }

    public void abortUpload(S3UploadAbortDto s3UploadAbortDto, String targetBucket, String targetObjectDir) {

        AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                .bucket(targetBucket)
                .key(targetObjectDir + "/" + s3UploadAbortDto.getFileName())
                .uploadId(s3UploadAbortDto.getUploadId())
                .build();

        s3Client.abortMultipartUpload(abortMultipartUploadRequest);

    }

    private long getFileSizeFromS3Url(String bucketName, String fileName) {
        GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucketName, fileName);
        ObjectMetadata objectMetadata = amazonS3Client.getObjectMetadata(metadataRequest);
        return objectMetadata.getContentLength();
    }
}
