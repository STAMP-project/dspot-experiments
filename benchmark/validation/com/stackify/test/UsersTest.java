package com.stackify.test;


import com.stackify.daos.UserDAO;
import com.stackify.models.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


public class UsersTest implements DatabaseConnectionTest {
    private static UserDAO userDAO;

    private static Logger logger = LogManager.getLogger(UsersTest.class);

    @Test
    @DisplayName("Test Get Users")
    public void testGetUsersNumber() {
        Assertions.assertEquals(2, UsersTest.userDAO.findAll().size());
    }

    @Test
    public void testGetUser() {
        User user = UsersTest.userDAO.findOne("john@gmail.com");
        Assertions.assertEquals("John", user.getName(), (("User name:" + (user.getName())) + " incorrect"));
    }

    @Test
    public void testClassicAssertions() {
        User user1 = UsersTest.userDAO.findOne("john@gmail.com");
        User user2 = UsersTest.userDAO.findOne("john@yahoo.com");
        Assertions.assertNotNull(user1);
        Assertions.assertNull(user2);
        user2 = new User("john@yahoo.com", "John");
        Assertions.assertEquals(user1.getName(), user2.getName(), "Names are not equal");
        Assertions.assertFalse(user1.getEmail().equals(user2.getEmail()), "Emails are equal");
        Assertions.assertNotSame(user1, user2);
    }

    @Test
    public void testIterableEquals() {
        User user1 = new User("john@gmail.com", "John");
        User user2 = new User("ana@gmail.com", "Ana");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Assertions.assertIterableEquals(users, UsersTest.userDAO.findAll());
    }

    @Test
    public void testLinesMatch() {
        List<String> expectedLines = Collections.singletonList("(.*)@(.*)");
        List<String> emails = Arrays.asList("john@gmail.com");
        Assertions.assertLinesMatch(expectedLines, emails);
    }

    @Nested
    class DeleteUsersTest {
        @Test
        public void addUser() {
            User user = new User("bob@gmail.com", "Bob");
            UsersTest.userDAO.add(user);
            Assertions.assertNotNull(UsersTest.userDAO.findOne("bob@gmail.com"));
            UsersTest.userDAO.delete("bob@gmail.com");
            Assertions.assertNull(UsersTest.userDAO.findOne("bob@gmail.com"));
        }
    }
}

