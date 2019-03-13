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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import java.io.File;
import java.util.Properties;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.ExpressionIllegalSyntaxException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for writing done files
 */
public class FilerProducerDoneFileNameTest extends ContextTestSupport {
    private Properties myProp = new Properties();

    @Test
    public void testProducerConstantDoneFileName() throws Exception {
        template.sendBodyAndHeader("file:target/data/done?doneFileName=done", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/done/hello.txt");
        Assert.assertEquals("File should exists", true, file.exists());
        File done = new File("target/data/done/done");
        Assert.assertEquals("Done file should exists", true, done.exists());
    }

    @Test
    public void testProducerPrefixDoneFileName() throws Exception {
        template.sendBodyAndHeader("file:target/data/done?doneFileName=done-${file:name}", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/done/hello.txt");
        Assert.assertEquals("File should exists", true, file.exists());
        File done = new File("target/data/done/done-hello.txt");
        Assert.assertEquals("Done file should exists", true, done.exists());
    }

    @Test
    public void testProducerExtDoneFileName() throws Exception {
        template.sendBodyAndHeader("file:target/data/done?doneFileName=${file:name}.done", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/done/hello.txt");
        Assert.assertEquals("File should exists", true, file.exists());
        File done = new File("target/data/done/hello.txt.done");
        Assert.assertEquals("Done file should exists", true, done.exists());
    }

    @Test
    public void testProducerReplaceExtDoneFileName() throws Exception {
        template.sendBodyAndHeader("file:target/data/done?doneFileName=${file:name.noext}.done", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/done/hello.txt");
        Assert.assertEquals("File should exists", true, file.exists());
        File done = new File("target/data/done/hello.done");
        Assert.assertEquals("Done file should exists", true, done.exists());
    }

    @Test
    public void testProducerInvalidDoneFileName() throws Exception {
        try {
            template.sendBodyAndHeader("file:target/data/done?doneFileName=${file:parent}/foo", "Hello World", FILE_NAME, "hello.txt");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            ExpressionIllegalSyntaxException cause = TestSupport.assertIsInstanceOf(ExpressionIllegalSyntaxException.class, e.getCause());
            Assert.assertTrue(cause.getMessage(), cause.getMessage().endsWith("Cannot resolve reminder: ${file:parent}/foo"));
        }
    }

    @Test
    public void testProducerEmptyDoneFileName() throws Exception {
        try {
            template.sendBodyAndHeader("file:target/data/done?doneFileName=", "Hello World", FILE_NAME, "hello.txt");
            Assert.fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            IllegalArgumentException cause = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertTrue(cause.getMessage(), cause.getMessage().startsWith("doneFileName must be specified and not empty"));
        }
    }

    @Test
    public void testProducerPlaceholderPrefixDoneFileName() throws Exception {
        myProp.put("myDir", "target/data/done");
        template.sendBodyAndHeader("file:{{myDir}}?doneFileName=done-${file:name}", "Hello World", FILE_NAME, "hello.txt");
        File file = new File("target/data/done/hello.txt");
        Assert.assertEquals("File should exists", true, file.exists());
        File done = new File("target/data/done/done-hello.txt");
        Assert.assertEquals("Done file should exists", true, done.exists());
    }
}

