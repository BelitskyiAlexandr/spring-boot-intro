package com.example.bookshop;

import com.example.bookshop.model.Book;
import com.example.bookshop.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootIntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootIntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book bookOfUnknownAuthor = new Book();
                bookOfUnknownAuthor.setAuthor("Unknown");
                bookOfUnknownAuthor.setIsbn("1");
                bookOfUnknownAuthor.setPrice(BigDecimal.valueOf(100));
                bookOfUnknownAuthor.setTitle("Unknown");
                bookOfUnknownAuthor.setDescription("Unknown");
                bookOfUnknownAuthor.setCoverImage("Unknown");

                System.out.println(bookService.save(bookOfUnknownAuthor));
                System.out.println(bookService.findAll());
            }
        };
    }
}
