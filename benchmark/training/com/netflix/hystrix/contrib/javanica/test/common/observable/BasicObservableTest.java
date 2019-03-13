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
package com.netflix.hystrix.contrib.javanica.test.common.observable;


import HystrixEventType.FAILURE;
import HystrixEventType.FALLBACK_SUCCESS;
import HystrixEventType.SUCCESS;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.CommonUtils;
import com.netflix.hystrix.contrib.javanica.test.common.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import rx.Completable;
import rx.Observable;
import rx.Single;


/**
 * Created by dmgcodevil
 */
public abstract class BasicObservableTest extends BasicHystrixTest {
    private BasicObservableTest.UserService userService;

    @Test
    public void testGetUserByIdSuccess() {
        // blocking
        Observable<User> observable = userService.getUser("1", "name: ");
        Assert.assertEquals("name: 1", observable.toBlocking().single().getName());
        // non-blocking
        // - this is a verbose anonymous inner-class approach and doesn't do assertions
        Observable<User> fUser = userService.getUser("1", "name: ");
        fUser.subscribe(new rx.Observer<User>() {
            @Override
            public void onCompleted() {
                // nothing needed here
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(User v) {
                System.out.println(("onNext: " + v));
            }
        });
        Observable<User> fs = userService.getUser("1", "name: ");
        fs.subscribe(new rx.functions.Action1<User>() {
            @Override
            public void call(User user) {
                Assert.assertEquals("name: 1", user.getName());
            }
        });
        Assert.assertEquals(3, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getUser");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(SUCCESS));
    }

    @Test
    public void testGetCompletableUser() {
        userService.getCompletableUser("1", "name: ");
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getCompletableUser");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(SUCCESS));
    }

    @Test
    public void testGetCompletableUserWithRegularFallback() {
        Completable completable = userService.getCompletableUserWithRegularFallback(null, "name: ");
        completable.<User>toObservable().subscribe(new rx.functions.Action1<User>() {
            @Override
            public void call(User user) {
                Assert.assertEquals("default_id", user.getId());
            }
        });
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getCompletableUserWithRegularFallback");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetCompletableUserWithRxFallback() {
        Completable completable = userService.getCompletableUserWithRxFallback(null, "name: ");
        completable.<User>toObservable().subscribe(new rx.functions.Action1<User>() {
            @Override
            public void call(User user) {
                Assert.assertEquals("default_id", user.getId());
            }
        });
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getCompletableUserWithRxFallback");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetSingleUser() {
        final String id = "1";
        Single<User> user = userService.getSingleUser(id, "name: ");
        user.subscribe(new rx.functions.Action1<User>() {
            @Override
            public void call(User user) {
                Assert.assertEquals(id, user.getId());
            }
        });
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getSingleUser");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(SUCCESS));
    }

    @Test
    public void testGetSingleUserWithRegularFallback() {
        Single<User> user = userService.getSingleUserWithRegularFallback(null, "name: ");
        user.subscribe(new rx.functions.Action1<User>() {
            @Override
            public void call(User user) {
                Assert.assertEquals("default_id", user.getId());
            }
        });
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getSingleUserWithRegularFallback");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetSingleUserWithRxFallback() {
        Single<User> user = userService.getSingleUserWithRxFallback(null, "name: ");
        user.subscribe(new rx.functions.Action1<User>() {
            @Override
            public void call(User user) {
                Assert.assertEquals("default_id", user.getId());
            }
        });
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getSingleUserWithRxFallback");
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetUserWithRegularFallback() {
        final User exUser = new User("def", "def");
        Observable<User> userObservable = userService.getUserRegularFallback(" ", "");
        // blocking
        Assert.assertEquals(exUser, userObservable.toBlocking().single());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getUserRegularFallback");
        // confirm that command has failed
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
        // and that fallback was successful
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetUserWithRxFallback() {
        final User exUser = new User("def", "def");
        // blocking
        Assert.assertEquals(exUser, userService.getUserRxFallback(" ", "").toBlocking().single());
        Assert.assertEquals(1, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        com.netflix.hystrix.HystrixInvokableInfo getUserCommand = CommonUtils.getHystrixCommandByKey("getUserRxFallback");
        // confirm that command has failed
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FAILURE));
        // and that fallback was successful
        Assert.assertTrue(getUserCommand.getExecutionEvents().contains(FALLBACK_SUCCESS));
    }

    @Test
    public void testGetUserWithRxCommandFallback() {
        final User exUser = new User("def", "def");
        // blocking
        Observable<User> userObservable = userService.getUserRxCommandFallback(" ", "");
        Assert.assertEquals(exUser, userObservable.toBlocking().single());
        Assert.assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        com.netflix.hystrix.HystrixInvokableInfo getUserRxCommandFallback = CommonUtils.getHystrixCommandByKey("getUserRxCommandFallback");
        com.netflix.hystrix.HystrixInvokableInfo rxCommandFallback = CommonUtils.getHystrixCommandByKey("rxCommandFallback");
        // confirm that command has failed
        Assert.assertTrue(getUserRxCommandFallback.getExecutionEvents().contains(FAILURE));
        Assert.assertTrue(getUserRxCommandFallback.getExecutionEvents().contains(FALLBACK_SUCCESS));
        // and that fallback command was successful
        Assert.assertTrue(rxCommandFallback.getExecutionEvents().contains(SUCCESS));
    }

    public static class UserService {
        private User regularFallback(String id, String name) {
            return new User("def", "def");
        }

        private Observable<User> rxFallback(String id, String name) {
            return Observable.just(new User("def", "def"));
        }

        @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)
        private Observable<User> rxCommandFallback(String id, String name, Throwable throwable) {
            if ((throwable instanceof BasicObservableTest.UserService.GetUserException) && ("getUserRxCommandFallback has failed".equals(throwable.getMessage()))) {
                return Observable.just(new User("def", "def"));
            } else {
                throw new IllegalStateException();
            }
        }

        @HystrixCommand
        public Observable<User> getUser(final String id, final String name) {
            validate(id, name, "getUser has failed");
            return createObservable(id, name);
        }

        @HystrixCommand
        public Completable getCompletableUser(final String id, final String name) {
            validate(id, name, "getCompletableUser has failed");
            return createObservable(id, name).toCompletable();
        }

        @HystrixCommand(fallbackMethod = "completableUserRegularFallback")
        public Completable getCompletableUserWithRegularFallback(final String id, final String name) {
            return getCompletableUser(id, name);
        }

        @HystrixCommand(fallbackMethod = "completableUserRxFallback")
        public Completable getCompletableUserWithRxFallback(final String id, final String name) {
            return getCompletableUser(id, name);
        }

        public User completableUserRegularFallback(final String id, final String name) {
            return new User("default_id", "default_name");
        }

        public Completable completableUserRxFallback(final String id, final String name) {
            return Completable.fromCallable(new rx.functions.Func0<User>() {
                @Override
                public User call() {
                    return new User("default_id", "default_name");
                }
            });
        }

        @HystrixCommand
        public Single<User> getSingleUser(final String id, final String name) {
            validate(id, name, "getSingleUser has failed");
            return createObservable(id, name).toSingle();
        }

        @HystrixCommand(fallbackMethod = "singleUserRegularFallback")
        public Single<User> getSingleUserWithRegularFallback(final String id, final String name) {
            return getSingleUser(id, name);
        }

        @HystrixCommand(fallbackMethod = "singleUserRxFallback")
        public Single<User> getSingleUserWithRxFallback(final String id, final String name) {
            return getSingleUser(id, name);
        }

        User singleUserRegularFallback(final String id, final String name) {
            return new User("default_id", "default_name");
        }

        Single<User> singleUserRxFallback(final String id, final String name) {
            return createObservable("default_id", "default_name").toSingle();
        }

        @HystrixCommand(fallbackMethod = "regularFallback", observableExecutionMode = ObservableExecutionMode.LAZY)
        public Observable<User> getUserRegularFallback(final String id, final String name) {
            validate(id, name, "getUser has failed");
            return createObservable(id, name);
        }

        @HystrixCommand(fallbackMethod = "rxFallback")
        public Observable<User> getUserRxFallback(final String id, final String name) {
            validate(id, name, "getUserRxFallback has failed");
            return createObservable(id, name);
        }

        @HystrixCommand(fallbackMethod = "rxCommandFallback", observableExecutionMode = ObservableExecutionMode.LAZY)
        public Observable<User> getUserRxCommandFallback(final String id, final String name) {
            validate(id, name, "getUserRxCommandFallback has failed");
            return createObservable(id, name);
        }

        private Observable<User> createObservable(final String id, final String name) {
            return Observable.create(new Observable.OnSubscribe<User>() {
                @Override
                public void call(Subscriber<? extends User> observer) {
                    try {
                        if (!(observer.isUnsubscribed())) {
                            observer.onNext(new User(id, (name + id)));
                            observer.onCompleted();
                        }
                    } catch ( e) {
                        observer.onError(e);
                    }
                }
            });
        }

        private void validate(String id, String name, String errorMsg) {
            if ((StringUtils.isBlank(id)) || (StringUtils.isBlank(name))) {
                throw new BasicObservableTest.UserService.GetUserException(errorMsg);
            }
        }

        private static final class GetUserException extends RuntimeException {
            public GetUserException(String message) {
                super(message);
            }
        }
    }
}

