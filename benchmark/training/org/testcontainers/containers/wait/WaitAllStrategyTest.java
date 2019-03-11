package org.testcontainers.containers.wait;


import com.google.common.util.concurrent.Uninterruptibles;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.rnorth.ducttape.TimeoutException;
import org.testcontainers.containers.GenericContainer;


public class WaitAllStrategyTest {
    @Mock
    private GenericContainer container;

    @Mock
    private WaitStrategy strategy1;

    @Mock
    private WaitStrategy strategy2;

    @Test
    public void simpleTest() {
        final WaitStrategy underTest = new WaitAllStrategy().withStrategy(strategy1).withStrategy(strategy2);
        Mockito.doNothing().when(strategy1).waitUntilReady(ArgumentMatchers.eq(container));
        Mockito.doNothing().when(strategy2).waitUntilReady(ArgumentMatchers.eq(container));
        underTest.waitUntilReady(container);
        InOrder inOrder = Mockito.inOrder(strategy1, strategy2);
        inOrder.verify(strategy1).waitUntilReady(ArgumentMatchers.any());
        inOrder.verify(strategy2).waitUntilReady(ArgumentMatchers.any());
    }

    @Test
    public void appliesOuterTimeout() {
        final WaitStrategy underTest = new WaitAllStrategy().withStrategy(strategy1).withStartupTimeout(Duration.ofMillis(10));
        Mockito.doAnswer(( invocation) -> {
            Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
            return null;
        }).when(strategy1).waitUntilReady(ArgumentMatchers.eq(container));
        assertThrows("The outer strategy timeout applies", TimeoutException.class, () -> {
            underTest.waitUntilReady(container);
        });
    }
}

