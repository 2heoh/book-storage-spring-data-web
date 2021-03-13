package ru.agilix.bookstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.domain.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
//    Optional<Book> findCommentByBook_Id(long id);
}
