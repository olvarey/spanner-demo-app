package com.olvarey.spanner.note;

import java.time.Instant;

public record NoteResponse(Long id, String title, String body, Instant createdAt) {

    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getTitle(), note.getBody(), note.getCreatedAt());
    }
}
