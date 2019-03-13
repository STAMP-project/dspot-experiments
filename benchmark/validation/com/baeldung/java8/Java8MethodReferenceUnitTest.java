package com.baeldung.java8;


import com.baeldung.java_8_features.User;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class Java8MethodReferenceUnitTest {
    private List<String> list;

    @Test
    public void checkStaticMethodReferences_whenWork_thenCorrect() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        boolean isReal = users.stream().anyMatch(( u) -> User.isRealUser(u));
        boolean isRealRef = users.stream().anyMatch(User::isRealUser);
        Assert.assertTrue(isReal);
        Assert.assertTrue(isRealRef);
    }

    @Test
    public void checkInstanceMethodReferences_whenWork_thenCorrect() {
        User user = new User();
        boolean isLegalName = list.stream().anyMatch(user::isLegalName);
        Assert.assertTrue(isLegalName);
    }

    @Test
    public void checkParticularTypeReferences_whenWork_thenCorrect() {
        long count = list.stream().filter(String::isEmpty).count();
        Assert.assertEquals(count, 2);
    }

    @Test
    public void checkConstructorReferences_whenWork_thenCorrect() {
        Stream<User> stream = list.stream().map(User::new);
        List<User> userList = stream.collect(Collectors.toList());
        Assert.assertEquals(userList.size(), list.size());
        Assert.assertTrue(((userList.get(0)) instanceof User));
    }
}

