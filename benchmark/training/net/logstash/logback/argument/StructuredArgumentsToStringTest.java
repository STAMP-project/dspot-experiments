/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.logstash.logback.argument;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.helpers.MessageFormatter;


@RunWith(Parameterized.class)
public class StructuredArgumentsToStringTest {
    private static class BuguyToString {
        @Override
        public String toString() {
            throw new NullPointerException("npe") {
                @Override
                public synchronized Throwable fillInStackTrace() {
                    return this;
                }
            };
        }
    }

    @Parameterized.Parameter(0)
    public String testName;

    @Parameterized.Parameter(1)
    public String expected;

    @Parameterized.Parameter(2)
    public Object arg;

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals(expected, StructuredArguments.toString(arg));
        Assert.assertEquals(MessageFormatter.format("{}", arg).getMessage(), StructuredArguments.toString(arg));
    }
}

