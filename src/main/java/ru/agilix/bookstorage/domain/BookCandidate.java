package ru.agilix.bookstorage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookCandidate {
    private Long id;
    private String title;
    private List<Long> authors;
    private long genre;
    private String description;

}
