package ru.agilix.bookstorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.agilix.bookstorage.domain.Comment;
import ru.agilix.bookstorage.domain.CommentCandidate;
import ru.agilix.bookstorage.repository.BooksRepository;
import ru.agilix.bookstorage.repository.CommentRepository;

import java.sql.Timestamp;

@Controller
public class CommentController {
    private final BooksRepository booksRepository;
    private final CommentRepository repository;

    public CommentController(BooksRepository booksRepository, CommentRepository repository) {
        this.booksRepository = booksRepository;
        this.repository = repository;
    }

    @PostMapping(value = "/save-comment/")
    String save(CommentCandidate candidate) {

        final var book = booksRepository.findById(candidate.getBookId());
        if (book.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");

        System.out.println(" ====>  "+ candidate.getId());

        if(candidate.getId() != null) {
            var updated = repository.findById(candidate.getId());
            updated.get().setAuthor(candidate.getAuthor());
            updated.get().setText(candidate.getText());
            repository.save(updated.get());
        } else {
            final var comments = book.get().getComments();
            comments.add(new Comment(null, candidate.getText(), candidate.getAuthor(), new Timestamp(System.currentTimeMillis())));
            book.get().setComments(comments);
            booksRepository.save(book.get());
        }
        return "redirect:/view-book/"+book.get().getId()+"#comments";
    }

    @GetMapping("/view-book/{bookId}/delete-comment/{id}")
    String delete(@PathVariable Long bookId, @PathVariable Long id) {
        var comment = repository.findById(id);
        if (comment.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");

        repository.delete(comment.get());
        return "redirect:/view-book/"+bookId;
    }

    @GetMapping("/view-book/{bookId}/edit-comment/{id}")
    String edit(@PathVariable Long bookId, @PathVariable Long id, Model model) {
        var comment = repository.findById(id);
        comment.ifPresent(value -> model.addAttribute("comment", value));
        model.addAttribute("bookId", bookId);
        return "comment/edit";
    }
}
