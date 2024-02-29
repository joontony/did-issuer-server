package org.snubi.did.issuerserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class StatusCheckController {

    private final Environment environment;

    /**
     * 프로필 체크(blue or green)
     * @return
     */
    @GetMapping("/profile")
    public String getProfile() {
        return Arrays.stream(environment.getActiveProfiles()).findFirst().orElse("default");
    }
}
