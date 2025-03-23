package com.subash.youtube.api.data.v3.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VersionController {

    @GetMapping("/version")
    public String getVersion() {
        return "0.1";
    }
}
