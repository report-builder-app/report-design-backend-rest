package com.report_design.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RenderCache {

    public record Entry(byte[] body, String contentType, String filename) {}

    private final Map<String, Entry> cache = new ConcurrentHashMap<>();

    public Entry get(String key) {
        return cache.get(key);
    }

    public void put(String key, byte[] body, String contentType, String filename) {
        cache.put(key, new Entry(body, contentType, filename));
    }

    public void clear() {
        cache.clear();
    }
}
