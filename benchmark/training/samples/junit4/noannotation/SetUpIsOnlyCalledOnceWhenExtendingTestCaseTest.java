package samples.junit4.noannotation;


import junit.framework.TestCase;


public class SetUpIsOnlyCalledOnceWhenExtendingTestCaseTest extends TestCase {
    private int state = 0;

    public void testSetupMethodIsOnlyCalledOnceWhenExtendingFromTestCase() throws Exception {
        TestCase.assertEquals(1, state);
    }
}

