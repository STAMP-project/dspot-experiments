package samples.junit4.singleton;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.singleton.SimpleStaticService;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleStaticService.class)
public class SimpleStaticServiceTest {
    @Test
    public void testMockStatic() throws Exception {
        mockStatic(SimpleStaticService.class);
        final String expected = "Hello altered World";
        expect(SimpleStaticService.say("hello")).andReturn("Hello altered World");
        replay(SimpleStaticService.class);
        final String actual = SimpleStaticService.say("hello");
        verify(SimpleStaticService.class);
        Assert.assertEquals("Expected and actual did not match", expected, actual);
        // Singleton still be mocked by now.
        try {
            SimpleStaticService.say("world");
            Assert.fail("Should throw AssertionError!");
        } catch (final AssertionError e) {
            Assert.assertEquals("\n  Unexpected method call SimpleStaticService.say(\"world\"):", e.getMessage());
        }
    }
}

