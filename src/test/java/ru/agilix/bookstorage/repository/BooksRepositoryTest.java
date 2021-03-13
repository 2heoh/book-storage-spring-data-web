package ru.agilix.bookstorage.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.agilix.bookstorage.domain.Author;
import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.domain.Genre;
import ru.agilix.bookstorage.repository.dsl.Create;

import java.text.ParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class BooksRepositoryTest {

    @Autowired
    private BooksRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    void getAllBooksReturnsListOfBooks() throws ParseException {
        SessionFactory sessionFactory = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);

        Author gogol = new Author(2, "Николай Гоголь");
        Genre classics = new Genre(2, "Classics");
        final var comment = Create.Comment()
                                        .Id(3L)
                                        .Text("third")
                                        .Book(Create.Book(2L).build())
                                        .Author("Liza Simpson")
                                        .Date("2021-01-19 10:02:00")
                                        .build();

        Book viy = Create.Book(2L)
                .Title("Вий")
                .Description("")
                .Author(gogol)
                .Genre(classics)
                .Comment(comment)
                .build();

        List<Book> books = repository.findAll();

        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(1);
        assertThat(books)
                .hasSize(2)
                .anyMatch( b -> b.getTitle().equals(viy.getTitle()))
                .anyMatch( b -> b.getAuthors().contains(gogol));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createBookShouldReturnIt() {

        Book book = Create.Book(null)
                .Title("title")
                .Description("description")
                .Author(new Author(1, "Лев Толстой"))
                .Genre(new Genre(1, "Action"))
                .build();

        Book inserted = repository.save(book);

        assertThat(inserted.getTitle()).isEqualTo("title");
        assertThat(inserted.getDescription()).isEqualTo("description");
        assertThat(inserted.getAuthors().get(0).getName()).isEqualTo("Лев Толстой");
        assertThat(inserted.getGenre().getName()).isEqualTo("Action");
    }

    @Test
    void saveBookShouldUpdateItsFields() {
        Book book = repository.findById(1L).get();
        Author newAuthor = new Author(2, "Николай Гоголь");
        Genre newGenre = new Genre(3, "Science Fiction (Sci-Fi)");
        Book updatedBook = Create
                            .Book(book.getId())
                            .Author(newAuthor)
                            .Title("new title")
                            .Description("new description")
                            .Author(newAuthor)
                            .Genre(newGenre)
                            .build();

        repository.save(updatedBook);

        em.detach(updatedBook);
        Book savedBook = repository.findById(1L).get();

        assertThat(savedBook.getId()).isEqualTo(1);
        assertThat(savedBook.getTitle()).isEqualTo("new title");
        assertThat(savedBook.getDescription()).isEqualTo("new description");
        assertThat(savedBook.getAuthors().get(0)).isEqualTo(newAuthor);
        assertThat(savedBook.getGenre()).isEqualTo(newGenre);
    }

    @Test
    void shouldReturnExistingBook() {
        Book book = repository.findById(1L).get();

        assertThat(book.getTitle()).isEqualTo("Война и мир");
        assertThat(book.getDescription()).startsWith("«Война́ и мир» —");
        assertThat(book.getAuthors()).size().isEqualTo(1);
        assertThat(book.getAuthors().get(0)).isEqualTo(new Author(1,"Лев Толстой"));
    }

    @Test
    void shouldRaiseExceptionForNonExistingBook() {
        final var nonExitingBook = repository.findById(-1L);
        assertFalse(nonExitingBook.isPresent());
    }

    @Test
    void shouldDeleteBookById() {
        final var book = repository.findById(1L);

        repository.delete(book.get());

        assertFalse( repository.findById(1L).isPresent());
    }


}