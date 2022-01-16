package com.naidiuk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryGeneratorTest {
    private final QueryGenerator queryGenerator = new QueryGenerator();
    private final Person person = new Person();

    @BeforeEach
    void setup() {
        person.setId(17);
        person.setName("Oleh");
        person.setSalary(500);
    }

    @Test
    void testGetAll() {
        //prepare
        String expectedQuery = "SELECT id, person_name, salary FROM persons;";

        //when
        String actualQuery = queryGenerator.getAll(Person.class);

        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void testInsert() {
        //prepare
        String expectedQuery = "INSERT INTO persons (id, person_name, salary) VALUES (17, 'Oleh', 500.0);";

        //when
        String actualQuery = queryGenerator.insert(person);

        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void testUpdate() {
        //prepare
        String expectedQuery = "UPDATE persons SET person_name='Oleh', salary=500.0 WHERE id=17;";

        //when
        String actualQuery = queryGenerator.update(person);

        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void testGetById() {
        //prepare
        String expectedQuery = "SELECT person_name, salary FROM persons WHERE id=17;";

        //when
        String actualQuery = queryGenerator.getById(Person.class, person.getId());

        //then
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    void testDelete() {
        //prepare
        String expectedQuery = "DELETE FROM persons WHERE id=17;";

        //when
        String actualQuery = queryGenerator.delete(Person.class, person.getId());

        //then
        assertEquals(expectedQuery, actualQuery);
    }
}