package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import com.java.moveminds.dto.RssItemDTO;

import java.util.List;

@Service
public interface RssNewsService {
    List<RssItemDTO> getDailyNews();
}
