package com.netflix.hystrix.contrib.javanica.test.common.error;


import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


/**
 * Created by Mike Cowan
 */
public abstract class BasicDefaultRaiseHystrixExceptionsTest {
    private BasicDefaultRaiseHystrixExceptionsTest.Service service;

    @Test(expected = BasicDefaultRaiseHystrixExceptionsTest.BadRequestException.class)
    public void testDefaultIgnoreException() {
        service.commandInheritsDefaultIgnoreExceptions();
    }

    @Test(expected = BasicDefaultRaiseHystrixExceptionsTest.SpecificException.class)
    public void testCommandOverridesDefaultIgnoreExceptions() {
        service.commandOverridesDefaultIgnoreExceptions(BasicDefaultRaiseHystrixExceptionsTest.SpecificException.class);
    }

    @Test(expected = HystrixRuntimeException.class)
    public void testCommandOverridesDefaultIgnoreExceptions_nonIgnoreExceptionShouldBePropagated() {
        // method throws BadRequestException that isn't ignored
        service.commandOverridesDefaultIgnoreExceptions(BasicDefaultRaiseHystrixExceptionsTest.BadRequestException.class);
    }

    @Test(expected = HystrixRuntimeException.class)
    public void testFallbackCommandOverridesDefaultIgnoreExceptions_nonIgnoreExceptionShouldBePropagated() {
        service.commandWithFallbackOverridesDefaultIgnoreExceptions(BasicDefaultRaiseHystrixExceptionsTest.BadRequestException.class);
    }

    @Test(expected = HystrixRuntimeException.class)
    public void testRaiseHystrixRuntimeException() {
        service.commandShouldRaiseHystrixRuntimeException();
    }

    @Test
    public void testObservableRaiseHystrixRuntimeException() {
        TestSubscriber<Void> testSubscriber = new TestSubscriber<Void>();
        service.observableCommandShouldRaiseHystrixRuntimeException().subscribe(testSubscriber);
        testSubscriber.assertError(HystrixRuntimeException.class);
    }

    @DefaultProperties(ignoreExceptions = BasicDefaultRaiseHystrixExceptionsTest.BadRequestException.class, raiseHystrixExceptions = { HystrixException.RUNTIME_EXCEPTION })
    public static class Service {
        @HystrixCommand
        public Object commandShouldRaiseHystrixRuntimeException() throws BasicDefaultRaiseHystrixExceptionsTest.SpecificException {
            throw new BasicDefaultRaiseHystrixExceptionsTest.SpecificException("from 'commandShouldRaiseHystrixRuntimeException'");
        }

        @HystrixCommand
        public Observable<Void> observableCommandShouldRaiseHystrixRuntimeException() throws BasicDefaultRaiseHystrixExceptionsTest.SpecificException {
            return Observable.error(new BasicDefaultRaiseHystrixExceptionsTest.SpecificException("from 'observableCommandShouldRaiseHystrixRuntimeException'"));
        }

        @HystrixCommand
        public Object commandInheritsDefaultIgnoreExceptions() throws BasicDefaultRaiseHystrixExceptionsTest.BadRequestException {
            // this exception will be ignored (wrapped in HystrixBadRequestException) because specified in default ignore exceptions
            throw new BasicDefaultRaiseHystrixExceptionsTest.BadRequestException("from 'commandInheritsIgnoreExceptionsFromDefault'");
        }

        @HystrixCommand(ignoreExceptions = BasicDefaultRaiseHystrixExceptionsTest.SpecificException.class)
        public Object commandOverridesDefaultIgnoreExceptions(Class<? extends Throwable> errorType) throws BasicDefaultRaiseHystrixExceptionsTest.BadRequestException, BasicDefaultRaiseHystrixExceptionsTest.SpecificException {
            if (errorType.equals(BasicDefaultRaiseHystrixExceptionsTest.BadRequestException.class)) {
                // isn't ignored because command doesn't specify this exception type in 'ignoreExceptions'
                throw new BasicDefaultRaiseHystrixExceptionsTest.BadRequestException(("from 'commandOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
            }
            // something went wrong, this error is ignored because specified in the command's ignoreExceptions
            throw new BasicDefaultRaiseHystrixExceptionsTest.SpecificException(("from 'commandOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
        }

        @HystrixCommand(fallbackMethod = "fallbackInheritsDefaultIgnoreExceptions")
        public Object commandWithFallbackInheritsDefaultIgnoreExceptions() throws BasicDefaultRaiseHystrixExceptionsTest.SpecificException {
            // isn't ignored, need to trigger fallback
            throw new BasicDefaultRaiseHystrixExceptionsTest.SpecificException("from 'commandWithFallbackInheritsDefaultIgnoreExceptions'");
        }

        @HystrixCommand
        private Object fallbackInheritsDefaultIgnoreExceptions() throws BasicDefaultRaiseHystrixExceptionsTest.BadRequestException {
            // should be ignored because specified in global ignore exception, fallback command inherits default ignore exceptions
            throw new BasicDefaultRaiseHystrixExceptionsTest.BadRequestException("from 'fallbackInheritsDefaultIgnoreExceptions'");
        }

        @HystrixCommand(fallbackMethod = "fallbackOverridesDefaultIgnoreExceptions")
        public Object commandWithFallbackOverridesDefaultIgnoreExceptions(Class<? extends Throwable> errorType) {
            // isn't ignored, need to trigger fallback
            throw new BasicDefaultRaiseHystrixExceptionsTest.SpecificException();
        }

        @HystrixCommand(ignoreExceptions = BasicDefaultRaiseHystrixExceptionsTest.SpecificException.class)
        private Object fallbackOverridesDefaultIgnoreExceptions(Class<? extends Throwable> errorType) {
            if (errorType.equals(BasicDefaultRaiseHystrixExceptionsTest.BadRequestException.class)) {
                // isn't ignored because fallback doesn't specify this exception type in 'ignoreExceptions'
                throw new BasicDefaultRaiseHystrixExceptionsTest.BadRequestException(("from 'fallbackOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
            }
            // something went wrong, this error is ignored because specified in the fallback's ignoreExceptions
            throw new BasicDefaultRaiseHystrixExceptionsTest.SpecificException(("from 'commandOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
        }
    }

    public static final class BadRequestException extends RuntimeException {
        public BadRequestException() {
        }

        public BadRequestException(String message) {
            super(message);
        }
    }

    public static final class SpecificException extends RuntimeException {
        public SpecificException() {
        }

        public SpecificException(String message) {
            super(message);
        }
    }
}

