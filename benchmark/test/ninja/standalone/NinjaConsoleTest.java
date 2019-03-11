/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.standalone;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NinjaConsoleTest {
    @Test
    public void ninjaPropertiesThrowsExceptionUntilConfigured() throws Exception {
        NinjaConsole console = new NinjaConsole();
        try {
            console.getNinjaProperties();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("configure() not called"));
        }
        console.configure();
        Assert.assertThat(console.getNinjaProperties(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void start() throws Exception {
        NinjaConsole console = new NinjaConsole();
        try {
            console.getInjector();
            Assert.fail("exception expected");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("start() not called"));
        }
        console.start();
        try {
            Assert.assertThat(console.getInjector(), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
        } finally {
            console.shutdown();
        }
    }
}

