package samples.junit4.legacy.noannotation;


import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.legacy.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class SetUpAndTearDownWhenExtendingTestCaseTest extends TestCase {
    private static final String INITIAL_MESSAGE = "";

    private static final String SET_UP_MESSAGE = "setUp";

    private static final String TEST_MESSAGE = "test";

    private static String CURRENT_MESSAGE = SetUpAndTearDownWhenExtendingTestCaseTest.INITIAL_MESSAGE;

    public void testSomething() throws Exception {
        TestCase.assertEquals(SetUpAndTearDownWhenExtendingTestCaseTest.SET_UP_MESSAGE, SetUpAndTearDownWhenExtendingTestCaseTest.CURRENT_MESSAGE);
        SetUpAndTearDownWhenExtendingTestCaseTest.CURRENT_MESSAGE = SetUpAndTearDownWhenExtendingTestCaseTest.TEST_MESSAGE;
    }
}

