/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.common;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonUtilTest {
    private static final Logger log = LoggerFactory.getLogger(JsonUtilTest.class);

    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testOpenBraceComesFirst() {
        Assert.assertTrue(JsonUtil.openBraceComesFirst("{}"));
        Assert.assertTrue(JsonUtil.openBraceComesFirst(" \t{\"foo\":\"bar\"}"));
        Assert.assertTrue(JsonUtil.openBraceComesFirst(" { \"foo\": \"bar\" }"));
        Assert.assertFalse(JsonUtil.openBraceComesFirst("/my/file/path"));
        Assert.assertFalse(JsonUtil.openBraceComesFirst("mypath"));
        Assert.assertFalse(JsonUtil.openBraceComesFirst(" blah{}"));
    }

    static final class Foo {
        @JsonProperty
        final int bar;

        @JsonCreator
        Foo(@JsonProperty("bar")
        int bar) {
            this.bar = bar;
        }
    }

    @Test
    public void testObjectFromCommandLineArgument() throws Exception {
        Assert.assertEquals(123, JsonUtil.<JsonUtilTest.Foo>objectFromCommandLineArgument("{\"bar\":123}", JsonUtilTest.Foo.class).bar);
        Assert.assertEquals(1, JsonUtil.<JsonUtilTest.Foo>objectFromCommandLineArgument("   {\"bar\": 1}   ", JsonUtilTest.Foo.class).bar);
        File tempFile = TestUtils.tempFile();
        try {
            Files.write(tempFile.toPath(), "{\"bar\": 456}".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(456, JsonUtil.<JsonUtilTest.Foo>objectFromCommandLineArgument(tempFile.getAbsolutePath(), JsonUtilTest.Foo.class).bar);
        } finally {
            Files.delete(tempFile.toPath());
        }
    }
}

