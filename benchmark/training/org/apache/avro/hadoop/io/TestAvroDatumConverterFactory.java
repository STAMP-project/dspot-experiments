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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.apache.avro.hadoop.io;


import Schema.Type.INT;
import Schema.Type.STRING;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroDatumConverterFactory {
    private Job mJob;

    private AvroDatumConverterFactory mFactory;

    @Test
    public void testConvertAvroKey() throws IOException {
        AvroJob.setOutputKeySchema(mJob, Schema.create(STRING));
        AvroKey<CharSequence> avroKey = new AvroKey("foo");
        @SuppressWarnings("unchecked")
        AvroDatumConverter<AvroKey<CharSequence>, ?> converter = mFactory.create(((Class<AvroKey<CharSequence>>) (avroKey.getClass())));
        Assert.assertEquals("foo", converter.convert(avroKey).toString());
    }

    @Test
    public void testConvertAvroValue() throws IOException {
        AvroJob.setOutputValueSchema(mJob, Schema.create(INT));
        AvroValue<Integer> avroValue = new AvroValue(42);
        @SuppressWarnings("unchecked")
        AvroDatumConverter<AvroValue<Integer>, Integer> converter = mFactory.create(((Class<AvroValue<Integer>>) (avroValue.getClass())));
        Assert.assertEquals(42, converter.convert(avroValue).intValue());
    }

    @Test
    public void testConvertBooleanWritable() {
        AvroDatumConverter<BooleanWritable, Boolean> converter = mFactory.create(BooleanWritable.class);
        Assert.assertEquals(true, converter.convert(new BooleanWritable(true)).booleanValue());
    }

    @Test
    public void testConvertBytesWritable() {
        AvroDatumConverter<BytesWritable, ByteBuffer> converter = mFactory.create(BytesWritable.class);
        ByteBuffer bytes = converter.convert(new BytesWritable(new byte[]{ 1, 2, 3 }));
        Assert.assertEquals(1, bytes.get(0));
        Assert.assertEquals(2, bytes.get(1));
        Assert.assertEquals(3, bytes.get(2));
    }

    @Test
    public void testConvertByteWritable() {
        AvroDatumConverter<ByteWritable, GenericFixed> converter = mFactory.create(ByteWritable.class);
        Assert.assertEquals(42, converter.convert(new ByteWritable(((byte) (42)))).bytes()[0]);
    }

    @Test
    public void testConvertDoubleWritable() {
        AvroDatumConverter<DoubleWritable, Double> converter = mFactory.create(DoubleWritable.class);
        Assert.assertEquals(2.0, converter.convert(new DoubleWritable(2.0)).doubleValue(), 1.0E-5);
    }

    @Test
    public void testConvertFloatWritable() {
        AvroDatumConverter<FloatWritable, Float> converter = mFactory.create(FloatWritable.class);
        Assert.assertEquals(2.2F, converter.convert(new FloatWritable(2.2F)).floatValue(), 1.0E-5);
    }

    @Test
    public void testConvertIntWritable() {
        AvroDatumConverter<IntWritable, Integer> converter = mFactory.create(IntWritable.class);
        Assert.assertEquals(2, converter.convert(new IntWritable(2)).intValue());
    }

    @Test
    public void testConvertLongWritable() {
        AvroDatumConverter<LongWritable, Long> converter = mFactory.create(LongWritable.class);
        Assert.assertEquals(123L, converter.convert(new LongWritable(123L)).longValue());
    }

    @Test
    public void testConvertNullWritable() {
        AvroDatumConverter<NullWritable, Object> converter = mFactory.create(NullWritable.class);
        Assert.assertNull(converter.convert(NullWritable.get()));
    }

    @Test
    public void testConvertText() {
        AvroDatumConverter<Text, CharSequence> converter = mFactory.create(Text.class);
        Assert.assertEquals("foo", converter.convert(new Text("foo")).toString());
    }
}

