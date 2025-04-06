package com.subash.youtube.api.data.v3.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.subash.youtube.api.data.v3.controller.AuthController;
import com.subash.youtube.api.data.v3.view.VideoUploadView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubeDataAPIService {

    private static Logger logger = LoggerFactory.getLogger(YoutubeDataAPIService.class);
    private static final String APPLICATION_NAME = "YouTube Upload App";

    /**
     * @param path
     * @param title
     * @param description
     * @param privacyStatus
     * @return
     */
    public String uploadVideo(String path, String title, String description, String privacyStatus) {
        try {
            YouTube youtube = getYouTubeService();
            logger.info("Uploading: " + path);
            // Define metadata

            Video video = new Video();

            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(title);
            snippet.setDescription(description);
            video.setSnippet(snippet);

            VideoStatus videoStatus = new VideoStatus();
            videoStatus.setPrivacyStatus(privacyStatus);
            video.setStatus(videoStatus);

            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,status",
                            video,
                            new com.google.api.client.http.InputStreamContent(
                                    "video/*", new FileInputStream(new File(path))));

            Video uploadedVideo = videoInsert.execute();
            logger.info("Request for upload video completed");
            return "Video uploaded successfully: https://www.youtube.com/watch?v=" + uploadedVideo.getId();
        } catch (Exception e) {
            logger.error("Error in uploading video " + title + "and error : " + e.getMessage());
            return "Failed";
        }
    }

    /**
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private YouTube getYouTubeService() throws GeneralSecurityException, IOException {
        Credential credential = AuthController.flow.loadCredential("user");
        return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * @param folderPath
     * @return
     */
    public List<String> uploadVideoFromFolder(String folderPath) {
        List<String> responseUrlList = new ArrayList<>();
        Path folder = Paths.get(folderPath);

        try {
            Files.list(folder)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        VideoUploadView videoUploadView = new VideoUploadView();
                        String path = folderPath + "/" + file.getFileName().toString();
                        String titleAndDescription = file.getFileName().toString().replace('-', '|');
                        // To remove .mp4 extension
                        titleAndDescription = titleAndDescription.substring(0, titleAndDescription.length() - 4);
                        String privacyStatus = "private";
                        responseUrlList.add(uploadVideo(path, titleAndDescription, titleAndDescription, privacyStatus));
                    });
        } catch (Exception e) {
            logger.error("Error processing the folder");
        }
        return responseUrlList;
    }
}
