package com.innov8.AssetSync.model;

import java.time.LocalDateTime;

public record CalendarEvent(
        Long id,
        String title,
        LocalDateTime start,
        LocalDateTime end,
        String resourceType) {
}
