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
package org.apache.camel.processor;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.impl.SerializationDataFormat;
import org.apache.camel.impl.StringDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.DataFormatFactory;
import org.junit.Assert;
import org.junit.Test;


public class DataFormatFactoryTest extends ContextTestSupport {
    private static final DataFormat STRING_DF = new StringDataFormat("US-ASCII");

    private static final DataFormatFactory STRING_DFF = () -> new StringDataFormat("UTF-8");

    private static final DataFormat SERIALIZATION_DF = new SerializationDataFormat();

    @Test
    public void testDataFormatResolveOrCreate() throws Exception {
        Assert.assertSame(DataFormatFactoryTest.STRING_DF, context.resolveDataFormat("string"));
        Assert.assertNotSame(DataFormatFactoryTest.STRING_DF, context.createDataFormat("string"));
        Assert.assertNotSame(context.createDataFormat("string"), context.createDataFormat("string"));
        Assert.assertSame(DataFormatFactoryTest.SERIALIZATION_DF, context.resolveDataFormat("serialization"));
        Assert.assertNotSame(DataFormatFactoryTest.SERIALIZATION_DF, context.createDataFormat("serialization"));
        Assert.assertNotSame(context.createDataFormat("serialization"), context.createDataFormat("serialization"));
        Assert.assertEquals("US-ASCII", getCharset());
        Assert.assertEquals("UTF-8", getCharset());
    }
}

