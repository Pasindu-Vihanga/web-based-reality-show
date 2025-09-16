package com.example.demo.Controller;

import com.example.demo.Service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookC {

    private final BookService bookService;

    public BookC(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/process")
        public String springboot(){
            return "process";
        }

    @GetMapping("/register")
    public String bookRegister() {
        return "register";
    }

    @PostMapping("/save")
    public String saveBook(@RequestParam String bookID,
                           @RequestParam String bookName,
                           @RequestParam String author,
                           @RequestParam String ISBN,
                           @RequestParam double price,
                           @RequestParam int bookQuantity) {
        Book book = new Book(bookID, bookName, author, ISBN, price, bookQuantity);
        bookService.registerBook(book);
        return "redirect:/view";
    }

    @GetMapping("/view")
    public String getAllBooks(Model model) {
        List<Book> books = bookService.fetchAllBooks();
        model.addAttribute("bookList", books);
        return "view";
    }

    @GetMapping("/search")
    public String searchBook(@RequestParam String bookName, Model model) {
        Book found = bookService.findBookByName(bookName);
        if (found != null) {
            model.addAttribute("bookList", List.of(found));
        } else {
            model.addAttribute("bookList", List.of());
            model.addAttribute("message", "No book found with Name: " + bookName);
        }
        return "view";
    }

}