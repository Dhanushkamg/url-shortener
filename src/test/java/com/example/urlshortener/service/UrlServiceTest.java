package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.repository.ClickEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @Mock
    private ClickEventRepository clickEventRepository;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository, shortCodeGenerator, clickEventRepository);
    }

    @Test
    void createShortUrl_WithCustomCode_Success() {
        Url savedUrl = new Url("https://example.com", "custom123");
        when(urlRepository.findByShortCode("custom123")).thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        Url result = urlService.createShortUrl("https://example.com", "custom123", null);

        assertNotNull(result);
        assertEquals("custom123", result.getShortCode());
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void createShortUrl_WithCustomCode_Conflict() {
        when(urlRepository.findByShortCode("custom123")).thenReturn(Optional.of(new Url()));

        assertThrows(IllegalArgumentException.class, () -> 
            urlService.createShortUrl("https://example.com", "custom123", null)
        );
    }

    @Test
    void createShortUrl_WithoutCustomCode_GeneratesCode() {
        when(shortCodeGenerator.generate()).thenReturn("gen1234");
        when(urlRepository.findByShortCode("gen1234")).thenReturn(Optional.empty());
        
        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        when(urlRepository.save(urlCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Url result = urlService.createShortUrl("https://example.com", null, null);

        assertEquals("gen1234", result.getShortCode());
        assertEquals("https://example.com", urlCaptor.getValue().getOriginalUrl());
    }

    @Test
    void getByShortCode_IncrementsClickCount() {
        Url url = new Url("https://example.com", "abc");
        url.setClickCount(5);
        
        when(urlRepository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Url> result = urlService.getByShortCode("abc", "ref", "ua");

        assertTrue(result.isPresent());
        assertEquals(6, result.get().getClickCount());
        verify(urlRepository).save(url);
        verify(clickEventRepository).save(any(com.example.urlshortener.entity.ClickEvent.class));
    }

    @Test
    void getByShortCode_ExpiredUrl_ReturnsEmpty() {
        Url url = new Url("https://example.com", "abc");
        url.setExpiresAt(LocalDateTime.now().minusDays(1));
        
        when(urlRepository.findByShortCode("abc")).thenReturn(Optional.of(url));

        Optional<Url> result = urlService.getByShortCode("abc", "ref", "ua");

        assertTrue(result.isEmpty());
        // Verify it was NOT saved (click count not incremented)
        verify(urlRepository, never()).save(any(Url.class));
    }
}
