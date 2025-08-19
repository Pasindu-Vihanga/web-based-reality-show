package com.example.demo.Service;

import com.example.demo.DAO.BookDAO;
import com.example.demo.Entity.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void registerBook(Book book) {
        if (book == null || book.getBookID().isEmpty() || book.getBookName().isEmpty() ||
                book.getPrice() <= 0 || book.getBookQuantity() < 0) {
            throw new IllegalArgumentException("Invalid book data");
        }
        bookDAO.saveBook(book);
    }

    public List<Book> fetchAllBooks() {
        return bookDAO.getAllBooks();
    }

    public Book findBookByName(String bookName) {
        if (bookName == null || bookName.isEmpty()) {
            throw new IllegalArgumentException("Book Name cannot be empty");
        }
        return bookDAO.getBookByName(bookName);
    }

    public boolean isStockAvailable(String bookName, int requestedQuantity) {
        Book book = findBookByName(bookName);
        return book != null && book.getBookQuantity() >= requestedQuantity;
    }
}