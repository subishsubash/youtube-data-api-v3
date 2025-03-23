package com.subash.youtube.api.data.v3.view;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoUploadView {

    private String path;
    private String title;
    private String description;
    private String privacyStatus;

    @Override
    public String toString() {
        return "VideoUploadView{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", privacyStatus='" + privacyStatus + '\'' +
                '}';
    }
}
