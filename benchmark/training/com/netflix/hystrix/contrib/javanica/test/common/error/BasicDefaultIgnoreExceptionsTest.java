package com.netflix.hystrix.contrib.javanica.test.common.error;


import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.junit.Test;


/**
 * Test for {@link DefaultProperties#ignoreExceptions()} feature.
 *
 * <p>
 * Created by dmgcodevil.
 */
public abstract class BasicDefaultIgnoreExceptionsTest {
    private BasicDefaultIgnoreExceptionsTest.Service service;

    @Test(expected = BasicDefaultIgnoreExceptionsTest.BadRequestException.class)
    public void testDefaultIgnoreException() {
        service.commandInheritsDefaultIgnoreExceptions();
    }

    @Test(expected = BasicDefaultIgnoreExceptionsTest.SpecificException.class)
    public void testCommandOverridesDefaultIgnoreExceptions() {
        service.commandOverridesDefaultIgnoreExceptions(BasicDefaultIgnoreExceptionsTest.SpecificException.class);
    }

    @Test(expected = BasicDefaultIgnoreExceptionsTest.BadRequestException.class)
    public void testCommandOverridesDefaultIgnoreExceptions_nonIgnoreExceptionShouldBePropagated() {
        // method throws BadRequestException that isn't ignored
        service.commandOverridesDefaultIgnoreExceptions(BasicDefaultIgnoreExceptionsTest.BadRequestException.class);
    }

    @Test(expected = BasicDefaultIgnoreExceptionsTest.BadRequestException.class)
    public void testFallbackCommandOverridesDefaultIgnoreExceptions_nonIgnoreExceptionShouldBePropagated() {
        service.commandWithFallbackOverridesDefaultIgnoreExceptions(BasicDefaultIgnoreExceptionsTest.BadRequestException.class);
    }

    @DefaultProperties(ignoreExceptions = BasicDefaultIgnoreExceptionsTest.BadRequestException.class)
    public static class Service {
        @HystrixCommand
        public Object commandInheritsDefaultIgnoreExceptions() throws BasicDefaultIgnoreExceptionsTest.BadRequestException {
            // this exception will be ignored (wrapped in HystrixBadRequestException) because specified in default ignore exceptions
            throw new BasicDefaultIgnoreExceptionsTest.BadRequestException("from 'commandInheritsIgnoreExceptionsFromDefault'");
        }

        @HystrixCommand(ignoreExceptions = BasicDefaultIgnoreExceptionsTest.SpecificException.class)
        public Object commandOverridesDefaultIgnoreExceptions(Class<? extends Throwable> errorType) throws BasicDefaultIgnoreExceptionsTest.BadRequestException, BasicDefaultIgnoreExceptionsTest.SpecificException {
            if (errorType.equals(BasicDefaultIgnoreExceptionsTest.BadRequestException.class)) {
                // isn't ignored because command doesn't specify this exception type in 'ignoreExceptions'
                throw new BasicDefaultIgnoreExceptionsTest.BadRequestException(("from 'commandOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
            }
            // something went wrong, this error is ignored because specified in the command's ignoreExceptions
            throw new BasicDefaultIgnoreExceptionsTest.SpecificException(("from 'commandOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
        }

        @HystrixCommand(fallbackMethod = "fallbackInheritsDefaultIgnoreExceptions")
        public Object commandWithFallbackInheritsDefaultIgnoreExceptions() throws BasicDefaultIgnoreExceptionsTest.SpecificException {
            // isn't ignored, need to trigger fallback
            throw new BasicDefaultIgnoreExceptionsTest.SpecificException("from 'commandWithFallbackInheritsDefaultIgnoreExceptions'");
        }

        @HystrixCommand
        private Object fallbackInheritsDefaultIgnoreExceptions() throws BasicDefaultIgnoreExceptionsTest.BadRequestException {
            // should be ignored because specified in global ignore exception, fallback command inherits default ignore exceptions
            throw new BasicDefaultIgnoreExceptionsTest.BadRequestException("from 'fallbackInheritsDefaultIgnoreExceptions'");
        }

        @HystrixCommand(fallbackMethod = "fallbackOverridesDefaultIgnoreExceptions")
        public Object commandWithFallbackOverridesDefaultIgnoreExceptions(Class<? extends Throwable> errorType) {
            // isn't ignored, need to trigger fallback
            throw new BasicDefaultIgnoreExceptionsTest.SpecificException();
        }

        @HystrixCommand(ignoreExceptions = BasicDefaultIgnoreExceptionsTest.SpecificException.class)
        private Object fallbackOverridesDefaultIgnoreExceptions(Class<? extends Throwable> errorType) {
            if (errorType.equals(BasicDefaultIgnoreExceptionsTest.BadRequestException.class)) {
                // isn't ignored because fallback doesn't specify this exception type in 'ignoreExceptions'
                throw new BasicDefaultIgnoreExceptionsTest.BadRequestException(("from 'fallbackOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
            }
            // something went wrong, this error is ignored because specified in the fallback's ignoreExceptions
            throw new BasicDefaultIgnoreExceptionsTest.SpecificException(("from 'commandOverridesDefaultIgnoreExceptions', cause: " + (errorType.getSimpleName())));
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

