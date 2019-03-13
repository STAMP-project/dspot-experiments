package com.sixthsolution.easymvp.test.usecase;


import easymvp.executer.PostExecutionThread;
import easymvp.executer.UseCaseExecutor;
import org.junit.Test;
import rx.observers.TestSubscriber;


/**
 *
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class UseCaseTest {
    private UseCaseExecutor useCaseExecutor;

    private PostExecutionThread postExecutionThread;

    @Test
    public void test_use_case_execution() {
        TestSubscriber<Boolean> subscriber = new TestSubscriber();
        IsNumberOdd isNumberOdd = new IsNumberOdd(useCaseExecutor, postExecutionThread);
        execute(10).subscribe(subscriber);
        subscriber.assertValue(false);
        subscriber.assertCompleted();
    }
}

