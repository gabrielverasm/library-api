package br.com.binariogital.libraryapi.api.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.binariogital.libraryapi.api.dto.BookDTO;
import br.com.binariogital.libraryapi.api.exception.ApiErros;
import br.com.binariogital.libraryapi.exception.BusinessException;
import br.com.binariogital.libraryapi.model.entity.Book;
import br.com.binariogital.libraryapi.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;

	public BookController(BookService service, ModelMapper mapper) {
		this.service = service;
		this.modelMapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO dto) {
		Book entity = modelMapper.map(dto, Book.class);

		entity = service.save(entity);
		return modelMapper.map(entity, BookDTO.class);

	}

	@GetMapping("/{id}")
	public BookDTO get(@PathVariable Long id) {
		Book book = service.getById(id).get();
		return modelMapper.map(book, BookDTO.class);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex) {

		BindingResult bindingResult = ex.getBindingResult();

		return new ApiErros(bindingResult);

	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErros handlesBusinessException(BusinessException ex) {

		return new ApiErros(ex);

	}

}
