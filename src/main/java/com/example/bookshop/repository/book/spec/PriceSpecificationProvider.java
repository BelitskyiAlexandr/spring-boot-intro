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
    public Specification<Book> getSpecification(String[] prices) {
        return new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                if (prices != null && prices.length == 2) {
                    try {
                        Double minPrice = Double.valueOf(prices[0]);
                        Double maxPrice = Double.valueOf(prices[1]);
                        return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
                    } catch (NumberFormatException e) {
                        // if invalid - true
                        return criteriaBuilder.conjunction();
                    }
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    @Override
    public String getKey() {
        return "price";
    }
}
