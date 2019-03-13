package com.baeldung.jdbc.autogenkey;


import com.baeldung.jdbc.autogenkey.repository.MessageRepositoryJDBCTemplate;
import com.baeldung.jdbc.autogenkey.repository.MessageRepositorySimpleJDBCInsert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@Ignore
public class GetAutoGenKeyByJDBCIntTest {
    @Configuration
    @EnableAutoConfiguration
    @PropertySource("classpath:autogenkey-db.properties")
    @ComponentScan(basePackages = { "com.baeldung.jdbc.autogenkey.repository" })
    public static class SpringConfig {}

    @Autowired
    MessageRepositorySimpleJDBCInsert messageRepositorySimpleJDBCInsert;

    @Autowired
    MessageRepositoryJDBCTemplate messageRepositoryJDBCTemplate;

    final String MESSAGE_CONTENT = "Test";

    @Test
    public void insertJDBC_whenLoadMessageByKey_thenGetTheSameMessage() {
        long key = messageRepositoryJDBCTemplate.insert(MESSAGE_CONTENT);
        String loadedMessage = messageRepositoryJDBCTemplate.getMessageById(key);
        Assertions.assertEquals(MESSAGE_CONTENT, loadedMessage);
    }

    @Test
    public void insertSimpleInsert_whenLoadMessageKey_thenGetTheSameMessage() {
        long key = messageRepositorySimpleJDBCInsert.insert(MESSAGE_CONTENT);
        String loadedMessage = messageRepositoryJDBCTemplate.getMessageById(key);
        Assertions.assertEquals(MESSAGE_CONTENT, loadedMessage);
    }
}

