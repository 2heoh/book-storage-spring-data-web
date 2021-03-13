package ru.agilix.bookstorage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.agilix.bookstorage.controller.BookController;
import ru.agilix.bookstorage.controller.CommentController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookStorageWebApplicationTests {

	@Autowired
	private BookController bookController;

	@Autowired
	private CommentController commentController;

	@Test
	void contextLoads() {
		assertThat(bookController).isNotNull();
		assertThat(commentController).isNotNull();
	}

}
