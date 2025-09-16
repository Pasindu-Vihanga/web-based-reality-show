package com.example.demo.DAO;

import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookDAO {

    private static final String FILE_NAME = "books.txt";

    public void saveBook(Book book) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String line = String.join(",",
                    book.getBookID(),
                    book.getBookName(),
                    book.getAuthor(),
                    book.getISBN(),
                    String.valueOf(book.getPrice()),
                    String.valueOf(book.getBookQuantity()));
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    books.add(new Book(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookByName(String bookName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6 && parts[1].equals(bookName)) {
                    return new Book(
                            parts[0], parts[1], parts[2],
                            parts[3], Double.parseDouble(parts[4]),
                            Integer.parseInt(parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}