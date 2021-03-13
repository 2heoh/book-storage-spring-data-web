package ru.agilix.bookstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.agilix.bookstorage.domain.Author;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
