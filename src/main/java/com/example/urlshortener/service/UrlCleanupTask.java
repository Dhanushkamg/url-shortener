package com.example.urlshortener.service;

import com.example.urlshortener.repository.UrlRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UrlCleanupTask {

    private final UrlRepository urlRepository;

    public UrlCleanupTask(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    // Run once a day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredUrls() {
        urlRepository.deleteByExpiresAtBefore(java.time.LocalDateTime.now());
    }
}
