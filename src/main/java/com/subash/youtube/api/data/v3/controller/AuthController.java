package com.subash.youtube.api.data.v3.controller;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CLIENT_SECRET_FILE = "src/main/resources/credentials.json";
    private static final String REDIRECT_URI = "http://localhost:8080/api/auth/callback";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static AuthorizationCodeFlow flow;

    static {
        try {
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CLIENT_SECRET_FILE)),
                    Collections.singletonList("https://www.googleapis.com/auth/youtube.upload"))
                    .setDataStoreFactory(new MemoryDataStoreFactory())
                    .setAccessType("offline")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/login")
    public void authenticate(HttpServletResponse response) throws IOException {
        String authUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        response.sendRedirect(authUrl);
    }

    @GetMapping("/callback")
    public String handleOAuthCallback(@RequestParam("code") String code) throws IOException {
        AuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI);
        TokenResponse tokenResponse = tokenRequest.execute();
        flow.createAndStoreCredential(tokenResponse, "user");
        return "Authentication successful! You can now upload videos.";
    }
}
