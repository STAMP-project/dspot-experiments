/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.webmonitor.handlers.utils;


import java.util.List;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link JarHandlerUtils}.
 */
public class JarHandlerUtilsTest extends TestLogger {
    @Test
    public void testTokenizeNonQuoted() {
        final List<String> arguments = JarHandlerUtils.tokenizeArguments("--foo bar");
        Assert.assertThat(arguments.get(0), Matchers.equalTo("--foo"));
        Assert.assertThat(arguments.get(1), Matchers.equalTo("bar"));
    }

    @Test
    public void testTokenizeSingleQuoted() {
        final List<String> arguments = JarHandlerUtils.tokenizeArguments("--foo 'bar baz '");
        Assert.assertThat(arguments.get(0), Matchers.equalTo("--foo"));
        Assert.assertThat(arguments.get(1), Matchers.equalTo("bar baz "));
    }

    @Test
    public void testTokenizeDoubleQuoted() {
        final List<String> arguments = JarHandlerUtils.tokenizeArguments("--name \"K. Bote \"");
        Assert.assertThat(arguments.get(0), Matchers.equalTo("--name"));
        Assert.assertThat(arguments.get(1), Matchers.equalTo("K. Bote "));
    }
}

