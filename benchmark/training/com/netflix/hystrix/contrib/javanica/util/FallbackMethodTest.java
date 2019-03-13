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
package com.netflix.hystrix.contrib.javanica.util;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.utils.MethodProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by dmgcodevil.
 */
@RunWith(DataProviderRunner.class)
public class FallbackMethodTest {
    @Test
    public void testGetExtendedFallback() throws NoSuchMethodException {
        // given
        Method command = FallbackMethodTest.Service.class.getDeclaredMethod("command", String.class, Integer.class);
        // when
        Method extFallback = MethodProvider.getInstance().getFallbackMethod(FallbackMethodTest.Service.class, command).getMethod();
        // then
        FallbackMethodTest.assertParamsTypes(extFallback, String.class, Integer.class, Throwable.class);
    }

    private static class Common {
        private String fallback(String s, Integer i) {
            return null;
        }

        private String fallbackV2(String s, Integer i) {
            return null;
        }
    }

    private static class Service extends FallbackMethodTest.Common {
        @HystrixCommand(fallbackMethod = "fallback")
        public String command(String s, Integer i) {
            return null;
        }

        @HystrixCommand(fallbackMethod = "fallback")
        public String extCommand(String s, Integer i, Throwable throwable) {
            return null;
        }

        @HystrixCommand(fallbackMethod = "fallbackV2")
        public String extCommandV2(String s, Integer i, Throwable throwable) {
            return null;
        }

        public String fallback(String s, Integer i, Throwable throwable) {
            return null;
        }
    }
}

