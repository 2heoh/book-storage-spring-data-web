package ru.agilix.bookstorage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommentCandidate {
    private Long id;
    private Long bookId;
    private String author;
    private String text;
}
