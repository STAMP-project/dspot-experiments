package com.baeldung.guava;


import com.baeldung.guava.entity.User;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class FluentIterableUnitTest {
    private static final int ADULT_AGE = 18;

    @Test
    public void whenFilteringByAge_shouldFilterOnlyAdultUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "John", 45));
        users.add(new User(2L, "Michael", 27));
        users.add(new User(3L, "Max", 16));
        users.add(new User(4L, "Bob", 10));
        users.add(new User(5L, "Bill", 65));
        Predicate<User> byAge = ( input) -> (input.getAge()) > (FluentIterableUnitTest.ADULT_AGE);
        List<String> results = FluentIterable.from(users).filter(byAge).transform(Functions.toStringFunction()).toList();
        Assert.assertThat(results.size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void whenCreatingFluentIterableFromArray_shouldContainAllUsers() throws Exception {
        User[] usersArray = new User[]{ new User(1L, "John", 45), new User(2L, "Max", 15) };
        FluentIterable<User> users = FluentIterable.of(usersArray);
        Assert.assertThat(users.size(), CoreMatchers.equalTo(2));
    }

    @Test
    public void whenAppendingElementsToFluentIterable_shouldContainAllUsers() throws Exception {
        User[] usersArray = new User[]{ new User(1L, "John", 45), new User(2L, "Max", 15) };
        FluentIterable<User> users = FluentIterable.of(usersArray).append(new User(3L, "Bob", 23), new User(4L, "Bill", 17));
        Assert.assertThat(users.size(), CoreMatchers.equalTo(4));
    }

    @Test
    public void whenAppendingListToFluentIterable_shouldContainAllUsers() throws Exception {
        User[] usersArray = new User[]{ new User(1L, "John", 45), new User(2L, "Max", 15) };
        List<User> usersList = new ArrayList<>();
        usersList.add(new User(3L, "David", 32));
        FluentIterable<User> users = FluentIterable.of(usersArray).append(usersList);
        Assert.assertThat(users.size(), CoreMatchers.equalTo(3));
    }

    @Test
    public void whenJoiningFluentIterableElements_shouldOutputAllUsers() throws Exception {
        User[] usersArray = new User[]{ new User(1L, "John", 45), new User(2L, "Max", 15) };
        FluentIterable<User> users = FluentIterable.of(usersArray);
        Assert.assertThat(users.join(Joiner.on("; ")), CoreMatchers.equalTo("User{id=1, name=John, age=45}; User{id=2, name=Max, age=15}"));
    }
}

