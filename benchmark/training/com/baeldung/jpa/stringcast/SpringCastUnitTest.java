package com.baeldung.jpa.stringcast;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SpringCastUnitTest {
    private static EntityManager em;

    private static EntityManagerFactory emFactory;

    @Test(expected = ClassCastException.class)
    public void givenExecutorNoCastCheck_whenQueryReturnsOneColumn_thenClassCastThrown() {
        List<String[]> results = QueryExecutor.executeNativeQueryNoCastCheck("select text from message", SpringCastUnitTest.em);
        // fails
        for (String[] row : results) {
            // do nothing
        }
    }

    @Test
    public void givenExecutorWithCastCheck_whenQueryReturnsOneColumn_thenNoClassCastThrown() {
        List<String[]> results = QueryExecutor.executeNativeQueryWithCastCheck("select text from message", SpringCastUnitTest.em);
        Assert.assertEquals("text", results.get(0)[0]);
    }

    @Test
    public void givenExecutorGeneric_whenQueryReturnsOneColumn_thenNoClassCastThrown() {
        List<Message> results = QueryExecutor.executeNativeQueryGeneric("select text from message", "textQueryMapping", SpringCastUnitTest.em);
        Assert.assertEquals("text", results.get(0).getText());
    }
}

