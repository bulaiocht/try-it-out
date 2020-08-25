package com.test;

import com.test.entity.Actor;
import com.test.repository.ActorRepository;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = TestContainersIntegration.Initializer.class)
class TestContainersIntegration {

    @Autowired
    private ActorRepository repository;

    @Container
    public static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:11.1")
                    .withDatabaseName("integration-tests-db")
                    .withUsername("sa")
                    .withPassword("sa");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }

    }

    @Test
    void testRepository() {
        Actor bobster = new Actor();
        bobster.setFirstName("Bobster");
        bobster.setLastName("Lobster");
        repository.save(bobster);

        List<Actor> actors = repository.findAll();
        Assertions.assertThat(actors).isNotEmpty();
        Assertions.assertThat(actors.size()).isEqualTo(1);
    }
}
