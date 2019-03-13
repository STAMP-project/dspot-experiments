/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.javanica.test.common.error;


import HystrixEventType.FAILURE;
import HystrixEventType.FALLBACK_FAILURE;
import HystrixEventType.FALLBACK_SUCCESS;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.CommonUtils;
import com.netflix.hystrix.contrib.javanica.test.common.domain.User;
import com.netflix.hystrix.exception.ExceptionNotWrappedByHystrix;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Created by dmgcodevil
 */
public abstract class BasicErrorPropagationTest extends BasicHystrixTest {
    private static final String COMMAND_KEY = "getUserById";

    private static final Map<String, User> USERS;

    static {
        USERS = new HashMap<String, User>();
        BasicErrorPropagationTest.USERS.put("1", new User("1", "user_1"));
        BasicErrorPropagationTest.USERS.put("2", new User("2", "user_2"));
        BasicErrorPropagationTest.USERS.put("3", new User("3", "user_3"));
    }

    private BasicErrorPropagationTest.UserService userService;

    @MockitoAnnotations.Mock
    private BasicErrorPropagationTest.FailoverService failoverService;

    @Test(expected = BasicErrorPropagationTest.BadRequestException.class)
    public void testGetUserByBadId() throws BasicErrorPropagationTest.NotFoundException {
        try {
            String badId = "";
            userService.getUserById(badId);
        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey(BasicErrorPropagationTest.COMMAND_KEY);
            // will not affect metrics
            Assert.assertFalse(getUserCommand.getExecutionEvents().contains(FAILURE));
            // and will not trigger fallback logic
            Mockito.verify(failoverService, Mockito.never()).getDefUser();
        }
    }

    @Test(expected = BasicErrorPropagationTest.NotFoundException.class)
    public void testGetNonExistentUser() throws BasicErrorPropagationTest.NotFoundException {
        try {
            userService.getUserById("4");// user with id 4 doesn't exist

        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey(BasicErrorPropagationTest.COMMAND_KEY);
            // will not affect metrics
            Assert.assertFalse(getUserCommand.getExecutionEvents().contains(FAILURE));
            // and will not trigger fallback logic
            Mockito.verify(failoverService, Mockito.never()).getDefUser();
        }
    }

