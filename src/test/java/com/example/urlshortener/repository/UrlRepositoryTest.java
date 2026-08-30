package com.example.urlshortener.repository;

import com.example.urlshortener.entity.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UrlRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void findByShortCode_Success() {
        Url url = new Url("https://example.com", "mycode");
        urlRepository.save(url);

        Optional<Url> found = urlRepository.findByShortCode("mycode");
        
        assertTrue(found.isPresent());
        assertEquals("https://example.com", found.get().getOriginalUrl());
    }

    @Test
    void deleteByExpiresAtBefore_Success() {
        Url expired = new Url("https://expired.com", "exp1");
        expired.setExpiresAt(LocalDateTime.now().minusDays(2));
        
        Url valid = new Url("https://valid.com", "val1");
        valid.setExpiresAt(LocalDateTime.now().plusDays(2));

        urlRepository.save(expired);
        urlRepository.save(valid);

        urlRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        assertTrue(urlRepository.findByShortCode("exp1").isEmpty());
        assertTrue(urlRepository.findByShortCode("val1").isPresent());
    }
}
