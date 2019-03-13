package org.javaee7.ejb.async;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.inject.Inject;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jakub Marchwicki
 */
@RunWith(Arquillian.class)
public class AsyncClassBeanTest {
    @Inject
    MyAsyncBeanClassLevel bean;

    @Test
    public void should_return_async_sum() throws InterruptedException, ExecutionException {
        final Integer numberOne = 5;
        final Integer numberTwo = 10;
        long start = System.currentTimeMillis();
        final Future<Integer> resultFuture = bean.addNumbers(numberOne, numberTwo);
        MatcherAssert.assertThat(resultFuture.isDone(), is(equalTo(false)));
        MatcherAssert.assertThat(((System.currentTimeMillis()) - start), is(lessThan(MyAsyncBeanMethodLevel.AWAIT)));
        await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return resultFuture.isDone();
            }
        });
        MatcherAssert.assertThat(resultFuture.get(), is(equalTo((numberOne + numberTwo))));
    }
}

