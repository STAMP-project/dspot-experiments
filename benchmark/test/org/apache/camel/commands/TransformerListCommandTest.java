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
package org.apache.camel.commands;


import org.apache.camel.Message;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.Transformer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransformerListCommandTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransformerListCommandTest.class);

    @Test
    public void testTransformerList() throws Exception {
        String out = doTest(false);
        Assert.assertTrue(out.contains("xml:foo"));
        Assert.assertTrue(out.contains("json:bar"));
        Assert.assertTrue(out.contains(("java:" + (this.getClass().getName()))));
        Assert.assertTrue(out.contains("xml:test"));
        Assert.assertTrue(out.contains("custom"));
        Assert.assertTrue(out.contains("Started"));
        Assert.assertFalse(out.contains("ProcessorTransformer["));
        Assert.assertFalse(out.contains("DataFormatTransformer["));
        Assert.assertFalse(out.contains("MyTransformer["));
    }

    @Test
    public void testTransformerListVerbose() throws Exception {
        String out = doTest(true);
        Assert.assertTrue(out.contains("xml:foo"));
        Assert.assertTrue(out.contains("json:bar"));
        Assert.assertTrue(out.contains(("java:" + (this.getClass().getName()))));
        Assert.assertTrue(out.contains("xml:test"));
        Assert.assertTrue(out.contains("custom"));
        Assert.assertTrue(out.contains("Started"));
        Assert.assertTrue(out.contains("ProcessorTransformer["));
        Assert.assertTrue(out.contains("DataFormatTransformer["));
        Assert.assertTrue(out.contains("MyTransformer["));
    }

    public static class MyTransformer extends Transformer {
        @Override
        public void transform(Message message, DataType from, DataType to) throws Exception {
            return;
        }
    }
}

