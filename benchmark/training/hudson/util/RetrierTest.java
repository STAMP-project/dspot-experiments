package hudson.util;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class RetrierTest {
    private static Logger LOG = Logger.getLogger(RetrierTest.class.getName());

    @Test
    public void performedAtThirdAttemptTest() throws Exception {
        final int SUCCESSFUL_ATTEMPT = 3;
        final String ACTION = "print";
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger.getLogger(Retrier.class.getName()).addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true if success
        // name of the action
        new Retrier.Builder<>(() -> {
            RetrierTest.LOG.info("action performed");
            return true;
        }, ( currentAttempt, result) -> currentAttempt == SUCCESSFUL_ATTEMPT, ACTION).withAttempts((SUCCESSFUL_ATTEMPT + 1)).withDelay(100).build();
        // Begin the process
        Boolean finalResult = r.start();
        Assert.assertTrue((finalResult == null ? false : finalResult));
        String text = Messages.Retrier_Success(ACTION, SUCCESSFUL_ATTEMPT);
        Assert.assertTrue(String.format("The log should contain '%s'", text), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(text)));
    }

    @Test
    public void sleepWorksTest() throws Exception {
        final int SUCCESSFUL_ATTEMPT = 2;
        final String ACTION = "print";
        final int SLEEP = 500;
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger retrierLogger = Logger.getLogger(Retrier.class.getName());
        // save current level, just in case it's needed in other tests
        Level currentLogLevel = retrierLogger.getLevel();
        retrierLogger.setLevel(Level.FINE);
        retrierLogger.addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // The time we want to wait between attempts. Let's set less time than default (1000) to have a faster
        // test
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true if success
        // name of the action
        new Retrier.Builder<>(() -> {
            RetrierTest.LOG.info("action performed");
            return true;
        }, ( currentAttempt, result) -> currentAttempt == SUCCESSFUL_ATTEMPT, ACTION).withAttempts(SUCCESSFUL_ATTEMPT).withDelay(SLEEP).build();
        // Begin the process measuring how long it takes
        Instant start = Instant.now();
        Boolean finalResult = r.start();
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        // Check delay works
        Assert.assertTrue((timeElapsed >= SLEEP));
        // Check result is true
        Assert.assertTrue((finalResult == null ? false : finalResult));
        // Check the log tell us the sleep time
        String text = Messages.Retrier_Sleeping(SLEEP, ACTION);
        Assert.assertTrue(String.format("The log should contain '%s'", text), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(text)));
        // recover log level
        retrierLogger.setLevel(currentLogLevel);
    }

    @Test
    public void failedActionAfterThreeAttemptsTest() throws Exception {
        final int ATTEMPTS = 3;
        final String ACTION = "print";
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger.getLogger(Retrier.class.getName()).addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true if success
        // name of the action
        new Retrier.Builder<>(() -> {
            RetrierTest.LOG.info("action performed");
            return false;
        }, ( currentAttempt, result) -> result, ACTION).withAttempts(ATTEMPTS).withDelay(100).build();
        // Begin the process
        Boolean finalResult = r.start();
        Assert.assertFalse((finalResult == null ? false : finalResult));
        String text = Messages.Retrier_NoSuccess(ACTION, ATTEMPTS);
        Assert.assertTrue(String.format("The log should contain '%s'", text), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(text)));
    }

    @Test
    public void failedActionWithExceptionAfterThreeAttemptsWithoutListenerTest() throws Exception {
        final int ATTEMPTS = 3;
        final String ACTION = "print";
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger.getLogger(Retrier.class.getName()).addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true (boolean primitive type) if success
        // name of the action
        new Retrier.Builder<>(((Callable<Boolean>) (() -> {
            throw new IndexOutOfBoundsException("Exception allowed considered as failure");
        })), ( currentAttempt, result) -> result == null ? false : result, ACTION).withAttempts(ATTEMPTS).withDelay(100).withDuringActionExceptions(new Class[]{ IndexOutOfBoundsException.class }).build();
        // Begin the process without catching the allowed exceptions
        Boolean finalResult = r.start();
        Assert.assertNull(finalResult);
        String textNoSuccess = Messages.Retrier_NoSuccess(ACTION, ATTEMPTS);
        Assert.assertTrue(String.format("The log should contain '%s'", textNoSuccess), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(textNoSuccess)));
        String testException = Messages.Retrier_ExceptionFailed(ATTEMPTS, ACTION);
        Assert.assertTrue(String.format("The log should contain '%s'", testException), handler.getView().stream().anyMatch(( m) -> m.getMessage().startsWith(testException)));
    }

    @Test
    public void failedActionWithAllowedExceptionWithListenerChangingResultTest() throws Exception {
        final int ATTEMPTS = 1;
        final String ACTION = "print";
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger.getLogger(Retrier.class.getName()).addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // Listener to call. It change the result to success
        // Exceptions allowed
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true if success
        // name of the action
        new Retrier.Builder<>(((Callable<Boolean>) (() -> {
            throw new IndexOutOfBoundsException("Exception allowed considered as failure");
        })), ( currentAttempt, result) -> result, ACTION).withAttempts(ATTEMPTS).withDuringActionExceptions(new Class[]{ IndexOutOfBoundsException.class }).withDuringActionExceptionListener(( attempt, exception) -> true).build();
        // Begin the process catching the allowed exception
        Boolean finalResult = r.start();
        Assert.assertTrue((finalResult == null ? false : finalResult));
        // The action was a success
        String textSuccess = Messages.Retrier_Success(ACTION, ATTEMPTS);
        Assert.assertTrue(String.format("The log should contain '%s'", textSuccess), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(textSuccess)));
        // And the message talking about the allowed raised is also there
        String testException = Messages.Retrier_ExceptionFailed(ATTEMPTS, ACTION);
        Assert.assertTrue(String.format("The log should contain '%s'", testException), handler.getView().stream().anyMatch(( m) -> m.getMessage().startsWith(testException)));
    }

    @Test
    public void failedActionWithAllowedExceptionByInheritanceTest() throws Exception {
        final int ATTEMPTS = 1;
        final String ACTION = "print";
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger.getLogger(Retrier.class.getName()).addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // Listener to call. It change the result to success
        // Exceptions allowed (not the one raised)
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true if success
        // name of the action
        new Retrier.Builder<>(((Callable<Boolean>) (() -> {
            // This one is allowed because we allow IndexOutOfBoundsException (parent exception)
            throw new ArrayIndexOutOfBoundsException("Unallowed exception breaks the process");
        })), ( currentAttempt, result) -> result, ACTION).withAttempts(ATTEMPTS).withDuringActionExceptions(new Class[]{ IndexOutOfBoundsException.class }).withDuringActionExceptionListener(( attempt, exception) -> true).build();
        // Begin the process catching the allowed exception
        Boolean finalResult = r.start();
        Assert.assertTrue((finalResult == null ? false : finalResult));
        // The action was a success
        String textSuccess = Messages.Retrier_Success(ACTION, ATTEMPTS);
        Assert.assertTrue(String.format("The log should contain '%s'", textSuccess), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(textSuccess)));
        // And the message talking about the allowed raised is also there
        String testException = Messages.Retrier_ExceptionFailed(ATTEMPTS, ACTION);
        Assert.assertTrue(String.format("The log should contain '%s'", testException), handler.getView().stream().anyMatch(( m) -> m.getMessage().startsWith(testException)));
    }

    @Test
    public void failedActionWithUnAllowedExceptionTest() {
        final int ATTEMPTS = 1;
        final String ACTION = "print";
        RingBufferLogHandler handler = new RingBufferLogHandler(20);
        Logger.getLogger(Retrier.class.getName()).addHandler(handler);
        Retrier<Boolean> r = // Construct the object
        // Exceptions allowed (not the one raised)
        // Set the optional parameters
        // Set the required params
        // action to perform
        // check the result and return true if success
        // name of the action
        new Retrier.Builder<>(((Callable<Boolean>) (() -> {
            // This one is not allowed, so it is raised out of the start method
            throw new IOException("Unallowed exception breaks the process");
        })), ( currentAttempt, result) -> result, ACTION).withAttempts(ATTEMPTS).withDuringActionExceptions(new Class[]{ IndexOutOfBoundsException.class }).build();
        // Begin the process that raises an unexpected exception
        try {
            r.start();
            TestCase.fail("The process should be exited with an unexpected exception");
        } catch (IOException e) {
            String testFailure = Messages.Retrier_ExceptionThrown(ATTEMPTS, ACTION);
            Assert.assertTrue(String.format("The log should contain '%s'", testFailure), handler.getView().stream().anyMatch(( m) -> m.getMessage().contains(testFailure)));
        } catch (Exception e) {
            TestCase.fail(String.format("Unexpected exception: %s", e));
        }
    }
}

