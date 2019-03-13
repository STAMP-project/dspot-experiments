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
package org.apache.beam.runners.spark.coders;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import org.apache.beam.runners.spark.io.MicrobatchSource;
import org.apache.spark.SparkConf;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing of beam registrar.
 */
public class BeamSparkRunnerRegistratorTest {
    @Test
    public void testKryoRegistration() {
        SparkConf conf = new SparkConf();
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        conf.set("spark.kryo.registrator", BeamSparkRunnerRegistratorTest.WrapperKryoRegistrator.class.getName());
        runSimplePipelineWithSparkContext(conf);
        Assert.assertTrue("WrapperKryoRegistrator wasn't initiated, probably KryoSerializer is not set", BeamSparkRunnerRegistratorTest.WrapperKryoRegistrator.wasInitiated);
    }

    @Test
    public void testDefaultSerializerNotCallingKryo() {
        SparkConf conf = new SparkConf();
        conf.set("spark.kryo.registrator", BeamSparkRunnerRegistratorTest.KryoRegistratorIsNotCalled.class.getName());
        runSimplePipelineWithSparkContext(conf);
    }

    /**
     * A {@link BeamSparkRunnerRegistrator} that fails if called. Use only for test purposes. Needs to
     * be public for serialization.
     */
    public static class KryoRegistratorIsNotCalled extends BeamSparkRunnerRegistrator {
        @Override
        public void registerClasses(Kryo kryo) {
            Assert.fail(("Default spark.serializer is JavaSerializer" + " so spark.kryo.registrator shouldn't be called"));
        }
    }

    /**
     * A {@link BeamSparkRunnerRegistrator} that registers an internal class to validate
     * KryoSerialization resolution. Use only for test purposes. Needs to be public for serialization.
     */
    public static class WrapperKryoRegistrator extends BeamSparkRunnerRegistrator {
        static boolean wasInitiated = false;

        public WrapperKryoRegistrator() {
            BeamSparkRunnerRegistratorTest.WrapperKryoRegistrator.wasInitiated = true;
        }

        @Override
        public void registerClasses(Kryo kryo) {
            super.registerClasses(kryo);
            Registration registration = kryo.getRegistration(MicrobatchSource.class);
            Serializer kryoSerializer = registration.getSerializer();
            Assert.assertTrue((kryoSerializer instanceof StatelessJavaSerializer));
        }
    }
}

