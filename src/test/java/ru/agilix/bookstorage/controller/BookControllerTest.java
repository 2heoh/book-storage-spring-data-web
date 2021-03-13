package ru.agilix.bookstorage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.agilix.bookstorage.domain.Author;
import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.domain.Genre;
import ru.agilix.bookstorage.repository.AuthorRepository;
import ru.agilix.bookstorage.repository.BooksRepository;
import ru.agilix.bookstorage.repository.GenreRepository;
import ru.agilix.bookstorage.repository.dsl.Create;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BooksRepository repository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    private Book the_bible;

    @BeforeEach
    void setUp() {
        the_bible = Create.Book(1L).Title("The bible").build();
    }

    @Test
    public void siteRootShouldReturnListOfBooks() throws Exception {
        when(repository.findAll()).thenReturn(List.of(the_bible));

        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("List of books")))
                .andExpect(content().string(containsString("The bible")));
    }


    @Test
    void viewBook1ShouldRenderPageWithBookWithId1() throws Exception {
        final var the_bible = Create
                .Book(1L)
                .Title("The bible")
                .Genre(new Genre(1L, "Classics"))
                .Author(new Author(1L, "Unknown"))
                .build();
        when(repository.findById(1L)).thenReturn(Optional.of(the_bible));

        this.mockMvc.perform(get("/view-book/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Classics")))
                .andExpect(content().string(containsString("Unknown")))
                .andExpect(content().string(containsString("The bible")));

    }

    @Test
    void viewNonExitingBookShouldRender404() throws Exception {
        when(repository.findById(-1L)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/view-book/-1"))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldSaveNewBookAndReturnItsLocation() throws Exception {
        when(repository.save(ArgumentMatchers.any(Book.class))).thenReturn(the_bible);
        when(genreRepository.findById(1L)).thenReturn(Optional.of(new Genre(1L, "Genre")));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author(1L, "Author")));

        this.mockMvc.perform(post("/save-book/")
                    .content("id=&title=book&authors=1&genre=1&description=description")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/view-book/1"));

        verify(repository, times(1)).save(
                Create
                    .Book(null)
                    .Title("book")
                    .Description("description")
                    .Author(new Author(1L, "Author"))
                    .Genre(new Genre(1L, "Genre"))
                    .build()
        );

    }

    @Test
    void shouldUpdateExistingBook() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(the_bible));
        when(genreRepository.findById(2L)).thenReturn(Optional.of(new Genre(2L, "New Genre")));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(new Author(2L, "New Author")));
        final var updatedBook = Create
                .Book()
                .Id(1L)
                .Title("new book")
                .Description("new description")
                .Author(new Author(2L, "New Author"))
                .Genre(new Genre(2L, "New Genre"))
                .build();

        when(repository.save(any())).thenReturn(updatedBook);

        this.mockMvc.perform(post("/save-book/")
            .content("id=1&title=new book&authors=2&genre=2&description=new description")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/view-book/1"));

        verify(repository, times(1)).save(updatedBook);
    }

    @Test
    void shouldRender404WhenUpdateNonExistingBook() throws Exception {
        when(repository.findById(-1L)).thenReturn(Optional.empty());

        this.mockMvc.perform(post("/save-book/")
                .content("id=-1&title=new book&authors=2&genre=2&description=new description")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
        .andExpect(status().isNotFound());

        verify(repository, times(0)).save(any());
    }

    @Test
    void saveShouldRender404WhenPassedNonExistingAuthor() throws Exception {

        when(repository.save(ArgumentMatchers.any(Book.class))).thenReturn(the_bible);
        when(genreRepository.findById(1L)).thenReturn(Optional.of(new Genre(1L, "Genre")));
        when(authorRepository.findById(-1L)).thenReturn(Optional.empty());

        this.mockMvc.perform(post("/save-book/")
                .content("id=1&title=book&authors=-1&genre=1&description=description")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isNotFound());

    }

    @Test
    void saveShouldRender404WhenPassedNonExistingGenre() throws Exception {

        when(repository.save(ArgumentMatchers.any(Book.class))).thenReturn(the_bible);
        when(genreRepository.findById(-1L)).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author(1L, "Author")));

        this.mockMvc.perform(post("/save-book/")
                .content("id=1&title=book&authors=1&genre=-1&description=description")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(status().isNotFound());

    }

    @Test
    void deleteExistingBookShouldReturnRedirectOnRoot() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(the_bible));

        this.mockMvc
                .perform(get("/delete-book/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/"));

        verify(repository, times(1)).delete(the_bible);
    }

    @Test
    void deleteNonExistingBookShouldRender404() throws Exception {
        when(repository.findById(-1L)).thenReturn(Optional.empty());

        this.mockMvc
                .perform(get("/delete-book/-1"))
                .andExpect(status().isNotFound());

        verify(repository, times(0)).delete(the_bible);
    }
}