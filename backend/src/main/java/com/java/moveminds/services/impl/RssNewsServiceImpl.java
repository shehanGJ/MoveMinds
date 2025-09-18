package com.java.moveminds.services.impl;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.RssFeedException;
import com.java.moveminds.dto.RssItemDTO;
import com.java.moveminds.services.RssNewsService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RssNewsServiceImpl implements RssNewsService {
    @Value("${rss.feed.url}")
    private String rssUrl;

    public List<RssItemDTO> getDailyNews() {
        List<RssItemDTO> rssItems = new ArrayList<>(); // Initialize the list to store RSS items
        try {
            // Create a URL object from the rssUrl
            URL url = new URL(rssUrl);

            // Create SyndFeedInput to parse the XML into a SyndFeed object
            SyndFeedInput input = new SyndFeedInput();

            // Use XmlReader to read the XML content from the URL and build the SyndFeed object
            SyndFeed feed = input.build(new XmlReader(url));

            for (SyndEntry entry : feed.getEntries()) {
                RssItemDTO item = new RssItemDTO();
                item.setCategory(entry.getCategories().get(0).getName());
                item.setTitle(entry.getTitle());
                item.setLink(entry.getLink());
                item.setDescription(entry.getDescription().getValue());
                rssItems.add(item);
            }

        } catch (Exception e) {
            throw new RssFeedException("Error retrieving RSS feed: " + e.getMessage(), e);
        }
        return rssItems;
    }
}
