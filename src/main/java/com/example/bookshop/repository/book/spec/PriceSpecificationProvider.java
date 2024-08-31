package com.example.bookshop.repository.book.spec;

import com.example.bookshop.model.Book;
import com.example.bookshop.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    public static final String PRICE = "price";
    public static final int MIN_VALUE_INDEX = 0;
    public static final int MAX_VALUE_INDEX = 1;

    public Specification<Book> getSpecification(String[] prices) {
        return new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                if (prices != null && prices.length == 2) {
                    try {
                        Double minPrice = Double.valueOf(prices[MIN_VALUE_INDEX]);
                        Double maxPrice = Double.valueOf(prices[MAX_VALUE_INDEX]);
                        return criteriaBuilder.between(root.get(PRICE), minPrice, maxPrice);
                    } catch (NumberFormatException e) {
                        return criteriaBuilder.conjunction();
                    }
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    @Override
    public String getKey() {
        return PRICE;
    }
}
