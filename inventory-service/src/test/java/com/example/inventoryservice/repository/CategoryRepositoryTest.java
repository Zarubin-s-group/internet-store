package com.example.inventoryservice.repository;

import com.example.inventoryservice.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepositoryJpa;

    @Test
    public void whenGetByTitle_thenReturnCategory() {
        Category category = new Category();
        category.setTitle("Some category");

        entityManager.persist(category);
        entityManager.flush();

        Category gotCategory = categoryRepositoryJpa.findByTitle(category.getTitle()).get();

        assertThat(gotCategory.getId())
                .isEqualTo(category.getId());
    }
}
