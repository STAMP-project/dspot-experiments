package liquibase.exception;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DuplicateStatementIdentifierException}
 */
@SuppressWarnings({ "ThrowableInstanceNeverThrown" })
public class DuplicateStatementIdentifierExceptionTest {
    @Test
    public void duplicateStatementIdentifierException() throws Exception {
        DuplicateStatementIdentifierException ex = new DuplicateStatementIdentifierException("Message Here");
        Assert.assertEquals("Message Here", ex.getMessage());
    }
}

