package com.fernandocejas.frodo.aspect;


import com.fernandocejas.frodo.internal.Counter;
import com.fernandocejas.frodo.internal.MessageManager;
import com.fernandocejas.frodo.internal.StopWatch;
import com.fernandocejas.frodo.joinpoint.TestJoinPoint;
import com.fernandocejas.frodo.joinpoint.TestProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;


@RunWith(MockitoJUnitRunner.class)
public class LogSubscriberTest {
    private LogSubscriber logSubscriber;

    @Mock
    private Counter counter;

    @Mock
    private StopWatch stopWatch;

    @Mock
    private MessageManager messageManager;

    private TestSubscriber subscriber;

    private TestJoinPoint joinPoint;

    @Test
    public void annotatedClassMustCheckTargetType() {
        final JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        BDDMockito.given(joinPoint.getTarget()).willReturn(subscriber);
        assertThat(LogSubscriber.classAnnotatedWithRxLogSubscriber(joinPoint)).isTrue();
        Mockito.verify(joinPoint).getTarget();
        Mockito.verifyNoMoreInteractions(joinPoint);
    }

    @Test
    public void shouldWeaveClassOfTypeSubscriber() {
        final TestJoinPoint joinPoint = new TestJoinPoint.Builder(subscriber.getClass()).build();
        final TestProceedingJoinPoint proceedingJoinPoint = new TestProceedingJoinPoint(joinPoint);
        assertThat(LogSubscriber.classAnnotatedWithRxLogSubscriber(proceedingJoinPoint)).isTrue();
    }

    @Test
    public void shouldNotWeaveClassOfOtherTypeThanSubscriber() {
        final TestJoinPoint joinPoint = new TestJoinPoint.Builder(this.getClass()).build();
        final TestProceedingJoinPoint proceedingJoinPoint = new TestProceedingJoinPoint(joinPoint);
        assertThat(LogSubscriber.classAnnotatedWithRxLogSubscriber(proceedingJoinPoint)).isFalse();
    }

    @Test
    public void printOnStartMessageBeforeSubscriberOnStartExecution() {
        logSubscriber.beforeOnStartExecution(joinPoint);
        Mockito.verify(messageManager).printSubscriberOnStart(subscriber.getClass().getSimpleName());
    }

    @Test
    public void printOnNextMessageBeforeSubscriberOnNextExecution() {
        logSubscriber.beforeOnNextExecution(joinPoint);
        Mockito.verify(counter).increment();
        Mockito.verify(stopWatch).start();
        Mockito.verify(messageManager).printSubscriberOnNext(ArgumentMatchers.eq(subscriber.getClass().getSimpleName()), ArgumentMatchers.eq("value"), ArgumentMatchers.anyString());
    }

    @Test
    public void printOnNextMessageBeforeSubscriberOnNextExecutionWithEmptyValues() {
        final TestJoinPoint joinPointTest = new TestJoinPoint.Builder(subscriber.getClass()).withParamTypes(String.class).withParamNames("param").withParamValues().build();
        logSubscriber.beforeOnNextExecution(joinPointTest);
        Mockito.verify(counter).increment();
        Mockito.verify(stopWatch).start();
        Mockito.verify(messageManager).printSubscriberOnNext(ArgumentMatchers.eq(subscriber.getClass().getSimpleName()), ArgumentMatchers.anyObject(), ArgumentMatchers.anyString());
    }

    @Test
    public void printOnErrorMessageAfterSubscriberOnErrorExecution() {
        logSubscriber.afterOnErrorExecution(joinPoint, new IllegalStateException());
        Mockito.verify(stopWatch).stop();
        Mockito.verify(counter).tally();
        Mockito.verify(messageManager).printSubscriberOnError(ArgumentMatchers.eq(subscriber.getClass().getSimpleName()), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());
        Mockito.verify(counter).clear();
        Mockito.verify(stopWatch).reset();
    }

    @Test
    public void printOnCompleteMessageBeforeSubscriberOnCompleteExecution() {
        logSubscriber.beforeOnCompletedExecution(joinPoint);
        Mockito.verify(stopWatch).stop();
        Mockito.verify(messageManager).printSubscriberOnCompleted(ArgumentMatchers.eq(subscriber.getClass().getSimpleName()), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());
        Mockito.verify(counter).tally();
        Mockito.verify(counter).clear();
        Mockito.verify(stopWatch).getTotalTimeMillis();
        Mockito.verify(stopWatch).reset();
    }

    @Test
    public void printUnsubscribeMessageAfterSubscriberUnsubscribeMethodCall() {
        logSubscriber.afterUnsubscribeMethodCall(joinPoint);
        Mockito.verify(messageManager).printSubscriberUnsubscribe(subscriber.getClass().getSimpleName());
    }

    @Test
    public void printRequestedItemsAfterSubscriberRequestMethodCall() {
        logSubscriber.afterRequestMethodCall(joinPoint, 10);
        Mockito.verify(messageManager).printSubscriberRequestedItems(subscriber.getClass().getSimpleName(), 10);
    }
}

