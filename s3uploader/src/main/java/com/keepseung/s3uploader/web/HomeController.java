package com.keepseung.s3uploader.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/v2/video")
    public String multipartS3() {
        return "multipart-upload-s3";
    }
}
