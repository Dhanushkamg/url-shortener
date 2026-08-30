package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlCreateRequest;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    @Test
    void createUrl_ValidRequest_ReturnsOk() throws Exception {
        UrlCreateRequest request = new UrlCreateRequest();
        request.setOriginalUrl("https://example.com");

        Url url = new Url("https://example.com", "abc1234");
        when(urlService.createShortUrl(eq("https://example.com"), any(), any())).thenReturn(url);

        mockMvc.perform(post("/api/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc1234"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortUrl").exists());
    }

    @Test
    void createUrl_InvalidUrlScheme_ReturnsBadRequest() throws Exception {
        UrlCreateRequest request = new UrlCreateRequest();
        request.setOriginalUrl("javascript:alert(1)");

        mockMvc.perform(post("/api/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.originalUrl").exists());
    }

    @Test
    void redirect_ExistingCode_Returns302() throws Exception {
        Url url = new Url("https://example.com", "abc");
        when(urlService.getByShortCode(eq("abc"), any(), any())).thenReturn(Optional.of(url));

        mockMvc.perform(get("/abc"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirect_NonExistingCode_Returns404() throws Exception {
        when(urlService.getByShortCode(eq("nonexist"), any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/nonexist"))
                .andExpect(status().isNotFound());
    }
}
