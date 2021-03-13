package ru.agilix.bookstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.agilix.bookstorage.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
