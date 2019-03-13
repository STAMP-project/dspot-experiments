package cucumber.runner;


import Result.Type;
import Result.Type.FAILED;
import Result.Type.PASSED;
import Result.Type.SKIPPED;
import Result.Type.UNDEFINED;
import cucumber.api.event.EmbedEvent;
import cucumber.api.event.WriteEvent;
import gherkin.events.PickleEvent;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ScenarioResultTest {
    private EventBus bus = Mockito.mock(EventBus.class);

    private Scenario s = new Scenario(bus, new TestCase(Collections.<PickleStepTestStep>emptyList(), Collections.<HookTestStep>emptyList(), Collections.<HookTestStep>emptyList(), Mockito.mock(PickleEvent.class), false));

    @Test
    public void no_steps_is_undefined() {
        Assert.assertEquals(UNDEFINED, s.getStatus());
    }

    @Test
    public void one_passed_step_is_passed() {
        s.add(new cucumber.api.Result(Type.PASSED, 0L, null));
        Assert.assertEquals(PASSED, s.getStatus());
    }

    @Test
    public void passed_failed_pending_undefined_skipped_is_failed() {
        s.add(new cucumber.api.Result(Type.PASSED, 0L, null));
        s.add(new cucumber.api.Result(Type.FAILED, 0L, null));
        s.add(new cucumber.api.Result(Type.PENDING, 0L, null));
        s.add(new cucumber.api.Result(Type.UNDEFINED, 0L, null));
        s.add(new cucumber.api.Result(Type.SKIPPED, 0L, null));
        Assert.assertEquals(FAILED, s.getStatus());
        Assert.assertTrue(s.isFailed());
    }

    @Test
    public void passed_and_skipped_is_skipped_although_we_cant_have_skipped_without_undefined_or_pending() {
        s.add(new cucumber.api.Result(Type.PASSED, 0L, null));
        s.add(new cucumber.api.Result(Type.SKIPPED, 0L, null));
        Assert.assertEquals(SKIPPED, s.getStatus());
        Assert.assertFalse(s.isFailed());
    }

    @Test
    public void passed_pending_undefined_skipped_is_pending() {
        s.add(new cucumber.api.Result(Type.PASSED, 0L, null));
        s.add(new cucumber.api.Result(Type.UNDEFINED, 0L, null));
        s.add(new cucumber.api.Result(Type.PENDING, 0L, null));
        s.add(new cucumber.api.Result(Type.SKIPPED, 0L, null));
        Assert.assertEquals(UNDEFINED, s.getStatus());
        Assert.assertFalse(s.isFailed());
    }

    @Test
    public void passed_undefined_skipped_is_undefined() {
        s.add(new cucumber.api.Result(Type.PASSED, 0L, null));
        s.add(new cucumber.api.Result(Type.UNDEFINED, 0L, null));
        s.add(new cucumber.api.Result(Type.SKIPPED, 0L, null));
        Assert.assertEquals(UNDEFINED, s.getStatus());
        Assert.assertFalse(s.isFailed());
    }

    @Test
    public void embeds_data() {
        byte[] data = new byte[]{ 1, 2, 3 };
        s.embed(data, "bytes/foo");
        Mockito.verify(bus).send(ArgumentMatchers.argThat(new ScenarioResultTest.EmbedEventMatcher(data, "bytes/foo")));
    }

    @Test
    public void prints_output() {
        s.write("Hi");
        Mockito.verify(bus).send(ArgumentMatchers.argThat(new ScenarioResultTest.WriteEventMatcher("Hi")));
    }

    @Test
    public void failed_followed_by_pending_yields_failed_error() {
        Throwable failedError = Mockito.mock(Throwable.class);
        Throwable pendingError = Mockito.mock(Throwable.class);
        s.add(new cucumber.api.Result(Type.FAILED, 0L, failedError));
        s.add(new cucumber.api.Result(Type.PENDING, 0L, pendingError));
        Assert.assertThat(s.getError(), CoreMatchers.sameInstance(failedError));
    }

    @Test
    public void pending_followed_by_failed_yields_failed_error() {
        Throwable pendingError = Mockito.mock(Throwable.class);
        Throwable failedError = Mockito.mock(Throwable.class);
        s.add(new cucumber.api.Result(Type.PENDING, 0L, pendingError));
        s.add(new cucumber.api.Result(Type.FAILED, 0L, failedError));
        Assert.assertThat(s.getError(), CoreMatchers.sameInstance(failedError));
    }

    private final class EmbedEventMatcher implements ArgumentMatcher<EmbedEvent> {
        private byte[] data;

        private String mimeType;

        EmbedEventMatcher(byte[] data, String mimeType) {
            this.data = data;
            this.mimeType = mimeType;
        }

        @Override
        public boolean matches(EmbedEvent argument) {
            return ((argument != null) && (Arrays.equals(argument.data, data))) && (argument.mimeType.equals(mimeType));
        }
    }

    private final class WriteEventMatcher implements ArgumentMatcher<WriteEvent> {
        private String text;

        WriteEventMatcher(String text) {
            this.text = text;
        }

        @Override
        public boolean matches(WriteEvent argument) {
            return (argument != null) && (argument.text.equals(text));
        }
    }
}

