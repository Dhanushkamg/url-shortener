package com.example.urlshortener.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_events")
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    @Column(nullable = false, updatable = false)
    private LocalDateTime clickedAt;

    @Column(length = 2048)
    private String referer;

    @Column(length = 2048)
    private String userAgent;

    public ClickEvent() {
    }

    public ClickEvent(Url url, String referer, String userAgent) {
        this.url = url;
        this.referer = referer;
        this.userAgent = userAgent;
    }

    @PrePersist
    protected void onCreate() {
        if (clickedAt == null) {
            clickedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
