package br.com.binariogital.libraryapi.service;

import java.util.Optional;

import br.com.binariogital.libraryapi.model.entity.Book;

public interface BookService {

	Book save(Book any);

	Optional<Book> getById(Long id);

}
