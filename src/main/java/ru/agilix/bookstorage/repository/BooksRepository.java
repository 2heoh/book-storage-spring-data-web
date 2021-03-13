package ru.agilix.bookstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.agilix.bookstorage.domain.Book;

public interface BooksRepository extends JpaRepository<Book, Long> {
}
