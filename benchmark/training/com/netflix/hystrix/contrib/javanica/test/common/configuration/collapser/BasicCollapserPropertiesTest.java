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
package com.netflix.hystrix.contrib.javanica.test.common.configuration.collapser;


import HystrixEventType.COLLAPSED;
import HystrixEventType.SUCCESS;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.test.common.BasicHystrixTest;
import com.netflix.hystrix.contrib.javanica.test.common.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by dmgcodevil
 */
public abstract class BasicCollapserPropertiesTest extends BasicHystrixTest {
    private BasicCollapserPropertiesTest.UserService userService;

    @Test
    public void testCollapser() throws InterruptedException, ExecutionException {
        User u1 = userService.getUser("1");
        User u2 = userService.getUser("2");
        User u3 = userService.getUser("3");
        User u4 = userService.getUser("4");
        Assert.assertEquals("name: 1", u1.getName());
        Assert.assertEquals("name: 2", u2.getName());
        Assert.assertEquals("name: 3", u3.getName());
        Assert.assertEquals("name: 4", u4.getName());
        Assert.assertEquals(4, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
        HystrixInvokableInfo<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().iterator().next();
        Assert.assertEquals("getUsers", command.getCommandKey().name());
        // confirm that it was a COLLAPSED command execution
        Assert.assertTrue(command.getExecutionEvents().contains(COLLAPSED));
        // and that it was successful
        Assert.assertTrue(command.getExecutionEvents().contains(SUCCESS));
    }

    public static class UserService {
        @HystrixCollapser(batchMethod = "getUsers", collapserKey = "GetUserCollapser", collapserProperties = { @HystrixProperty(name = TIMER_DELAY_IN_MILLISECONDS, value = "200"), @HystrixProperty(name = MAX_REQUESTS_IN_BATCH, value = "1") })
        public User getUser(String id) {
            return null;
        }

        @HystrixCommand
        public List<User> getUsers(List<String> ids) {
            List<User> users = new ArrayList<User>();
            for (String id : ids) {
                users.add(new User(id, ("name: " + id)));
            }
            return users;
        }
    }
}

