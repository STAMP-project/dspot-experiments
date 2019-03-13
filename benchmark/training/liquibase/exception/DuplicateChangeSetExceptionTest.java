package liquibase.exception;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DuplicateChangeSetException}
 */
@SuppressWarnings({ "ThrowableInstanceNeverThrown" })
public class DuplicateChangeSetExceptionTest {
    @Test
    public void duplicateChangeSetException() throws Exception {
        DuplicateChangeSetException duplicateChangeSetException = new DuplicateChangeSetException("MESSAGE HERE");
        Assert.assertEquals("MESSAGE HERE", duplicateChangeSetException.getMessage());
    }
}

