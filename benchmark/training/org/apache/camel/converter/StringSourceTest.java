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
package org.apache.camel.converter;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.camel.StringSource;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.impl.DefaultFactoryFinderResolver;
import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.util.ReflectionInjector;
import org.junit.Assert;
import org.junit.Test;


public class StringSourceTest extends Assert {
    protected TypeConverter converter = new org.apache.camel.impl.converter.DefaultTypeConverter(new DefaultPackageScanClassResolver(), new ReflectionInjector(), new DefaultFactoryFinderResolver().resolveDefaultFactoryFinder(new DefaultClassResolver()), false);

    protected String expectedBody = "<hello>world!</hello>";

    @Test
    public void testSerialization() throws Exception {
        StringSource expected = new StringSource(expectedBody, "mySystemID", "utf-8");
        expected.setPublicId("myPublicId");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(expected);
        output.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        Object object = in.readObject();
        Assert.assertTrue("is a StringSource", (object instanceof StringSource));
        StringSource actual = ((StringSource) (object));
        Assert.assertEquals("source.text", expected.getPublicId(), actual.getPublicId());
        Assert.assertEquals("source.text", expected.getSystemId(), actual.getSystemId());
        Assert.assertEquals("source.text", expected.getEncoding(), actual.getEncoding());
        Assert.assertEquals("source.text", expected.getText(), actual.getText());
        String value = converter.convertTo(String.class, actual);
        Assert.assertEquals("text value of StringSource", expectedBody, value);
    }
}

