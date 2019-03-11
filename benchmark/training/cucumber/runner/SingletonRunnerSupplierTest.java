package cucumber.runner;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SingletonRunnerSupplierTest {
    private SingletonRunnerSupplier runnerSupplier;

    @Test
    public void should_create_a_runner() {
        Assert.assertThat(runnerSupplier.get(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void should_return_the_same_runner_on_subsequent_calls() {
        Assert.assertSame(runnerSupplier.get(), runnerSupplier.get());
    }
}

