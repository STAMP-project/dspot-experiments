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
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.CommonUtils;
import com.netflix.hystrix.contrib.javanica.test.common.domain.User;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.observers.TestSubscriber;


/**
 * Created by dmgcodevil
 */
public abstract class BasicObservableErrorPropagationTest extends BasicHystrixTest {
    private static final String COMMAND_KEY = "getUserById";

    private static final Map<String, User> USERS;

    static {
        USERS = new HashMap<String, User>();
        BasicObservableErrorPropagationTest.USERS.put("1", new User("1", "user_1"));
        BasicObservableErrorPropagationTest.USERS.put("2", new User("2", "user_2"));
        BasicObservableErrorPropagationTest.USERS.put("3", new User("3", "user_3"));
    }

    private BasicObservableErrorPropagationTest.UserService userService;

    @MockitoAnnotations.Mock
    private BasicObservableErrorPropagationTest.FailoverService failoverService;

    @Test
    public void testGetUserByBadId() throws BasicObservableErrorPropagationTest.NotFoundException {
        try {
            TestSubscriber<User> testSubscriber = new TestSubscriber<User>();
            String badId = "";
            userService.getUserById(badId).subscribe(testSubscriber);
            testSubscriber.assertError(BasicObservableErrorPropagationTest.BadRequestException.class);
        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey(BasicObservableErrorPropagationTest.COMMAND_KEY);
            // will not affect metrics
            Assert.assertFalse(getUserCommand.getExecutionEvents().contains(FAILURE));
            // and will not trigger fallback logic
            Mockito.verify(failoverService, Mockito.never()).getDefUser();
        }
    }

    @Test
    public void testGetNonExistentUser() throws BasicObservableErrorPropagationTest.NotFoundException {
        try {
            TestSubscriber<User> testSubscriber = new TestSubscriber<User>();
            userService.getUserById("4").subscribe(testSubscriber);// user with id 4 doesn't exist

            testSubscriber.assertError(BasicObservableErrorPropagationTest.NotFoundException.class);
        } finally {
            Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey(BasicObservableErrorPropagationTest.COMMAND_KEY);
            // will not affect metrics
            Assert.assertFalse(getUserCommand.getExecutionEvents().contains(FAILURE));
            // and will not trigger fallback logic
            Mockito.verify(failoverService, Mockito.never()).getDefUser();
        }
    }

    // don't expect any exceptions because fallback must be triggered
    @Test
    public void testActivateUser() throws BasicObservableErrorPropagationTest.ActivationException, BasicObservableErrorPropagationTest.NotFoundException {
        try {
            userService.activateUser("1").toBlocking().single();// this method always throws ActivationException

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

    @Test
    public void testBlockUser() throws BasicObservableErrorPropagationTest.ActivationException, BasicObservableErrorPropagationTest.NotFoundException, BasicObservableErrorPropagationTest.OperationException {
        try {
            TestSubscriber<Void> testSubscriber = new TestSubscriber<Void>();
            userService.blockUser("1").subscribe(testSubscriber);// this method always throws ActivationException

            testSubscriber.assertError(Throwable.class);
            Assert.assertTrue(((testSubscriber.getOnErrorEvents().size()) == 1));
            Assert.assertTrue(((testSubscriber.getOnErrorEvents().get(0).getCause()) instanceof BasicObservableErrorPropagationTest.OperationException));
        } finally {
            Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
            com.netflix.hystrix.HystrixInvokableInfo activateUserCommand = CommonUtils.getHystrixCommandByKey("blockUser");
            // will not affect metrics
            Assert.assertTrue(activateUserCommand.getExecutionEvents().contains(FAILURE));
            Assert.assertTrue(activateUserCommand.getExecutionEvents().contains(FALLBACK_FAILURE));
        }
    }

    @Test
    public void testPropagateCauseException() throws BasicObservableErrorPropagationTest.NotFoundException {
        TestSubscriber<Void> testSubscriber = new TestSubscriber<Void>();
        userService.deleteUser("").subscribe(testSubscriber);
        testSubscriber.assertError(BasicObservableErrorPropagationTest.NotFoundException.class);
    }

    public static class UserService {
        private BasicObservableErrorPropagationTest.FailoverService failoverService;

        public void setFailoverService(BasicObservableErrorPropagationTest.FailoverService failoverService) {
            this.failoverService = failoverService;
        }

        @HystrixCommand
        public Observable<Void> deleteUser(String id) throws BasicObservableErrorPropagationTest.NotFoundException {
            return Observable.error(new BasicObservableErrorPropagationTest.NotFoundException(""));
        }

        @HystrixCommand(commandKey = BasicObservableErrorPropagationTest.COMMAND_KEY, ignoreExceptions = { BasicObservableErrorPropagationTest.BadRequestException.class, BasicObservableErrorPropagationTest.NotFoundException.class }, fallbackMethod = "fallback")
        public Observable<User> getUserById(String id) throws BasicObservableErrorPropagationTest.NotFoundException {
            validate(id);
            if (!(BasicObservableErrorPropagationTest.USERS.containsKey(id))) {
                return Observable.error(new BasicObservableErrorPropagationTest.NotFoundException((("user with id: " + id) + " not found")));
            }
            return Observable.just(BasicObservableErrorPropagationTest.USERS.get(id));
        }

        @HystrixCommand(ignoreExceptions = { BasicObservableErrorPropagationTest.BadRequestException.class, BasicObservableErrorPropagationTest.NotFoundException.class }, fallbackMethod = "activateFallback")
        public Observable<Void> activateUser(String id) throws BasicObservableErrorPropagationTest.ActivationException, BasicObservableErrorPropagationTest.NotFoundException {
            validate(id);
            if (!(BasicObservableErrorPropagationTest.USERS.containsKey(id))) {
                return Observable.error(new BasicObservableErrorPropagationTest.NotFoundException((("user with id: " + id) + " not found")));
            }
            // always throw this exception
            return Observable.error(new BasicObservableErrorPropagationTest.ActivationException("user cannot be activate"));
        }

        @HystrixCommand(ignoreExceptions = { BasicObservableErrorPropagationTest.BadRequestException.class, BasicObservableErrorPropagationTest.NotFoundException.class }, fallbackMethod = "blockUserFallback")
        public Observable<Void> blockUser(String id) throws BasicObservableErrorPropagationTest.NotFoundException, BasicObservableErrorPropagationTest.OperationException {
            validate(id);
            if (!(BasicObservableErrorPropagationTest.USERS.containsKey(id))) {
                return Observable.error(new BasicObservableErrorPropagationTest.NotFoundException((("user with id: " + id) + " not found")));
            }
            // always throw this exception
            return Observable.error(new BasicObservableErrorPropagationTest.OperationException("user cannot be blocked"));
        }

        private Observable<User> fallback(String id) {
            return failoverService.getDefUser();
        }

        private Observable<Void> activateFallback(String id) {
            return failoverService.activate();
        }

        @HystrixCommand(ignoreExceptions = { RuntimeException.class })
        private Observable<Void> blockUserFallback(String id) {
            return Observable.error(new BasicObservableErrorPropagationTest.RuntimeOperationException("blockUserFallback has failed"));
        }

        private void validate(String val) throws BasicObservableErrorPropagationTest.BadRequestException {
            if ((val == null) || ((val.length()) == 0)) {
                throw new BasicObservableErrorPropagationTest.BadRequestException("parameter cannot be null ot empty");
            }
        }
    }

    private class FailoverService {
        public Observable<User> getDefUser() {
            return Observable.just(new User("def", "def"));
        }

        public Observable<Void> activate() {
            return Observable.empty();
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
}

