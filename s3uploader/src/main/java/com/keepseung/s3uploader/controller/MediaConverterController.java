package com.keepseung.s3uploader.controller;

import com.keepseung.s3uploader.dto.mediaconverter.AwsMediaConvertForm;
import com.keepseung.s3uploader.service.MediaConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MediaConverterController {

    private final MediaConverterService mediaConverterService;
    @PostMapping("/video/status")
    public String subConvertComplete(@RequestBody AwsMediaConvertForm form) {
        mediaConverterService.subConvertComplete(form);
        return null;
    }
}
