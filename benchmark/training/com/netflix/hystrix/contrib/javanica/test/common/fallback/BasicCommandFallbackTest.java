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
package com.netflix.hystrix.contrib.javanica.test.common.fallback;


import HystrixEventType.FAILURE;
import HystrixEventType.FALLBACK_MISSING;
import HystrixEventType.FALLBACK_SUCCESS;
import HystrixEventType.SUCCESS;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.exception.FallbackDefinitionException;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.CommonUtils;
import com.netflix.hystrix.contrib.javanica.test.common.domain.Domain;
import com.netflix.hystrix.contrib.javanica.test.common.domain.User;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.lang3.Validate;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;


public abstract class BasicCommandFallbackTest extends BasicHystrixTest {
    private BasicCommandFallbackTest.UserService userService;

    @Test
    public void testGetUserAsyncWithFallback() throws InterruptedException, ExecutionException {
        Future<User> f1 = userService.getUserAsync(" ", "name: ");
        Assert.assertEquals("def", f1.get().getName());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("getUserAsync", command.getCommandKey().name());
        // confirm that 'getUserAsync' command has failed
        Assert.assertTrue(command.getExecutionEvents().contains(FAILURE));
        // and that fallback waw successful
        Assert.assertTrue(command.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetUserSyncWithFallback() {
        User u1 = userService.getUserSync(" ", "name: ");
        Assert.assertEquals("def", u1.getName());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("getUserSync", command.getCommandKey().name());
        // confirm that command has failed
        Assert.assertTrue(command.getExecutionEvents().contains(FAILURE));
        // and that fallback was successful
        Assert.assertTrue(command.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    /**
     * * **************************** *
     * * * TEST FALLBACK COMMANDS * *
     * * **************************** *
     */
    @Test
    public void testGetUserAsyncWithFallbackCommand() throws InterruptedException, ExecutionException {
        Future<User> f1 = userService.getUserAsyncFallbackCommand(" ", "name: ");
        Assert.assertEquals("def", f1.get().getName());
        Assert.assertEquals(3, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> getUserAsyncFallbackCommand = CommonUtils.getHystrixCommandByKey("getUserAsyncFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo firstFallbackCommand = CommonUtils.getHystrixCommandByKey("firstFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo secondFallbackCommand = CommonUtils.getHystrixCommandByKey("secondFallbackCommand");
        Assert.assertEquals("getUserAsyncFallbackCommand", getUserAsyncFallbackCommand.getCommandKey().name());
        // confirm that command has failed
        Assert.assertTrue(getUserAsyncFallbackCommand.getExecutionEvents().contains(FAILURE));
        // confirm that first fallback has failed
        Assert.assertTrue(firstFallbackCommand.getExecutionEvents().contains(FAILURE));
        // and that second fallback was successful
        Assert.assertTrue(secondFallbackCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetUserAsyncFallbackAsyncCommand() throws InterruptedException, ExecutionException {
        Future<User> f1 = userService.getUserAsyncFallbackAsyncCommand(" ", "name: ");
        Assert.assertEquals("def", f1.get().getName());
        Assert.assertEquals(4, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> getUserAsyncFallbackAsyncCommand = CommonUtils.getHystrixCommandByKey("getUserAsyncFallbackAsyncCommand");
        com.netflix.hystrix.HystrixInvokableInfo firstAsyncFallbackCommand = CommonUtils.getHystrixCommandByKey("firstAsyncFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo secondAsyncFallbackCommand = CommonUtils.getHystrixCommandByKey("secondAsyncFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo thirdAsyncFallbackCommand = CommonUtils.getHystrixCommandByKey("thirdAsyncFallbackCommand");
        Assert.assertEquals("getUserAsyncFallbackAsyncCommand", getUserAsyncFallbackAsyncCommand.getCommandKey().name());
        // confirm that command has failed
        Assert.assertTrue(getUserAsyncFallbackAsyncCommand.getExecutionEvents().contains(FAILURE));
        // confirm that first fallback has failed
        Assert.assertTrue(firstAsyncFallbackCommand.getExecutionEvents().contains(FAILURE));
        // and that second fallback was successful
        Assert.assertTrue(secondAsyncFallbackCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(thirdAsyncFallbackCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(thirdAsyncFallbackCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetUserSyncWithFallbackCommand() {
        User u1 = userService.getUserSyncFallbackCommand(" ", "name: ");
        Assert.assertEquals("def", u1.getName());
        Assert.assertEquals(3, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> getUserSyncFallbackCommand = CommonUtils.getHystrixCommandByKey("getUserSyncFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo firstFallbackCommand = CommonUtils.getHystrixCommandByKey("firstFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo secondFallbackCommand = CommonUtils.getHystrixCommandByKey("secondFallbackCommand");
        Assert.assertEquals("getUserSyncFallbackCommand", getUserSyncFallbackCommand.getCommandKey().name());
        // confirm that command has failed
        Assert.assertTrue(getUserSyncFallbackCommand.getExecutionEvents().contains(FAILURE));
        // confirm that first fallback has failed
        Assert.assertTrue(firstFallbackCommand.getExecutionEvents().contains(FAILURE));
        // and that second fallback was successful
        Assert.assertTrue(secondFallbackCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testAsyncCommandWithAsyncFallbackCommand() throws InterruptedException, ExecutionException {
        Future<User> userFuture = userService.asyncCommandWithAsyncFallbackCommand("", "");
        User user = userFuture.get();
        Assert.assertEquals("def", user.getId());
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> asyncCommandWithAsyncFallbackCommand = CommonUtils.getHystrixCommandByKey("asyncCommandWithAsyncFallbackCommand");
        com.netflix.hystrix.HystrixInvokableInfo asyncFallbackCommand = CommonUtils.getHystrixCommandByKey("asyncFallbackCommand");
        // confirm that command has failed
        Assert.assertTrue(asyncCommandWithAsyncFallbackCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(asyncCommandWithAsyncFallbackCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
        // and that second fallback was successful
        Assert.assertTrue(asyncFallbackCommand.getExecutionEvents().contains(SUCCESS));
    }

    @Test(expected = FallbackDefinitionException.class)
    public void testAsyncCommandWithAsyncFallback() {
        userService.asyncCommandWithAsyncFallback("", "");
    }

    @Test(expected = FallbackDefinitionException.class)
    public void testCommandWithWrongFallbackReturnType() {
        userService.commandWithWrongFallbackReturnType("", "");
    }

    @Test(expected = FallbackDefinitionException.class)
    public void testAsyncCommandWithWrongFallbackReturnType() {
        userService.asyncCommandWithWrongFallbackReturnType("", "");
    }

    @Test(expected = FallbackDefinitionException.class)
    public void testCommandWithWrongFallbackParams() {
        userService.commandWithWrongFallbackParams("1", "2");
    }

    @Test(expected = FallbackDefinitionException.class)
    public void testCommandWithFallbackReturnSuperType() {
        userService.commandWithFallbackReturnSuperType("", "");
    }

    @Test
    public void testCommandWithFallbackReturnSubType() {
        User user = ((User) (userService.commandWithFallbackReturnSubType("", "")));
        Assert.assertEquals("def", user.getName());
    }

    @Test
    public void testCommandWithFallbackWithAdditionalParameter() {
        User user = userService.commandWithFallbackWithAdditionalParameter("", "");
        Assert.assertEquals("def", user.getName());
    }

    @Test(expected = HystrixBadRequestException.class)
    public void testCommandThrowsHystrixBadRequestExceptionWithNoCause() {
        try {
            userService.commandThrowsHystrixBadRequestExceptionWithNoCause(null, null);
        } finally {
            HystrixInvokableInfo<?> asyncCommandWithAsyncFallbackCommand = CommonUtils.getHystrixCommandByKey("commandThrowsHystrixBadRequestExceptionWithNoCause");
            Assert.assertFalse(asyncCommandWithAsyncFallbackCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
        }
    }

    @Test
    public void testFallbackMissing() {
        try {
            userService.getUserWithoutFallback(null, null);
        } catch (Exception e) {
        }
        HystrixInvokableInfo<?> command = CommonUtils.getHystrixCommandByKey("getUserWithoutFallback");
        Assert.assertTrue("expected event: FALLBACK_MISSING", command.getExecutionEvents().contains(FALLBACK_MISSING));
    }

    public static class UserService {
        @HystrixCommand
        public User getUserWithoutFallback(final String id, final String name) {
            validate(id, name);
            return new User(id, name);
        }

        @HystrixCommand(fallbackMethod = "fallback")
        public Future<User> getUserAsync(final String id, final String name) {
            validate(id, name);// validate logic can be inside and outside of AsyncResult#invoke method

            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    // validate(id, name); possible put validation logic here, in case of any exception a fallback method will be invoked
                    return new User(id, (name + id));// it should be network call

                }
            };
        }

        @HystrixCommand(fallbackMethod = "fallback")
        public User getUserSync(String id, String name) {
            validate(id, name);
            return new User(id, (name + id));// it should be network call

        }

        private User fallback(String id, String name) {
            return new User("def", "def");
        }

        @HystrixCommand(fallbackMethod = "firstFallbackCommand")
        public Future<User> getUserAsyncFallbackCommand(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    validate(id, name);
                    return new User(id, (name + id));// it should be network call

                }
            };
        }

        @HystrixCommand(fallbackMethod = "firstFallbackCommand")
        public User getUserSyncFallbackCommand(String id, String name) {
            validate(id, name);
            return new User(id, (name + id));// it should be network call

        }

        // FALLBACK COMMANDS METHODS:
        // This fallback methods will be processed as hystrix commands
        @HystrixCommand(fallbackMethod = "secondFallbackCommand")
        private User firstFallbackCommand(String id, String name) {
            validate(id, name);
            return new User(id, (name + id));// it should be network call

        }

        @HystrixCommand(fallbackMethod = "staticFallback")
        private User secondFallbackCommand(String id, String name) {
            validate(id, name);
            return new User(id, (name + id));// it should be network call

        }

        @HystrixCommand(fallbackMethod = "firstAsyncFallbackCommand")
        public Future<User> getUserAsyncFallbackAsyncCommand(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    throw new RuntimeException("getUserAsyncFallbackAsyncCommand failed");
                }
            };
        }

        @HystrixCommand(fallbackMethod = "secondAsyncFallbackCommand")
        private Future<User> firstAsyncFallbackCommand(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    throw new RuntimeException("firstAsyncFallbackCommand failed");
                }
            };
        }

        @HystrixCommand(fallbackMethod = "thirdAsyncFallbackCommand")
        private Future<User> secondAsyncFallbackCommand(final String id, final String name, final Throwable e) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    if ("firstAsyncFallbackCommand failed".equals(e.getMessage())) {
                        throw new RuntimeException("secondAsyncFallbackCommand failed");
                    }
                    return new User(id, (name + id));
                }
            };
        }

        @HystrixCommand(fallbackMethod = "fallbackWithAdditionalParam")
        private Future<User> thirdAsyncFallbackCommand(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    throw new RuntimeException("thirdAsyncFallbackCommand failed");
                }
            };
        }

        private User fallbackWithAdditionalParam(final String id, final String name, final Throwable e) {
            if (!("thirdAsyncFallbackCommand failed".equals(e.getMessage()))) {
                throw new RuntimeException("fallbackWithAdditionalParam failed");
            }
            return new User("def", "def");
        }

        @HystrixCommand(fallbackMethod = "asyncFallbackCommand", commandProperties = { @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100000") })
        public Future<User> asyncCommandWithAsyncFallbackCommand(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    validate(id, name);
                    return new User(id, (name + id));// it should be network call

                }
            };
        }

        @HystrixCommand(fallbackMethod = "asyncFallback", commandProperties = { @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100000") })
        public Future<User> asyncCommandWithAsyncFallback(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    validate(id, name);
                    return new User(id, (name + id));// it should be network call

                }
            };
        }

        public Future<User> asyncFallback(final String id, final String name) {
            return Observable.just(new User("def", "def")).toBlocking().toFuture();
        }

        @HystrixCommand
        public Future<User> asyncFallbackCommand(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    return new User("def", "def");// it should be network call

                }
            };
        }

        @HystrixCommand(fallbackMethod = "fallbackWithAdditionalParameter")
        public User commandWithFallbackWithAdditionalParameter(final String id, final String name) {
            validate(id, name);
            return new User(id, (name + id));
        }

        public User fallbackWithAdditionalParameter(final String id, final String name, Throwable e) {
            if (e == null) {
                throw new RuntimeException("exception should be not null");
            }
            return new User("def", "def");
        }

        @HystrixCommand(fallbackMethod = "fallbackWithStringReturnType")
        public User commandWithWrongFallbackReturnType(final String id, final String name) {
            validate(id, name);
            return new User(id, name);
        }

        @HystrixCommand(fallbackMethod = "fallbackWithStringReturnType")
        public Future<User> asyncCommandWithWrongFallbackReturnType(final String id, final String name) {
            return new com.netflix.hystrix.contrib.javanica.command.AsyncResult<User>() {
                @Override
                public User invoke() {
                    return new User("def", "def");// it should be network call

                }
            };
        }

        @HystrixCommand(fallbackMethod = "fallbackWithoutParameters")
        public User commandWithWrongFallbackParams(final String id, final String name) {
            return new User(id, name);
        }

        @HystrixCommand(fallbackMethod = "fallbackReturnSubTypeOfDomain")
        public Domain commandWithFallbackReturnSubType(final String id, final String name) {
            validate(id, name);
            return new User(id, name);
        }

        @HystrixCommand(fallbackMethod = "fallbackReturnSuperTypeOfDomain")
        public User commandWithFallbackReturnSuperType(final String id, final String name) {
            validate(id, name);
            return new User(id, name);
        }

        @HystrixCommand(fallbackMethod = "staticFallback")
        public User commandThrowsHystrixBadRequestExceptionWithNoCause(final String id, final String name) {
            throw new HystrixBadRequestException("invalid arguments");
        }

        private User fallbackReturnSubTypeOfDomain(final String id, final String name) {
            return new User("def", "def");
        }

        private Domain fallbackReturnSuperTypeOfDomain(final String id, final String name) {
            return new User("def", "def");
        }

        private String fallbackWithStringReturnType(final String id, final String name) {
            return null;
        }

        private User fallbackWithoutParameters() {
            return null;
        }

        private User staticFallback(String id, String name) {
            return new User("def", "def");
        }

        private void validate(String id, String name) {
            Validate.notBlank(id);
            Validate.notBlank(name);
        }
    }
}

