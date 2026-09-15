package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponse> createUrl(
            @Valid @RequestBody UrlCreateRequest request,
            HttpServletRequest httpRequest) {

        Url url = urlService.createShortUrl(request.getOriginalUrl(), request.getCustomCode(), request.getExpiresAt());

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(httpRequest)
                .replacePath(null)
                .build()
                .toUriString();
                
        String shortUrl = baseUrl + "/" + url.getShortCode();

        UrlResponse response = new UrlResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                shortUrl,
                url.getCreatedAt(),
                url.getExpiresAt(),
                url.getClickCount()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Referer", required = false) String referer) {

        return urlService.getByShortCode(shortCode, referer, userAgent)
        .map(url -> ResponseEntity
                .status(302)
                .location(URI.create(url.getOriginalUrl()))
                .<Void>build())
        .orElse(ResponseEntity.<Void>notFound().build());
    }

    @GetMapping("/api/urls")
    public ResponseEntity<java.util.List<UrlResponse>> getAllUrls(HttpServletRequest httpRequest) {
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(httpRequest)
                .replacePath(null)
                .build()
                .toUriString();

        java.util.List<UrlResponse> responses = urlService.getAllUrls().stream()
                .map(url -> new UrlResponse(
                        url.getShortCode(),
                        url.getOriginalUrl(),
                        baseUrl + "/" + url.getShortCode(),
                        url.getCreatedAt(),
                        url.getExpiresAt(),
                        url.getClickCount()
                ))
                .toList();

        return ResponseEntity.ok(responses);
    }
}
// API endpoints for URL operations
