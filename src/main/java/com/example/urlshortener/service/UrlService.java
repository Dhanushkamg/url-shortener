package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.repository.ClickEventRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final ClickEventRepository clickEventRepository;

    public UrlService(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator, ClickEventRepository clickEventRepository) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.clickEventRepository = clickEventRepository;
    }

    public Url createShortUrl(String originalUrl, String customCode, java.time.LocalDateTime expiresAt) {
        String code = customCode;
        if (code == null || code.isBlank()) {
            code = generateUniqueShortCode();
        } else if (urlRepository.findByShortCode(code).isPresent()) {
            throw new IllegalArgumentException("Short code already exists"); // Will be mapped to 409 later
        }

        Url url = new Url(originalUrl, code);
        url.setExpiresAt(expiresAt);
        return urlRepository.save(url);
    }
    
    private String generateUniqueShortCode() {
        for (int i = 0; i < 5; i++) {
            String code = shortCodeGenerator.generate();
            if (urlRepository.findByShortCode(code).isEmpty()) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate a unique short code after 5 attempts");
    }

    public Optional<Url> getByShortCode(String shortCode, String referer, String userAgent) {
        Optional<Url> optionalUrl = urlRepository.findByShortCode(shortCode);
        if (optionalUrl.isPresent()) {
            Url url = optionalUrl.get();
            if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
                return Optional.empty(); // Treat expired as not found (or throw 410 Gone)
            }
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
            
            com.example.urlshortener.entity.ClickEvent clickEvent = new com.example.urlshortener.entity.ClickEvent(url, referer, userAgent);
            clickEventRepository.save(clickEvent);
        }
        return optionalUrl;
    }
}