package com.baeldung.jdbc.autogenkey;


import com.baeldung.jdbc.autogenkey.repository.MessageRepositoryJDBCTemplate;
import com.baeldung.jdbc.autogenkey.repository.MessageRepositorySimpleJDBCInsert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class GetAutoGenKeyByJDBC {
    @Configuration
    @ComponentScan(basePackages = { "com.baeldung.jdbc.autogenkey" })
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
        Assert.assertEquals(MESSAGE_CONTENT, loadedMessage);
    }

    @Test
    public void insertSimpleInsert_whenLoadMessageKey_thenGetTheSameMessage() {
        long key = messageRepositorySimpleJDBCInsert.insert(MESSAGE_CONTENT);
        String loadedMessage = messageRepositoryJDBCTemplate.getMessageById(key);
        Assert.assertEquals(MESSAGE_CONTENT, loadedMessage);
    }
}