    // don't expect any exceptions because fallback must be triggered
    @Test
    public void testActivateUser() throws BasicErrorPropagationTest.ActivationException, BasicErrorPropagationTest.NotFoundException {
        try {
            userService.activateUser("1");// this method always throws ActivationException

        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo activateUserCommand = CommonUtils.getHystrixCommandByKey("activateUser");
            // will not affect metrics
            Assert.assertTrue(activateUserCommand.getExecutionEvents().contains(FAILURE));
            Assert.assertTrue(activateUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
            // and will not trigger fallback logic
            Mockito.verify(failoverService, Mockito.atLeastOnce()).activate();
        }
    }

    @Test(expected = BasicErrorPropagationTest.RuntimeOperationException.class)
    public void testBlockUser() throws BasicErrorPropagationTest.ActivationException, BasicErrorPropagationTest.NotFoundException, BasicErrorPropagationTest.OperationException {
        try {
            userService.blockUser("1");// this method always throws ActivationException

        } finally {
            Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo activateUserCommand = CommonUtils.getHystrixCommandByKey("blockUser");
            // will not affect metrics
            Assert.assertTrue(activateUserCommand.getExecutionEvents().contains(FAILURE));
            Assert.assertTrue(activateUserCommand.getExecutionEvents().contains(FALLBACK_FAILURE));
        }
    }

    @Test(expected = BasicErrorPropagationTest.NotFoundException.class)
    public void testPropagateCauseException() throws BasicErrorPropagationTest.NotFoundException {
        userService.deleteUser("");
    }

    @Test(expected = BasicErrorPropagationTest.UserException.class)
    public void testUserExceptionThrownFromCommand() {
        userService.userFailureWithoutFallback();
    }

    @Test
    public void testHystrixExceptionThrownFromCommand() {
        try {
            userService.timedOutWithoutFallback();
        } catch (HystrixRuntimeException e) {
            Assert.assertEquals(TimeoutException.class, e.getCause().getClass());
        } catch (Throwable e) {
            Assert.assertTrue("'HystrixRuntimeException' is expected exception.", false);
        }
    }

    @Test
    public void testUserExceptionThrownFromFallback() {
        try {
            userService.userFailureWithFallback();
        } catch (BasicErrorPropagationTest.UserException e) {
            Assert.assertEquals(1, e.level);
        } catch (Throwable e) {
            Assert.assertTrue("'UserException' is expected exception.", false);
        }
    }

    @Test
    public void testUserExceptionThrownFromFallbackCommand() {
        try {
            userService.userFailureWithFallbackCommand();
        } catch (BasicErrorPropagationTest.UserException e) {
            Assert.assertEquals(2, e.level);
        } catch (Throwable e) {
            Assert.assertTrue("'UserException' is expected exception.", false);
        }
    }

    @Test
    public void testCommandAndFallbackErrorsComposition() {
        try {
            userService.commandAndFallbackErrorsComposition();
        } catch (BasicErrorPropagationTest.HystrixFlowException e) {
            Assert.assertEquals(BasicErrorPropagationTest.UserException.class, e.commandException.getClass());
            Assert.assertEquals(BasicErrorPropagationTest.UserException.class, e.fallbackException.getClass());
            BasicErrorPropagationTest.UserException commandException = ((BasicErrorPropagationTest.UserException) (e.commandException));
            BasicErrorPropagationTest.UserException fallbackException = ((BasicErrorPropagationTest.UserException) (e.fallbackException));
            Assert.assertEquals(0, commandException.level);
            Assert.assertEquals(1, fallbackException.level);
        } catch (Throwable e) {
            Assert.assertTrue("'HystrixFlowException' is expected exception.", false);
        }
    }

    @Test
    public void testCommandWithFallbackThatFailsByTimeOut() {
        try {
            userService.commandWithFallbackThatFailsByTimeOut();
        } catch (HystrixRuntimeException e) {
            Assert.assertEquals(TimeoutException.class, e.getCause().getClass());
        } catch (Throwable e) {
            Assert.assertTrue("'HystrixRuntimeException' is expected exception.", false);
        }
    }

    @Test
    public void testCommandWithNotWrappedExceptionAndNoFallback() {
        try {
            userService.throwNotWrappedCheckedExceptionWithoutFallback();
            Assert.fail();
        } catch (BasicErrorPropagationTest.NotWrappedCheckedException e) {
            // pass
        } catch (Throwable e) {
            Assert.fail("'NotWrappedCheckedException' is expected exception.");
        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("throwNotWrappedCheckedExceptionWithoutFallback");
            // record failure in metrics
            Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
            // and will not trigger fallback logic
            Mockito.verify(failoverService, Mockito.never()).activate();
        }
    }

    @Test
    public void testCommandWithNotWrappedExceptionAndFallback() {
        try {
            userService.throwNotWrappedCheckedExceptionWithFallback();
        } catch (BasicErrorPropagationTest.NotWrappedCheckedException e) {
            Assert.fail();
        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("throwNotWrappedCheckedExceptionWithFallback");
            Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
            Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
            Mockito.verify(failoverService).activate();
        }
    }

    public static class UserService {
        private BasicErrorPropagationTest.FailoverService failoverService;

        public void setFailoverService(BasicErrorPropagationTest.FailoverService failoverService) {
            this.failoverService = failoverService;
        }

        @HystrixCommand
        public Object deleteUser(String id) throws BasicErrorPropagationTest.NotFoundException {
            throw new BasicErrorPropagationTest.NotFoundException("");
        }

        @HystrixCommand(commandKey = BasicErrorPropagationTest.COMMAND_KEY, ignoreExceptions = { BasicErrorPropagationTest.BadRequestException.class, BasicErrorPropagationTest.NotFoundException.class }, fallbackMethod = "fallback")
        public User getUserById(String id) throws BasicErrorPropagationTest.NotFoundException {
            validate(id);
            if (!(BasicErrorPropagationTest.USERS.containsKey(id))) {
                throw new BasicErrorPropagationTest.NotFoundException((("user with id: " + id) + " not found"));
            }
            return BasicErrorPropagationTest.USERS.get(id);
        }

        @HystrixCommand(ignoreExceptions = { BasicErrorPropagationTest.BadRequestException.class, BasicErrorPropagationTest.NotFoundException.class }, fallbackMethod = "activateFallback")
        public void activateUser(String id) throws BasicErrorPropagationTest.ActivationException, BasicErrorPropagationTest.NotFoundException {
            validate(id);
            if (!(BasicErrorPropagationTest.USERS.containsKey(id))) {
                throw new BasicErrorPropagationTest.NotFoundException((("user with id: " + id) + " not found"));
            }
            // always throw this exception
            throw new BasicErrorPropagationTest.ActivationException("user cannot be activate");
        }

        @HystrixCommand(ignoreExceptions = { BasicErrorPropagationTest.BadRequestException.class, BasicErrorPropagationTest.NotFoundException.class }, fallbackMethod = "blockUserFallback")
        public void blockUser(String id) throws BasicErrorPropagationTest.NotFoundException, BasicErrorPropagationTest.OperationException {
            validate(id);
            if (!(BasicErrorPropagationTest.USERS.containsKey(id))) {
                throw new BasicErrorPropagationTest.NotFoundException((("user with id: " + id) + " not found"));
            }
            // always throw this exception
            throw new BasicErrorPropagationTest.OperationException("user cannot be blocked");
        }

        private User fallback(String id) {
            return failoverService.getDefUser();
        }

        private void activateFallback(String id) {
            failoverService.activate();
        }

        @HystrixCommand(ignoreExceptions = { RuntimeException.class })
        private void blockUserFallback(String id) {
            throw new BasicErrorPropagationTest.RuntimeOperationException("blockUserFallback has failed");
        }

        private void validate(String val) throws BasicErrorPropagationTest.BadRequestException {
            if ((val == null) || ((val.length()) == 0)) {
                throw new BasicErrorPropagationTest.BadRequestException("parameter cannot be null ot empty");
            }
        }

        @HystrixCommand
        void throwNotWrappedCheckedExceptionWithoutFallback() throws BasicErrorPropagationTest.NotWrappedCheckedException {
            throw new BasicErrorPropagationTest.NotWrappedCheckedException();
        }

        @HystrixCommand(fallbackMethod = "voidFallback")
        void throwNotWrappedCheckedExceptionWithFallback() throws BasicErrorPropagationTest.NotWrappedCheckedException {
            throw new BasicErrorPropagationTest.NotWrappedCheckedException();
        }

        private void voidFallback() {
            failoverService.activate();
        }

        /**
         * ******************************************************************************
         */
        @HystrixCommand
        String userFailureWithoutFallback() throws BasicErrorPropagationTest.UserException {
            throw new BasicErrorPropagationTest.UserException();
        }

        @HystrixCommand(commandProperties = { @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1") })
        String timedOutWithoutFallback() {
            return "";
        }

        /**
         * ******************************************************************************
         */
        @HystrixCommand(fallbackMethod = "userFailureWithFallback_f_0")
        String userFailureWithFallback() {
            throw new BasicErrorPropagationTest.UserException();
        }

        String userFailureWithFallback_f_0() {
            throw new BasicErrorPropagationTest.UserException(1);
        }

        /**
         * ******************************************************************************
         */
        @HystrixCommand(fallbackMethod = "userFailureWithFallbackCommand_f_0")
        String userFailureWithFallbackCommand() {
            throw new BasicErrorPropagationTest.UserException(0);
        }

        @HystrixCommand(fallbackMethod = "userFailureWithFallbackCommand_f_1")
        String userFailureWithFallbackCommand_f_0() {
            throw new BasicErrorPropagationTest.UserException(1);
        }

        @HystrixCommand
        String userFailureWithFallbackCommand_f_1() {
            throw new BasicErrorPropagationTest.UserException(2);
        }

        /**
         * ******************************************************************************
         */
        @HystrixCommand(fallbackMethod = "commandAndFallbackErrorsComposition_f_0")
        String commandAndFallbackErrorsComposition() {
            throw new BasicErrorPropagationTest.UserException();
        }

        String commandAndFallbackErrorsComposition_f_0(Throwable commandError) {
            throw new BasicErrorPropagationTest.HystrixFlowException(commandError, new BasicErrorPropagationTest.UserException(1));
        }

        /**
         * ******************************************************************************
         */
        @HystrixCommand(fallbackMethod = "commandWithFallbackThatFailsByTimeOut_f_0")
        String commandWithFallbackThatFailsByTimeOut() {
            throw new BasicErrorPropagationTest.UserException(0);
        }

        @HystrixCommand(commandProperties = { @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1") })
        String commandWithFallbackThatFailsByTimeOut_f_0() {
            return "";
        }
    }

    private class FailoverService {
        public User getDefUser() {
            return new User("def", "def");
        }

        public void activate() {
        }
    }

    // exceptions
    private static class NotFoundException extends Exception {
        private NotFoundException(String message) {
            super(message);
        }
    }

    private static class BadRequestException extends RuntimeException {
        private BadRequestException(String message) {
            super(message);
        }
    }

    private static class ActivationException extends Exception {
        private ActivationException(String message) {
            super(message);
        }
    }

    private static class OperationException extends Throwable {
        private OperationException(String message) {
            super(message);
        }
    }

    private static class RuntimeOperationException extends RuntimeException {
        private RuntimeOperationException(String message) {
            super(message);
        }
    }

    private static class NotWrappedCheckedException extends Exception implements ExceptionNotWrappedByHystrix {}

    static class UserException extends RuntimeException {
        final int level;

        public UserException() {
            this(0);
        }

        public UserException(int level) {
            this.level = level;
        }
    }

    static class HystrixFlowException extends RuntimeException {
        final Throwable commandException;

        final Throwable fallbackException;

        public HystrixFlowException(Throwable commandException, Throwable fallbackException) {
            this.commandException = commandException;
            this.fallbackException = fallbackException;
        }
    }
}

