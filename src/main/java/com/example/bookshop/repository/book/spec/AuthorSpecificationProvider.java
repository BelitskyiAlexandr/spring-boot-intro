package com.example.bookshop.repository.book.spec;

import com.example.bookshop.model.Book;
import com.example.bookshop.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    public static final String AUTHOR = "author";

    public Specification<Book> getSpecification(String[] authors) {
        return (root, query, criteriaBuilder) ->
                root.get(AUTHOR).in(Arrays.stream(authors).toArray());
    }

    @Override
    public String getKey() {
        return AUTHOR;
    }
}
