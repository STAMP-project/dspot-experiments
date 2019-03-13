/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.ssh;


import org.junit.Assert;
import org.junit.Test;


public class SshShellOutputStringHelperTest extends Assert {
    @Test
    public void testBeforeLast() {
        Assert.assertEquals("Hello ", SshShellOutputStringHelper.beforeLast("Hello World", "World"));
        Assert.assertEquals("Hello World ", SshShellOutputStringHelper.beforeLast("Hello World World", "World"));
        Assert.assertEquals("Hello ", SshShellOutputStringHelper.beforeLast("Hello World Again", "World"));
        Assert.assertEquals(null, SshShellOutputStringHelper.beforeLast("Hello Again", "Foo"));
        Assert.assertTrue(SshShellOutputStringHelper.beforeLast("mykey:ignore:hello", ":", "mykey:ignore"::equals).orElse(false));
        Assert.assertFalse(SshShellOutputStringHelper.beforeLast("ignore:ignore:world", ":", "mykey"::equals).orElse(false));
    }

    @Test
    public void testBetweenBeforeLast() {
        Assert.assertEquals("foo bar' how are", SshShellOutputStringHelper.betweenBeforeLast("Hello 'foo bar' how are' you", "'", "'"));
        Assert.assertEquals("foo bar", SshShellOutputStringHelper.betweenBeforeLast("Hello ${foo bar} how are you", "${", "}"));
        Assert.assertEquals(null, SshShellOutputStringHelper.betweenBeforeLast("Hello ${foo bar} how are you", "'", "'"));
        Assert.assertTrue(SshShellOutputStringHelper.betweenBeforeLast("begin:mykey:end:end", "begin:", ":end", "mykey:end"::equals).orElse(false));
        Assert.assertFalse(SshShellOutputStringHelper.betweenBeforeLast("begin:ignore:end:end", "begin:", ":end", "mykey"::equals).orElse(false));
    }
}

