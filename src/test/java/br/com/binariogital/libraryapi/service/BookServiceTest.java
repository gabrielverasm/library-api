package br.com.binariogital.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.binariogital.libraryapi.exception.BusinessException;
import br.com.binariogital.libraryapi.model.entity.Book;
import br.com.binariogital.libraryapi.model.repository.BookRepository;
import br.com.binariogital.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;

	@MockBean
	BookRepository repository;

	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve Salvar um livro")
	public void saveBookTest() {

		// Cenario
		Book book = createValidBok();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book))
				.thenReturn(Book.builder().id(1l).isbn("12345").author("Fulano").title("As Aventuras").build());

		// Execucao
		Book savedBook = service.save(book);

		// Verificacao
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("12345");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
		assertThat(savedBook.getTitle()).isEqualTo("As Aventuras");
	}

	@Test
	@DisplayName("Deve lancar erro de negocio ao tentar salvar um livro com ISBN duplicado")
	public void shouldNotSaveABookWithDuplicatedIsbn() {

		// Cenario
		Book book = createValidBok();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

		// Execucao
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));

		// Verificacao
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado.");

		Mockito.verify(repository, Mockito.never()).save(book);

	}

	private Book createValidBok() {
		return Book.builder().isbn("12345").author("Fulano").title("As Aventuras").build();
	}
}
