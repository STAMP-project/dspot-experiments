/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix;


import HystrixCommandGroupKey.Factory;
import HystrixEventType.RESPONSE_FROM_CACHE;
import com.hystrix.junit.HystrixRequestContextRule;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HystrixSubclassCommandTest {
    private static final HystrixCommandGroupKey groupKey = Factory.asKey("GROUP");

    @Rule
    public HystrixRequestContextRule ctx = new HystrixRequestContextRule();

    @Test
    public void testFallback() {
        HystrixCommand<Integer> superCmd = new HystrixSubclassCommandTest.SuperCommand("cache", false);
        Assert.assertEquals(2, superCmd.execute().intValue());
        HystrixCommand<Integer> subNoOverridesCmd = new HystrixSubclassCommandTest.SubCommandNoOverride("cache", false);
        Assert.assertEquals(2, subNoOverridesCmd.execute().intValue());
        HystrixCommand<Integer> subOverriddenFallbackCmd = new HystrixSubclassCommandTest.SubCommandOverrideFallback("cache", false);
        Assert.assertEquals(3, subOverriddenFallbackCmd.execute().intValue());
    }

    @Test
    public void testRequestCacheSuperClass() {
        HystrixCommand<Integer> superCmd1 = new HystrixSubclassCommandTest.SuperCommand("cache", true);
        Assert.assertEquals(1, superCmd1.execute().intValue());
        HystrixCommand<Integer> superCmd2 = new HystrixSubclassCommandTest.SuperCommand("cache", true);
        Assert.assertEquals(1, superCmd2.execute().intValue());
        HystrixCommand<Integer> superCmd3 = new HystrixSubclassCommandTest.SuperCommand("no-cache", true);
        Assert.assertEquals(1, superCmd3.execute().intValue());
        System.out.println(("REQ LOG : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        HystrixRequestLog reqLog = HystrixRequestLog.getCurrentRequest();
        Assert.assertEquals(3, reqLog.getAllExecutedCommands().size());
        List<HystrixInvokableInfo<?>> infos = new ArrayList<HystrixInvokableInfo<?>>(reqLog.getAllExecutedCommands());
        HystrixInvokableInfo<?> info1 = infos.get(0);
        Assert.assertEquals("SuperCommand", info1.getCommandKey().name());
        Assert.assertEquals(1, info1.getExecutionEvents().size());
        HystrixInvokableInfo<?> info2 = infos.get(1);
        Assert.assertEquals("SuperCommand", info2.getCommandKey().name());
        Assert.assertEquals(2, info2.getExecutionEvents().size());
        Assert.assertEquals(RESPONSE_FROM_CACHE, info2.getExecutionEvents().get(1));
        HystrixInvokableInfo<?> info3 = infos.get(2);
        Assert.assertEquals("SuperCommand", info3.getCommandKey().name());
        Assert.assertEquals(1, info3.getExecutionEvents().size());
    }

    @Test
    public void testRequestCacheSubclassNoOverrides() {
        HystrixCommand<Integer> subCmd1 = new HystrixSubclassCommandTest.SubCommandNoOverride("cache", true);
        Assert.assertEquals(1, subCmd1.execute().intValue());
        HystrixCommand<Integer> subCmd2 = new HystrixSubclassCommandTest.SubCommandNoOverride("cache", true);
        Assert.assertEquals(1, subCmd2.execute().intValue());
        HystrixCommand<Integer> subCmd3 = new HystrixSubclassCommandTest.SubCommandNoOverride("no-cache", true);
        Assert.assertEquals(1, subCmd3.execute().intValue());
        System.out.println(("REQ LOG : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        HystrixRequestLog reqLog = HystrixRequestLog.getCurrentRequest();
        Assert.assertEquals(3, reqLog.getAllExecutedCommands().size());
        List<HystrixInvokableInfo<?>> infos = new ArrayList<HystrixInvokableInfo<?>>(reqLog.getAllExecutedCommands());
        HystrixInvokableInfo<?> info1 = infos.get(0);
        Assert.assertEquals("SubCommandNoOverride", info1.getCommandKey().name());
        Assert.assertEquals(1, info1.getExecutionEvents().size());
        HystrixInvokableInfo<?> info2 = infos.get(1);
        Assert.assertEquals("SubCommandNoOverride", info2.getCommandKey().name());
        Assert.assertEquals(2, info2.getExecutionEvents().size());
        Assert.assertEquals(RESPONSE_FROM_CACHE, info2.getExecutionEvents().get(1));
        HystrixInvokableInfo<?> info3 = infos.get(2);
        Assert.assertEquals("SubCommandNoOverride", info3.getCommandKey().name());
        Assert.assertEquals(1, info3.getExecutionEvents().size());
    }

    @Test
    public void testRequestLogSuperClass() {
        HystrixCommand<Integer> superCmd = new HystrixSubclassCommandTest.SuperCommand("cache", true);
        Assert.assertEquals(1, superCmd.execute().intValue());
        System.out.println(("REQ LOG : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        HystrixRequestLog reqLog = HystrixRequestLog.getCurrentRequest();
        Assert.assertEquals(1, reqLog.getAllExecutedCommands().size());
        HystrixInvokableInfo<?> info = reqLog.getAllExecutedCommands().iterator().next();
        Assert.assertEquals("SuperCommand", info.getCommandKey().name());
    }

    @Test
    public void testRequestLogSubClassNoOverrides() {
        HystrixCommand<Integer> subCmd = new HystrixSubclassCommandTest.SubCommandNoOverride("cache", true);
        Assert.assertEquals(1, subCmd.execute().intValue());
        System.out.println(("REQ LOG : " + (HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString())));
        HystrixRequestLog reqLog = HystrixRequestLog.getCurrentRequest();
        Assert.assertEquals(1, reqLog.getAllExecutedCommands().size());
        HystrixInvokableInfo<?> info = reqLog.getAllExecutedCommands().iterator().next();
        Assert.assertEquals("SubCommandNoOverride", info.getCommandKey().name());
    }

    public static class SuperCommand extends HystrixCommand<Integer> {
        private final String uniqueArg;

        private final boolean shouldSucceed;

        SuperCommand(String uniqueArg, boolean shouldSucceed) {
            super(Setter.withGroupKey(HystrixSubclassCommandTest.groupKey));
            this.uniqueArg = uniqueArg;
            this.shouldSucceed = shouldSucceed;
        }

        @Override
        protected Integer run() throws Exception {
            if (shouldSucceed) {
                return 1;
            } else {
                throw new RuntimeException("unit test failure");
            }
        }

        @Override
        protected Integer getFallback() {
            return 2;
        }

        @Override
        protected String getCacheKey() {
            return uniqueArg;
        }
    }

    public static class SubCommandNoOverride extends HystrixSubclassCommandTest.SuperCommand {
        SubCommandNoOverride(String uniqueArg, boolean shouldSucceed) {
            super(uniqueArg, shouldSucceed);
        }
    }

    public static class SubCommandOverrideFallback extends HystrixSubclassCommandTest.SuperCommand {
        SubCommandOverrideFallback(String uniqueArg, boolean shouldSucceed) {
            super(uniqueArg, shouldSucceed);
        }

        @Override
        protected Integer getFallback() {
            return 3;
        }
    }
}

