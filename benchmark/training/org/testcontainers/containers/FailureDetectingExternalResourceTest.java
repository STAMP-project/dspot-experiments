package org.testcontainers.containers;


import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FailureDetectingExternalResourceTest {
    @Test
    public void finishedIsCalledForCleanupIfStartingThrows() throws Throwable {
        FailureDetectingExternalResource res = Mockito.spy(FailureDetectingExternalResource.class);
        Statement stmt = res.apply(Mockito.mock(Statement.class), Description.EMPTY);
        Mockito.doThrow(new RuntimeException()).when(res).starting(ArgumentMatchers.any());
        try {
            stmt.evaluate();
        } catch (Throwable t) {
            // ignore
        }
        Mockito.verify(res).starting(ArgumentMatchers.any());
        Mockito.verify(res).finished(ArgumentMatchers.any());
    }
}

