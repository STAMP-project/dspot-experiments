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
package com.twitter.elephantbird.pig.piggybank;


import java.io.IOException;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * need more tests -- non-String funcs and especially the full path through the pig interpreter.
 * I tested manually, seems to work, but
 * should really add more here.
 */
public class TestInvoker {
    private final TupleFactory tf_ = TupleFactory.getInstance();

    @Test
    public void testStringInvoker() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        // Test non-static method
        InvokeForString is = new InvokeForString("java.lang.String.toUpperCase", "String", "false");
        Assert.assertEquals("FOO", is.exec(tf_.newTuple("foo")));
        // both "static" and "true" should work
        // Test static method
        is = new InvokeForString("java.lang.String.valueOf", "int", "true");
        Tuple t = tf_.newTuple(1);
        t.set(0, 231);
        Assert.assertEquals("231", is.exec(t));
        // test default (should be static)
        is = new InvokeForString("java.lang.String.valueOf", "int");
        Assert.assertEquals("231", is.exec(t));
        // Test method with multiple args
        is = new InvokeForString(((TestInvoker.class.getName()) + ".concatStrings"), "String String");
        t = tf_.newTuple(2);
        t.set(0, "foo");
        t.set(1, "bar");
        Assert.assertEquals("foobar", is.exec(t));
    }

    @Test
    public void testNoArgInvoker() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        InvokeForInt id = new InvokeForInt(((TestInvoker.class.getName()) + ".simpleStaticFunction"));
        Assert.assertEquals(Integer.valueOf(1), id.exec(tf_.newTuple()));
    }

    @Test
    public void testLongInvoker() throws IOException, ClassNotFoundException, NoSuchMethodException, NumberFormatException, SecurityException {
        InvokeForLong il = new InvokeForLong("java.lang.Long.valueOf", "String");
        Tuple t = tf_.newTuple(1);
        String arg = "245";
        t.set(0, arg);
        Assert.assertEquals(Long.valueOf(arg), il.exec(t));
    }

    @Test
    public void testIntInvoker() throws IOException, ClassNotFoundException, NoSuchMethodException, NumberFormatException, SecurityException {
        InvokeForInt il = new InvokeForInt("java.lang.Integer.valueOf", "String");
        Tuple t = tf_.newTuple(1);
        String arg = "245";
        t.set(0, arg);
        Assert.assertEquals(Integer.valueOf(arg), il.exec(t));
    }

    @Test
    public void testArrayConversion() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        InvokeForInt id = new InvokeForInt(((TestInvoker.class.getName()) + ".avg"), "double[]");
        DataBag nums = newSimpleBag(1.0, 2.0, 3.0);
        Assert.assertEquals(Integer.valueOf(2), id.exec(tf_.newTuple(nums)));
        InvokeForString is = new InvokeForString(((TestInvoker.class.getName()) + ".concatStringArray"), "string[]");
        DataBag strings = newSimpleBag("foo", "bar", "baz");
        Assert.assertEquals("foobarbaz", is.exec(tf_.newTuple(strings)));
    }

    @Test
    public void testDoubleInvoker() throws IOException, ClassNotFoundException, NoSuchMethodException, NumberFormatException, SecurityException {
        InvokeForDouble il = new InvokeForDouble("java.lang.Double.valueOf", "String");
        Tuple t = tf_.newTuple(1);
        String arg = "245";
        t.set(0, arg);
        Assert.assertEquals(Double.valueOf(arg), il.exec(t));
    }

    @Test
    public void testFloatInvoker() throws IOException, ClassNotFoundException, NoSuchMethodException, NumberFormatException, SecurityException {
        InvokeForFloat il = new InvokeForFloat("java.lang.Float.valueOf", "String");
        Tuple t = tf_.newTuple(1);
        String arg = "245.3";
        t.set(0, arg);
        Assert.assertEquals(Float.valueOf(arg), il.exec(t));
    }

    private class Log extends EvalFunc<Double> {
        @Override
        public Double exec(Tuple input) throws IOException {
            Double d = ((Double) (input.get(0)));
            return Math.log(d);
        }
    }
}

