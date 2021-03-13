package ru.agilix.bookstorage.repository.dsl;

import ru.agilix.bookstorage.domain.Book;
import ru.agilix.bookstorage.domain.Comment;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommentBuilder {
    private Long id;
    private Book book;
    private String text = "nothing";
    private Timestamp date;
    private String author = "no name";

    public CommentBuilder(Long id) {
        this.id = id;
    }

    public CommentBuilder Id(long id) {
        this.id = id;
        return this;
    }

    public CommentBuilder Text(String text) {
        this.text = text;
        return this;
    }

    public CommentBuilder Book(Book book) {
        this.book = book;
        return this;
    }

    public CommentBuilder Date(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = dateFormat.parse(date);
        this.date = new java.sql.Timestamp(parsedDate.getTime());
        return this;
    }

    public CommentBuilder Author(String author) {
        this.author = author;
        return this;
    }

    public Comment build() {
        final var comment = new Comment(id, text, author, date);
        return comment;
    }
}
