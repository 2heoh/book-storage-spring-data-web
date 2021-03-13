package ru.agilix.bookstorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.agilix.bookstorage.domain.Author;
import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.domain.BookCandidate;
import ru.agilix.bookstorage.domain.Genre;
import ru.agilix.bookstorage.repository.AuthorRepository;
import ru.agilix.bookstorage.repository.BooksRepository;
import ru.agilix.bookstorage.repository.GenreRepository;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookController {
    private final BooksRepository repository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookController(BooksRepository repository, AuthorRepository authorRepository, GenreRepository genreRepository) {
        this.repository = repository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    @GetMapping("/")
    String list(Model model) {
        final var books = repository.findAll();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/view-book/{id}")
    String view(@PathVariable Long id, Model model) {
        final var book = repository.findById(id);
        if (book.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "book not found");
        model.addAttribute("book", book.get());
        return "book/view";
    }

    @GetMapping("/add-book")
    String create(Model model) {
        final var book = new Book();
        book.setGenre(new Genre(0, "Unknown"));
        book.setAuthors(List.of(new Author(0, "Unknown")));
        model.addAttribute("book", book);
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        return "book/edit";
    }

    @GetMapping("/edit-book/{id}")
    String edit(@PathVariable Long id, Model model) {
        final var book = repository.findById(id);
        book.ifPresent(value -> model.addAttribute("book", value));
        model.addAttribute("authors", authorRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        return "book/edit";
    }

    @PostMapping(value = "/save-book/")
    String save(BookCandidate bookCandidate) {
        Book updatedBook;

        if (bookCandidate.getId() == null) {
            updatedBook = new Book(null, bookCandidate.getTitle(), bookCandidate.getDescription());
        } else {
            final var book = repository.findById(bookCandidate.getId());

            if (book.isPresent()) {
                updatedBook = new Book(bookCandidate.getId(), bookCandidate.getTitle(), bookCandidate.getDescription());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
            }

        }

        final var genre = genreRepository.findById(bookCandidate.getGenre());
        if (genre.isPresent()) {
            updatedBook.setGenre(genre.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }

        List<Author> result = new ArrayList<>();
        for (long authorId : bookCandidate.getAuthors()) {
            var author = authorRepository.findById(authorId);
            if (author.isPresent()) {
                result.add(author.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
            }
        }
        updatedBook.setAuthors(result);

        var saved = repository.save(updatedBook);
        return "redirect:/view-book/" + saved.getId();
    }

    @GetMapping("/delete-book/{id}")
    String delete(@PathVariable Long id, Model model) {
        var book = repository.findById(id);
        if (book.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");

        repository.delete(book.get());
        return "redirect:/";
    }

}
