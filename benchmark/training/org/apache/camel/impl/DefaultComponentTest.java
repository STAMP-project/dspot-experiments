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
package org.apache.camel.impl;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchBeanException;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.DefaultComponent;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for helper methods on the DefaultComponent.
 */
public class DefaultComponentTest extends ContextTestSupport {
    private static final class MyComponent extends DefaultComponent {
        private MyComponent(CamelContext context) {
            super(context);
        }

        protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
            return null;
        }
    }

    @Test
    public void testGetAndRemoveParameterEmptyMap() {
        Map<String, Object> parameters = new HashMap<>();
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = my.getAndRemoveParameter(parameters, "size", Integer.class);
        Assert.assertNull(value);
    }

    @Test
    public void testGetAndRemoveParameterEmptyMapDefault() {
        Map<String, Object> parameters = new HashMap<>();
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = getAndRemoveParameter(parameters, "size", Integer.class, 5);
        Assert.assertEquals(value.intValue(), 5);
    }

    @Test
    public void testGetAndRemoveParameterEmptyMapDefaultIsNull() {
        Map<String, Object> parameters = new HashMap<>();
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = my.getAndRemoveParameter(parameters, "size", Integer.class, null);
        Assert.assertNull(value);
    }

    @Test
    public void testGetAndRemoveParameterToInteger() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("size", 200);
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = my.getAndRemoveParameter(parameters, "size", Integer.class);
        Assert.assertEquals(value.intValue(), 200);
    }

    @Test
    public void testGetAndRemoveParameterToIntegerDefault() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("size", 200);
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = getAndRemoveParameter(parameters, "level", Integer.class, 4);
        Assert.assertEquals(value.intValue(), 4);
    }

    @Test
    public void testResolveAndRemoveReferenceParameter() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", "#beginning");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Date value = resolveAndRemoveReferenceParameter(parameters, "date", Date.class);
        Assert.assertEquals(new Date(0), value);
        // usage of leading # is optional
        parameters.put("date", "beginning");
        value = resolveAndRemoveReferenceParameter(parameters, "date", Date.class);
        Assert.assertEquals(new Date(0), value);
    }

    @Test
    public void testResolveAndRemoveReferenceParameterWithConversion() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("number", "#numeric");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = my.resolveAndRemoveReferenceParameter(parameters, "number", Integer.class);
        Assert.assertEquals(12345, value.intValue());
    }

    @Test
    public void testResolveAndRemoveReferenceParameterWithFailedConversion() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("number", "#non-numeric");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        try {
            my.resolveAndRemoveReferenceParameter(parameters, "number", Integer.class);
        } catch (TypeConversionException ex) {
            Assert.assertEquals(("Error during type conversion from type: java.lang.String " + ("to the required type: java.lang.Integer " + "with value abc due to java.lang.NumberFormatException: For input string: \"abc\"")), ex.getMessage());
        }
    }

    @Test
    public void testResolveAndRemoveReferenceParameterNotInRegistry() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", "#somewhen");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        try {
            my.resolveAndRemoveReferenceParameter(parameters, "date", Date.class);
            Assert.fail("returned without finding object in registry");
        } catch (NoSuchBeanException e) {
            Assert.assertEquals("No bean could be found in the registry for: somewhen of type: java.util.Date", e.getMessage());
        }
    }

    @Test
    public void testResolveAndRemoveReferenceParameterNotInMapDefault() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", "#beginning");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Date value = my.resolveAndRemoveReferenceParameter(parameters, "wrong", Date.class, new Date(1));
        Assert.assertEquals(new Date(1), value);
    }

    @Test
    public void testResolveAndRemoveReferenceParameterNotInMapNull() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", "#beginning");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Date value = resolveAndRemoveReferenceParameter(parameters, "wrong", Date.class);
        Assert.assertNull(value);
    }

    @Test
    public void testResolveAndRemoveReferenceListParameterElement() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dates", "#bean1");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        List<Date> values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
    }

    @Test
    public void testResolveAndRemoveReferenceListParameterListComma() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dates", "#bean1,#bean2");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        List<Date> values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
        Assert.assertEquals(new Date(11), values.get(1));
        // usage of leading # is optional
        parameters.put("dates", "bean1,bean2");
        values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
        Assert.assertEquals(new Date(11), values.get(1));
    }

    @Test
    public void testResolveAndRemoveReferenceListParameterListCommaTrim() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dates", " #bean1 , #bean2 ");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        List<Date> values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
        Assert.assertEquals(new Date(11), values.get(1));
        // usage of leading # is optional
        parameters.put("dates", " bean1 , bean2 ");
        values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
        Assert.assertEquals(new Date(11), values.get(1));
    }

    @Test
    public void testResolveAndRemoveReferenceListParameterListBean() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dates", "#listBean");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        List<Date> values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
        Assert.assertEquals(new Date(11), values.get(1));
        // usage of leading # is optional
        parameters.put("dates", "#listBean");
        values = resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals(new Date(10), values.get(0));
        Assert.assertEquals(new Date(11), values.get(1));
    }

    @Test
    public void testResolveAndRemoveReferenceListParameterInvalidBean() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dates", "#bean1,#bean3");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        try {
            my.resolveAndRemoveReferenceListParameter(parameters, "dates", Date.class);
            Assert.fail("returned without finding object in registry");
        } catch (NoSuchBeanException e) {
            Assert.assertEquals("No bean could be found in the registry for: bean3 of type: java.util.Date", e.getMessage());
        }
    }

    @Test
    public void testGetAndRemoveOrResolveReferenceParameter() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("size", 123);
        parameters.put("date", "#bean1");
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(this.context);
        Integer value = my.getAndRemoveOrResolveReferenceParameter(parameters, "size", Integer.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(123, value.intValue());
        Assert.assertEquals(1, parameters.size());
        Date bean1 = my.getAndRemoveOrResolveReferenceParameter(parameters, "date", Date.class);
        Assert.assertNotNull(bean1);
        Assert.assertEquals(new Date(10), bean1);
        Assert.assertEquals(0, parameters.size());
        Integer age = getAndRemoveOrResolveReferenceParameter(parameters, "age", Integer.class, 7);
        Assert.assertNotNull(age);
        Assert.assertEquals(7, age.intValue());
    }

    @Test
    public void testContextShouldBeSet() throws Exception {
        DefaultComponentTest.MyComponent my = new DefaultComponentTest.MyComponent(null);
        try {
            start();
            Assert.fail("Should have thrown a IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("camelContext must be specified", e.getMessage());
        }
    }
}

