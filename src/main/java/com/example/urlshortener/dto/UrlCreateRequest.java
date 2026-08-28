package com.example.urlshortener.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public class UrlCreateRequest {

    @NotBlank(message = "Original URL must not be blank")
    @Pattern(regexp = "^(http|https)://.*$", message = "Only http and https schemes are supported")
    @URL(message = "Must be a valid URL")
    private String originalUrl;

    @Pattern(regexp = "^([a-zA-Z0-9-_]{4,20})?$", message = "Custom code must be alphanumeric (can include hyphens and underscores) and between 4 to 20 characters long")
    private String customCode;

    @Future(message = "Expiration time must be in the future")
    private LocalDateTime expiresAt;

    public UrlCreateRequest() {
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomCode() {
        return customCode;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
