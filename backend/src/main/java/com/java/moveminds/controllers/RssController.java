package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.java.moveminds.models.dto.RssItemDTO;
import com.java.moveminds.services.RssNewsService;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class RssController {
    private final RssNewsService rssNewsService;

    @GetMapping()
    public ResponseEntity<List<RssItemDTO>> getDailyNews() {
        List<RssItemDTO> news = rssNewsService.getDailyNews();
        return ResponseEntity.ok(news);
    }
}
