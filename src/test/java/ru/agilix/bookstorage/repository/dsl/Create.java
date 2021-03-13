package ru.agilix.bookstorage.repository.dsl;

public class Create {
    public static BookBuilder Book(Long id) {
        return new BookBuilder(id);
    }

    public static CommentBuilder Comment() {
        return new CommentBuilder(-1L);
    }

    public static BookBuilder Book() {
        return new BookBuilder(-1L);
    }

    public static CommentBuilder Comment(Long id) {
        return new CommentBuilder(id);
    }
}
