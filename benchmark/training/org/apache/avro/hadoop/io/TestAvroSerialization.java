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


import Schema.Type.STRING;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapred.AvroWrapper;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serializer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroSerialization {
    @Test
    public void testAccept() {
        AvroSerialization<CharSequence> serialization = new AvroSerialization();
        Assert.assertTrue(serialization.accept(AvroKey.class));
        Assert.assertTrue(serialization.accept(AvroValue.class));
        Assert.assertFalse(serialization.accept(AvroWrapper.class));
        Assert.assertFalse(serialization.accept(String.class));
    }

    @Test
    public void testGetSerializerForKey() throws IOException {
        // Set the writer schema in the job configuration.
        Schema writerSchema = Schema.create(STRING);
        Job job = new Job();
        AvroJob.setMapOutputKeySchema(job, writerSchema);
        // Get a serializer from the configuration.
        AvroSerialization serialization = ReflectionUtils.newInstance(AvroSerialization.class, job.getConfiguration());
        @SuppressWarnings("unchecked")
        Serializer<AvroWrapper> serializer = serialization.getSerializer(AvroKey.class);
        Assert.assertTrue((serializer instanceof AvroSerializer));
        AvroSerializer avroSerializer = ((AvroSerializer) (serializer));
        // Check that the writer schema is set correctly on the serializer.
        Assert.assertEquals(writerSchema, avroSerializer.getWriterSchema());
    }

    @Test
    public void testGetSerializerForValue() throws IOException {
        // Set the writer schema in the job configuration.
        Schema writerSchema = Schema.create(STRING);
        Job job = new Job();
        AvroJob.setMapOutputValueSchema(job, writerSchema);
        // Get a serializer from the configuration.
        AvroSerialization serialization = ReflectionUtils.newInstance(AvroSerialization.class, job.getConfiguration());
        @SuppressWarnings("unchecked")
        Serializer<AvroWrapper> serializer = serialization.getSerializer(AvroValue.class);
        Assert.assertTrue((serializer instanceof AvroSerializer));
        AvroSerializer avroSerializer = ((AvroSerializer) (serializer));
        // Check that the writer schema is set correctly on the serializer.
        Assert.assertEquals(writerSchema, avroSerializer.getWriterSchema());
    }

    @Test
    public void testGetDeserializerForKey() throws IOException {
        // Set the reader schema in the job configuration.
        Schema readerSchema = Schema.create(STRING);
        Job job = new Job();
        AvroJob.setMapOutputKeySchema(job, readerSchema);
        // Get a deserializer from the configuration.
        AvroSerialization serialization = ReflectionUtils.newInstance(AvroSerialization.class, job.getConfiguration());
        @SuppressWarnings("unchecked")
        Deserializer<AvroWrapper> deserializer = serialization.getDeserializer(AvroKey.class);
        Assert.assertTrue((deserializer instanceof AvroKeyDeserializer));
        AvroKeyDeserializer avroDeserializer = ((AvroKeyDeserializer) (deserializer));
        // Check that the reader schema is set correctly on the deserializer.
        Assert.assertEquals(readerSchema, avroDeserializer.getReaderSchema());
    }

    @Test
    public void testGetDeserializerForValue() throws IOException {
        // Set the reader schema in the job configuration.
        Schema readerSchema = Schema.create(STRING);
        Job job = new Job();
        AvroJob.setMapOutputValueSchema(job, readerSchema);
        // Get a deserializer from the configuration.
        AvroSerialization serialization = ReflectionUtils.newInstance(AvroSerialization.class, job.getConfiguration());
        @SuppressWarnings("unchecked")
        Deserializer<AvroWrapper> deserializer = serialization.getDeserializer(AvroValue.class);
        Assert.assertTrue((deserializer instanceof AvroValueDeserializer));
        AvroValueDeserializer avroDeserializer = ((AvroValueDeserializer) (deserializer));
        // Check that the reader schema is set correctly on the deserializer.
        Assert.assertEquals(readerSchema, avroDeserializer.getReaderSchema());
    }

    @Test
    public void testClassPath() throws Exception {
        Configuration conf = new Configuration();
        ClassLoader loader = conf.getClass().getClassLoader();
        AvroSerialization serialization = new AvroSerialization();
        serialization.setConf(conf);
        AvroDeserializer des = ((AvroDeserializer) (serialization.getDeserializer(AvroKey.class)));
        ReflectData data = ((ReflectData) (getData()));
        Assert.assertEquals(loader, data.getClassLoader());
    }

    @Test
    public void testRoundTrip() throws Exception {
        Schema schema = Schema.create(STRING);
        Assert.assertTrue(((roundTrip(schema, "record", null)) instanceof String));
        Assert.assertTrue(((roundTrip(schema, "record", GenericData.class)) instanceof Utf8));
    }
}

