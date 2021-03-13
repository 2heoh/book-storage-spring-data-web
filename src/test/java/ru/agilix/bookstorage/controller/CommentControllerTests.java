package ru.agilix.bookstorage.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.repository.BooksRepository;
import ru.agilix.bookstorage.repository.CommentRepository;
import ru.agilix.bookstorage.repository.dsl.Create;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BooksRepository booksRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Test
    void saveNewCommentShouldPutIntIntoRepository() throws Exception {
        final var book = Create.Book(1L).Comments(new ArrayList<>()).build();
        when(booksRepository.findById(1L)).thenReturn(Optional.of(book));

        this.mockMvc.perform(post("/save-comment/")
            .content("bookId=1&author=author&text=text")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/view-book/1#comments"));

        book.setComments(List.of(Create.Comment(null).Author("author").Text("text").build()));
        verify(booksRepository, times(1)).save(book);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    void saveExistingCommentShouldUpdateIntIntoRepository() throws Exception {
        final var book = Create.Book(2L).Comments(new ArrayList<>()).build();
        when(booksRepository.findById(2L)).thenReturn(Optional.of(book));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(Create.Comment(1L).Author("author").Text("text").build()));

        this.mockMvc.perform(post("/save-comment/")
                .content("id=1&bookId=2&author=new_author&text=new text")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/view-book/2#comments"));

        book.setComments(List.of(Create.Comment(null).Author("author").Text("text").build()));
        verify(commentRepository, times(1)).save(Create.Comment(1L).Author("new_author").Text("new text").build());
        verify(booksRepository, times(0)).save(any());
    }
}
