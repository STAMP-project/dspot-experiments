package samples.junit4.expectnew;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.expectnew.PrimitiveAndWrapperDemo;
import samples.expectnew.PrimitiveAndWrapperUser;


/**
 * Unit test for the {@link PrimitiveAndWrapperUser} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PrimitiveAndWrapperUser.class)
public class PrimitiveAndWrapperUserTest {
    @Test
    public void testNewWithStrictMocking_ok() throws Exception {
        PrimitiveAndWrapperDemo mock1 = createStrictMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ Integer.class }, 42);
        PrimitiveAndWrapperDemo mock2 = createStrictMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ int.class }, 21);
        expect(mock1.getMyInt()).andReturn(10);
        expect(mock2.getMyInt()).andReturn(21);
        replayAll();
        Assert.assertEquals(31, new PrimitiveAndWrapperUser().useThem());
        verifyAll();
    }

    @Test(expected = AssertionError.class)
    public void testNewWithStrictMocking_notOk() throws Exception {
        PrimitiveAndWrapperDemo mock2 = createStrictMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ int.class }, 21);
        PrimitiveAndWrapperDemo mock1 = createStrictMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ Integer.class }, 42);
        expect(mock1.getMyInt()).andReturn(10);
        expect(mock2.getMyInt()).andReturn(21);
        replayAll();
        Assert.assertEquals(31, new PrimitiveAndWrapperUser().useThem());
        verifyAll();
    }

    @Test
    public void testNewWithNiceMocking() throws Exception {
        PrimitiveAndWrapperDemo mock = createNiceMockAndExpectNew(PrimitiveAndWrapperDemo.class, new Class<?>[]{ Integer.class }, 42);
        expect(mock.getMyInt()).andReturn(2);
        replayAll();
        Assert.assertEquals(2, new PrimitiveAndWrapperUser().useThem());
        verifyAll();
    }
}

