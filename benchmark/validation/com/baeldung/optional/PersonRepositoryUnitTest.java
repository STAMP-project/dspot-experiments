package com.baeldung.optional;


import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;


public class PersonRepositoryUnitTest {
    PersonRepository personRepository = new PersonRepository();

    @Test
    public void whenIdIsNull_thenExceptionIsThrown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Optional.ofNullable(personRepository.findNameById(null)).orElseThrow(IllegalArgumentException::new));
    }

    @Test
    public void whenIdIsNonNull_thenNoExceptionIsThrown() {
        Assertions.assertAll(() -> Optional.ofNullable(personRepository.findNameById("id")).orElseThrow(RuntimeException::new));
    }

    @Test
    public void whenIdNonNull_thenReturnsNameUpperCase() {
        String name = Optional.ofNullable(personRepository.findNameById("id")).map(String::toUpperCase).orElseThrow(RuntimeException::new);
        Assertions.assertEquals("NAME", name);
    }
}

