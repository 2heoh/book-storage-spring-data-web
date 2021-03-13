package ru.agilix.bookstorage.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.domain.Comment;
import ru.agilix.bookstorage.repository.dsl.Create;

import java.text.ParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BooksRepository booksRepository;
    private Comment first;
    private Comment second;

    @BeforeEach
    void setUp() throws ParseException {
        em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class);

        first = Create.Comment()
                .Id(1)
                .Text("first")
                .Author("Gomer Simpson")
//                    .Book(Create.Book(1).build())
                .Date("2021-01-19 10:00:00")
                .build();

        second = Create
                .Comment()
                .Id(2)
                .Text("second")
                .Author("Gomer Simpson")
                .Date("2021-01-19 10:01:00")
//                .Book(Create.Book(2).build())
                .build();

    }

    @Test
    void shouldGetAllCommentsByBookId() {

        List<Comment> comments = booksRepository.findById(1L).get().getComments();

        assertThat(comments)
                .hasSize(2)
                .anyMatch(c -> c.getText().equals(first.getText()))
                .anyMatch(c -> c.getText().equals(second.getText()));
    }


    @Test
    void shouldReturnCommentsInReverseDateOrder() {
        List<Comment> expectedOrder = List.of(second, first);

        final var comments = booksRepository.findById(1L).get().getComments();

        assertThat(comments)
                .containsExactlyElementsOf(expectedOrder);
    }

    @Test
    void shouldInsertNewComment() {

        final var book = booksRepository.findById(1L).get();
        Comment comment = Create.Comment(null)
                .Text("text")
                .Author("somebody")
                .Book(book)
                .build();

        comment = commentRepository.save(comment);

        long id = comment.getId();
        em.detach(comment);

        Comment saved = em.find(Comment.class, id);
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getText()).isEqualTo("text");
        assertThat(saved.getAuthor()).isEqualTo("somebody");
    }

    @Test
    void shouldDeleteComment() {
        final var exiting = em.find(Comment.class, 1L);

        commentRepository.delete(exiting);

        assertThat(em.find(Comment.class, 1L)).isNull();
    }

    @Test
    void shouldUpdateExistingComment() {
        final long id = 1;
        Comment comment = commentRepository.findById(id).get();
        em.detach(comment);
        comment.setAuthor("new author");
        comment.setText("new text");

        commentRepository.save(comment);

        Comment saved = em.find(Comment.class, id);
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getText()).isEqualTo("new text");
        assertThat(saved.getAuthor()).isEqualTo("new author");
    }

}