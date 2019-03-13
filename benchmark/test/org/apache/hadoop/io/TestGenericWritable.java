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
package org.apache.hadoop.io;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * TestCase for {@link GenericWritable} class.
 *
 * @see TestWritable#testWritable(Writable)
 */
public class TestGenericWritable {
    private Configuration conf;

    public static final String CONF_TEST_KEY = "test.generic.writable";

    public static final String CONF_TEST_VALUE = "dummy";

    /**
     * Dummy class for testing {@link GenericWritable}
     */
    public static class Foo implements Writable {
        private String foo = "foo";

        @Override
        public void readFields(DataInput in) throws IOException {
            foo = Text.readString(in);
        }

        @Override
        public void write(DataOutput out) throws IOException {
            Text.writeString(out, foo);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestGenericWritable.Foo))
                return false;

            return this.foo.equals(((TestGenericWritable.Foo) (obj)).foo);
        }
    }

    /**
     * Dummy class for testing {@link GenericWritable}
     */
    public static class Bar implements Configurable , Writable {
        private int bar = 42;// The Answer to The Ultimate Question Of Life, the Universe and Everything


        private Configuration conf = null;

        @Override
        public void readFields(DataInput in) throws IOException {
            bar = in.readInt();
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(bar);
        }

        @Override
        public Configuration getConf() {
            return conf;
        }

        @Override
        public void setConf(Configuration conf) {
            this.conf = conf;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestGenericWritable.Bar))
                return false;

            return (this.bar) == (((TestGenericWritable.Bar) (obj)).bar);
        }
    }

    /**
     * Dummy class for testing {@link GenericWritable}
     */
    public static class Baz extends TestGenericWritable.Bar {
        @Override
        public void readFields(DataInput in) throws IOException {
            super.readFields(in);
            // needs a configuration parameter
            Assert.assertEquals("Configuration is not set for the wrapped object", TestGenericWritable.CONF_TEST_VALUE, getConf().get(TestGenericWritable.CONF_TEST_KEY));
        }

        @Override
        public void write(DataOutput out) throws IOException {
            super.write(out);
        }
    }

    /**
     * Dummy class for testing {@link GenericWritable}
     */
    public static class FooGenericWritable extends GenericWritable {
        @Override
        @SuppressWarnings("unchecked")
        protected Class<? extends Writable>[] getTypes() {
            return new Class[]{ TestGenericWritable.Foo.class, TestGenericWritable.Bar.class, TestGenericWritable.Baz.class };
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestGenericWritable.FooGenericWritable))
                return false;

            return get().equals(get());
        }
    }

    @Test
    public void testFooWritable() throws Exception {
        System.out.println("Testing Writable wrapped in GenericWritable");
        TestGenericWritable.FooGenericWritable generic = new TestGenericWritable.FooGenericWritable();
        generic.setConf(conf);
        TestGenericWritable.Foo foo = new TestGenericWritable.Foo();
        set(foo);
        TestWritable.testWritable(generic);
    }

    @Test
    public void testBarWritable() throws Exception {
        System.out.println("Testing Writable, Configurable wrapped in GenericWritable");
        TestGenericWritable.FooGenericWritable generic = new TestGenericWritable.FooGenericWritable();
        generic.setConf(conf);
        TestGenericWritable.Bar bar = new TestGenericWritable.Bar();
        bar.setConf(conf);
        generic.set(bar);
        // test writing generic writable
        TestGenericWritable.FooGenericWritable after = ((TestGenericWritable.FooGenericWritable) (TestWritable.testWritable(generic, conf)));
        // test configuration
        System.out.println("Testing if Configuration is passed to wrapped classes");
        Assert.assertTrue(((get()) instanceof Configurable));
        Assert.assertNotNull(getConf());
    }

    @Test
    public void testBazWritable() throws Exception {
        System.out.println("Testing for GenericWritable to find class names");
        TestGenericWritable.FooGenericWritable generic = new TestGenericWritable.FooGenericWritable();
        generic.setConf(conf);
        TestGenericWritable.Baz baz = new TestGenericWritable.Baz();
        generic.set(baz);
        TestWritable.testWritable(generic, conf);
    }

    @Test
    public void testSet() throws Exception {
        TestGenericWritable.Foo foo = new TestGenericWritable.Foo();
        TestGenericWritable.FooGenericWritable generic = new TestGenericWritable.FooGenericWritable();
        // exception should not occur
        set(foo);
        try {
            // exception should occur, since IntWritable is not registered
            generic = new TestGenericWritable.FooGenericWritable();
            generic.set(new IntWritable(1));
            Assert.fail("Generic writable should have thrown an exception for a Writable not registered");
        } catch (RuntimeException e) {
            // ignore
        }
    }

    @Test
    public void testGet() throws Exception {
        TestGenericWritable.Foo foo = new TestGenericWritable.Foo();
        TestGenericWritable.FooGenericWritable generic = new TestGenericWritable.FooGenericWritable();
        set(foo);
        Assert.assertEquals(foo, get());
    }
}

