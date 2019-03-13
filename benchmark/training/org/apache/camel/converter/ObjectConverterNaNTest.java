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


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class ObjectConverterNaNTest extends ContextTestSupport {
    @Test
    public void testDoubleToLongWithNaN() throws Exception {
        Assert.assertEquals(Long.valueOf("4"), context.getTypeConverter().convertTo(Long.class, Double.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Long.class, Double.NaN));
        Assert.assertEquals(Long.valueOf("3"), context.getTypeConverter().convertTo(Long.class, Double.valueOf("3")));
    }

    @Test
    public void testFloatToLongWithNaN() throws Exception {
        Assert.assertEquals(Long.valueOf("4"), context.getTypeConverter().convertTo(Long.class, Float.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Long.class, Float.NaN));
        Assert.assertEquals(Long.valueOf("3"), context.getTypeConverter().convertTo(Long.class, Float.valueOf("3")));
    }

    @Test
    public void testDoubleToIntegerWithNaN() throws Exception {
        Assert.assertEquals(Integer.valueOf("4"), context.getTypeConverter().convertTo(Integer.class, Double.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Integer.class, Double.NaN));
        Assert.assertEquals(Integer.valueOf("3"), context.getTypeConverter().convertTo(Integer.class, Double.valueOf("3")));
    }

    @Test
    public void testFloatToIntegerWithNaN() throws Exception {
        Assert.assertEquals(Integer.valueOf("4"), context.getTypeConverter().convertTo(Integer.class, Float.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Integer.class, Float.NaN));
        Assert.assertEquals(Integer.valueOf("3"), context.getTypeConverter().convertTo(Integer.class, Float.valueOf("3")));
    }

    @Test
    public void testDoubleToShortWithNaN() throws Exception {
        Assert.assertEquals(Short.valueOf("4"), context.getTypeConverter().convertTo(Short.class, Double.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Short.class, Double.NaN));
        Assert.assertEquals(Short.valueOf("3"), context.getTypeConverter().convertTo(Short.class, Double.valueOf("3")));
    }

    @Test
    public void testFloatToShortWithNaN() throws Exception {
        Assert.assertEquals(Short.valueOf("4"), context.getTypeConverter().convertTo(Short.class, Float.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Short.class, Float.NaN));
        Assert.assertEquals(Short.valueOf("3"), context.getTypeConverter().convertTo(Short.class, Float.valueOf("3")));
    }

    @Test
    public void testDoubleToByteWithNaN() throws Exception {
        Assert.assertEquals(Byte.valueOf("4"), context.getTypeConverter().convertTo(Byte.class, Double.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Byte.class, Double.NaN));
        Assert.assertEquals(Byte.valueOf("3"), context.getTypeConverter().convertTo(Byte.class, Double.valueOf("3")));
    }

    @Test
    public void testFloatToByteWithNaN() throws Exception {
        Assert.assertEquals(Byte.valueOf("4"), context.getTypeConverter().convertTo(Byte.class, Float.valueOf("4")));
        Assert.assertEquals(null, context.getTypeConverter().convertTo(Byte.class, Float.NaN));
        Assert.assertEquals(Byte.valueOf("3"), context.getTypeConverter().convertTo(Byte.class, Float.valueOf("3")));
    }

    @Test
    public void testDoubleToFloatWithNaN() throws Exception {
        Assert.assertEquals(Float.valueOf("4"), context.getTypeConverter().convertTo(Float.class, Double.valueOf("4")));
        Assert.assertEquals(((Float) (Float.NaN)), context.getTypeConverter().convertTo(Float.class, Double.NaN));
        Assert.assertEquals(Float.valueOf("3"), context.getTypeConverter().convertTo(Float.class, Double.valueOf("3")));
    }

    @Test
    public void testFloatToDoubleWithNaN() throws Exception {
        Assert.assertEquals(Double.valueOf("4"), context.getTypeConverter().convertTo(Double.class, Float.valueOf("4")));
        Assert.assertEquals(((Double) (Double.NaN)), context.getTypeConverter().convertTo(Double.class, Float.NaN));
        Assert.assertEquals(Double.valueOf("3"), context.getTypeConverter().convertTo(Double.class, Float.valueOf("3")));
    }
}

