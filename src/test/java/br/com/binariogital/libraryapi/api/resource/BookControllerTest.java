package br.com.binariogital.libraryapi.api.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.binariogital.libraryapi.api.dto.BookDTO;
import br.com.binariogital.libraryapi.exception.BusinessException;
import br.com.binariogital.libraryapi.model.entity.Book;
import br.com.binariogital.libraryapi.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService service;

	@Test
	@DisplayName("Deve criar um livro com sucesso.")
	public void createBookTest() throws Exception {

		BookDTO dto = createNewBook();
		Book savedBook = Book.builder().id(10l).author("Arthur").title("As aventuras").isbn("001").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(10l))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));

	}

	@Test
	@DisplayName("Deve lançar erro quando não houver dados necessários para criar livro.")
	public void createInvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("erros", hasSize(3)));

	}

	@Test
	@DisplayName("Deve lancar erro ao tentar cadastrar livro com ISBN repetido")
	public void createBookWithDuplicatedIsbn() throws Exception {

		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		String mensagemErro = "Isbn já cadastrado.";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagemErro));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest()).andExpect(jsonPath("erros", hasSize(1)))
				.andExpect(jsonPath("erros[0]").value(mensagemErro));

	}

	@Test
	@DisplayName("Deve obter informacoes de um livro")
	public void getBookDetailsTest() throws Exception {

		// cenario (given)
		Long id = 1l;

		Book book = Book.builder().id(id).title(createNewBook().getTitle()).author(createNewBook().getAuthor())
				.isbn(createNewBook().getIsbn()).build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		// execucao (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));

	}

	private BookDTO createNewBook() {
		return BookDTO.builder().author("Arthur").title("As aventuras").isbn("001").build();
	}

}
