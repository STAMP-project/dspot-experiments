package cucumber.runtime;


import cucumber.api.event.TestCaseStarted;
import cucumber.runner.EventBus;
import cucumber.runner.Runner;
import cucumber.runner.ThreadLocalRunnerSupplier;
import cucumber.runner.TimeServiceEventBus;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ThreadLocalRunnerSupplierTest {
    private ThreadLocalRunnerSupplier runnerSupplier;

    private TimeServiceEventBus eventBus;

    @Test
    public void should_create_a_runner() {
        Assert.assertThat(runnerSupplier.get(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void should_create_a_runner_per_thread() throws InterruptedException {
        final Runner[] runners = new Runner[2];
        Thread thread0 = new Thread(new Runnable() {
            @Override
            public void run() {
                runners[0] = runnerSupplier.get();
            }
        });
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                runners[1] = runnerSupplier.get();
            }
        });
        thread0.start();
        thread1.start();
        thread0.join();
        thread1.join();
        Assert.assertNotSame(runners[0], runners[1]);
    }

    @Test
    public void should_return_the_same_runner_on_subsequent_calls() {
        Assert.assertSame(runnerSupplier.get(), runnerSupplier.get());
    }

    @Test
    public void runner_should_wrap_event_bus_bus() {
        // This avoids problems with JUnit which listens to individual runners
        EventBus runnerBus = runnerSupplier.get().getBus();
        Assert.assertNotSame(eventBus, runnerBus);
    }

    @Test
    public void should_limit_runner_bus_scope_to_events_generated_by_runner() {
        // This avoids problems with JUnit which listens to individual runners
        runnerSupplier.get().getBus().registerHandlerFor(TestCaseStarted.class, new cucumber.api.event.EventHandler<TestCaseStarted>() {
            @Override
            public void receive(TestCaseStarted event) {
                Assert.fail();
            }
        });
        eventBus.send(new TestCaseStarted(0L, null));
    }
}

