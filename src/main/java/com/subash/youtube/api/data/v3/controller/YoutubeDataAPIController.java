package com.subash.youtube.api.data.v3.controller;

import com.subash.youtube.api.data.v3.service.YoutubeDataAPIService;
import com.subash.youtube.api.data.v3.view.VideoUploadView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/youtube")
public class YoutubeDataAPIController {

    private static Logger logger = LoggerFactory.getLogger(YoutubeDataAPIController.class);

    private YoutubeDataAPIService youtubeDataAPIService;

    YoutubeDataAPIController(YoutubeDataAPIService service) {
        this.youtubeDataAPIService = service;
    }

    @PostMapping("/video/upload")
    public String uploadVideo(@RequestBody VideoUploadView videoUploadView) {
        logger.info("Request received for upload Video");
        return youtubeDataAPIService.uploadVideo(videoUploadView.getPath(), videoUploadView.getTitle(), videoUploadView.getDescription(), videoUploadView.getPrivacyStatus());
    }

    @PostMapping("/video/bulk/upload")
    public List<String> uploadVideos(@RequestParam("folderPath") String folderPath) {
        logger.info("Request received for upload Videos from folder");
        return youtubeDataAPIService.uploadVideoFromFolder(folderPath);

    }
}
