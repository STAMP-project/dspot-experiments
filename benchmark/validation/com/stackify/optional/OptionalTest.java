package com.stackify.optional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class OptionalTest {
    private User user;

    @Test
    public void whenEmptyOptional_thenGetValueFromOr() {
        User result = Optional.ofNullable(user).or(() -> Optional.of(new User("default", "1234"))).get();
        Assert.assertEquals(result.getEmail(), "default");
    }

    @Test
    public void whenIfPresentOrElse_thenOk() {
        Optional.ofNullable(user).ifPresentOrElse(( u) -> System.out.println(("User is:" + (u.getEmail()))), () -> System.out.println("User not found"));
    }

    @Test
    public void whenGetStream_thenOk() {
        User user = new User("john@gmail.com", "1234");
        List<String> emails = Optional.ofNullable(user).stream().filter(( u) -> ((u.getEmail()) != null) && (u.getEmail().contains("@"))).map(( u) -> u.getEmail()).collect(Collectors.toList());
        Assert.assertTrue(((emails.size()) == 1));
        Assert.assertEquals(emails.get(0), user.getEmail());
    }
}

