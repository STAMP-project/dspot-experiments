package cucumber.runtime.java.spring.threading;


import cucumber.api.cli.Main;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


public class RunParallelCukesTest {
    private final Callable<Byte> runCuke = new Callable<Byte>() {
        @Override
        public Byte call() throws Exception {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String[] args = new String[]{ "--glue", "cucumber.runtime.java.spring.threading", "classpath:cucumber/runtime/java/spring/threadingCukes.feature", "--strict" };
            return Main.run(args, classLoader);
        }
    };

    @Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Byte> result1 = executorService.submit(runCuke);
        Future<Byte> result2 = executorService.submit(runCuke);
        Assert.assertEquals(result1.get().byteValue(), 0);
        Assert.assertEquals(result2.get().byteValue(), 0);
    }
}

