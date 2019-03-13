package samples.junit4.noannotation;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class SetUpAndTearDownWhenNotExtendingTestCaseTest {
    private static final String INITIAL_MESSAGE = "";

    private static String CURRENT_MESSAGE = SetUpAndTearDownWhenNotExtendingTestCaseTest.INITIAL_MESSAGE;

    @Test
    public void testSomething() throws Exception {
        Assert.assertEquals(SetUpAndTearDownWhenNotExtendingTestCaseTest.INITIAL_MESSAGE, SetUpAndTearDownWhenNotExtendingTestCaseTest.CURRENT_MESSAGE);
    }
}

