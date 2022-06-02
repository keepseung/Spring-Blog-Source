package com.keepseung.s3uploader.controller;

import com.keepseung.s3uploader.service.S3UploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class S3UploaderController {
    private final S3UploaderService s3UploaderService;

    @GetMapping("/image")
    public String image() {
        return "image-upload";
    }

    @GetMapping("/video")
    public String video() {
        return "video-upload";
    }

    @PostMapping("/image-upload")
    @ResponseBody
    public String imageUpload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3UploaderService.upload(multipartFile, "1-source", "image");
    }

    @PostMapping("/video-upload")
    @ResponseBody
    public String videoUpload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        return s3UploaderService.upload(multipartFile, "1-source", "video");
    }
}
