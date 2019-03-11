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
package org.apache.beam.runners.direct;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.beam.runners.local.StructuralKey;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.util.UserCodeException;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link CloningBundleFactory}.
 */
@RunWith(JUnit4.class)
public class CloningBundleFactoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public final TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    private CloningBundleFactory factory = CloningBundleFactory.create();

    @Test
    public void rootBundleSucceedsIgnoresCoder() {
        WindowedValue<CloningBundleFactoryTest.Record> one = WindowedValue.valueInGlobalWindow(new CloningBundleFactoryTest.Record());
        WindowedValue<CloningBundleFactoryTest.Record> two = WindowedValue.valueInGlobalWindow(new CloningBundleFactoryTest.Record());
        CommittedBundle<CloningBundleFactoryTest.Record> root = factory.<CloningBundleFactoryTest.Record>createRootBundle().add(one).add(two).commit(Instant.now());
        Assert.assertThat(root.getElements(), Matchers.containsInAnyOrder(one, two));
    }

    @Test
    public void bundleWorkingCoderSucceedsClonesOutput() {
        PCollection<Integer> created = p.apply(Create.of(1, 3).withCoder(VarIntCoder.of()));
        PCollection<KV<String, Integer>> kvs = created.apply(WithKeys.of("foo")).setCoder(KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()));
        WindowedValue<KV<String, Integer>> fooOne = WindowedValue.valueInGlobalWindow(KV.of("foo", 1));
        WindowedValue<KV<String, Integer>> fooThree = WindowedValue.valueInGlobalWindow(KV.of("foo", 3));
        CommittedBundle<KV<String, Integer>> bundle = factory.createBundle(kvs).add(fooOne).add(fooThree).commit(Instant.now());
        Assert.assertThat(bundle.getElements(), Matchers.containsInAnyOrder(fooOne, fooThree));
        Assert.assertThat(bundle.getElements(), Matchers.not(Matchers.containsInAnyOrder(Matchers.theInstance(fooOne), Matchers.theInstance(fooThree))));
        for (WindowedValue<KV<String, Integer>> foo : bundle.getElements()) {
            Assert.assertThat(foo.getValue(), Matchers.not(Matchers.anyOf(Matchers.theInstance(fooOne.getValue()), Matchers.theInstance(fooThree.getValue()))));
        }
        Assert.assertThat(bundle.getPCollection(), Matchers.equalTo(kvs));
    }

    @Test
    public void keyedBundleWorkingCoderSucceedsClonesOutput() {
        PCollection<Integer> created = p.apply(Create.of(1, 3).withCoder(VarIntCoder.of()));
        PCollection<KV<String, Iterable<Integer>>> keyed = created.apply(WithKeys.of("foo")).setCoder(KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of())).apply(GroupByKey.create());
        WindowedValue<KV<String, Iterable<Integer>>> foos = WindowedValue.valueInGlobalWindow(KV.<String, Iterable<Integer>>of("foo", ImmutableList.of(1, 3)));
        CommittedBundle<KV<String, Iterable<Integer>>> keyedBundle = factory.createKeyedBundle(StructuralKey.of("foo", StringUtf8Coder.of()), keyed).add(foos).commit(Instant.now());
        Assert.assertThat(keyedBundle.getElements(), Matchers.containsInAnyOrder(foos));
        Assert.assertThat(Iterables.getOnlyElement(keyedBundle.getElements()).getValue(), Matchers.not(Matchers.theInstance(foos.getValue())));
        Assert.assertThat(keyedBundle.getPCollection(), Matchers.equalTo(keyed));
        Assert.assertThat(keyedBundle.getKey(), Matchers.equalTo(StructuralKey.of("foo", StringUtf8Coder.of())));
    }

    @Test
    public void bundleEncodeFailsAddFails() {
        PCollection<CloningBundleFactoryTest.Record> pc = p.apply(Create.empty(new CloningBundleFactoryTest.RecordNoEncodeCoder()));
        UncommittedBundle<CloningBundleFactoryTest.Record> bundle = factory.createBundle(pc);
        thrown.expect(UserCodeException.class);
        thrown.expectCause(Matchers.isA(CoderException.class));
        thrown.expectMessage("Encode not allowed");
        bundle.add(WindowedValue.valueInGlobalWindow(new CloningBundleFactoryTest.Record()));
    }

    @Test
    public void bundleDecodeFailsAddFails() {
        PCollection<CloningBundleFactoryTest.Record> pc = p.apply(Create.empty(new CloningBundleFactoryTest.RecordNoDecodeCoder()));
        UncommittedBundle<CloningBundleFactoryTest.Record> bundle = factory.createBundle(pc);
        thrown.expect(UserCodeException.class);
        thrown.expectCause(Matchers.isA(CoderException.class));
        thrown.expectMessage("Decode not allowed");
        bundle.add(WindowedValue.valueInGlobalWindow(new CloningBundleFactoryTest.Record()));
    }

    @Test
    public void keyedBundleEncodeFailsAddFails() {
        PCollection<CloningBundleFactoryTest.Record> pc = p.apply(Create.empty(new CloningBundleFactoryTest.RecordNoEncodeCoder()));
        UncommittedBundle<CloningBundleFactoryTest.Record> bundle = factory.createKeyedBundle(StructuralKey.of("foo", StringUtf8Coder.of()), pc);
        thrown.expect(UserCodeException.class);
        thrown.expectCause(Matchers.isA(CoderException.class));
        thrown.expectMessage("Encode not allowed");
        bundle.add(WindowedValue.valueInGlobalWindow(new CloningBundleFactoryTest.Record()));
    }

    @Test
    public void keyedBundleDecodeFailsAddFails() {
        PCollection<CloningBundleFactoryTest.Record> pc = p.apply(Create.empty(new CloningBundleFactoryTest.RecordNoDecodeCoder()));
        UncommittedBundle<CloningBundleFactoryTest.Record> bundle = factory.createKeyedBundle(StructuralKey.of("foo", StringUtf8Coder.of()), pc);
        thrown.expect(UserCodeException.class);
        thrown.expectCause(Matchers.isA(CoderException.class));
        thrown.expectMessage("Decode not allowed");
        bundle.add(WindowedValue.valueInGlobalWindow(new CloningBundleFactoryTest.Record()));
    }

    static class Record {}

    static class RecordNoEncodeCoder extends AtomicCoder<CloningBundleFactoryTest.Record> {
        @Override
        public void encode(CloningBundleFactoryTest.Record value, OutputStream outStream) throws IOException {
            throw new CoderException("Encode not allowed");
        }

        @Override
        public CloningBundleFactoryTest.Record decode(InputStream inStream) throws IOException {
            return null;
        }
    }

    static class RecordNoDecodeCoder extends AtomicCoder<CloningBundleFactoryTest.Record> {
        @Override
        public void encode(CloningBundleFactoryTest.Record value, OutputStream outStream) throws IOException {
        }

        @Override
        public CloningBundleFactoryTest.Record decode(InputStream inStream) throws IOException {
            throw new CoderException("Decode not allowed");
        }
    }

    private static class RecordStructuralValueCoder extends AtomicCoder<CloningBundleFactoryTest.Record> {
        @Override
        public void encode(CloningBundleFactoryTest.Record value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public CloningBundleFactoryTest.Record decode(InputStream inStream) throws IOException, CoderException {
            return new CloningBundleFactoryTest.Record() {
                @Override
                public String toString() {
                    return "DecodedRecord";
                }
            };
        }

        @Override
        public boolean consistentWithEquals() {
            return true;
        }

        @Override
        public Object structuralValue(CloningBundleFactoryTest.Record value) {
            return value;
        }
    }

    private static class RecordNotConsistentWithEqualsStructuralValueCoder extends AtomicCoder<CloningBundleFactoryTest.Record> {
        @Override
        public void encode(CloningBundleFactoryTest.Record value, OutputStream outStream) throws IOException, CoderException {
        }

        @Override
        public CloningBundleFactoryTest.Record decode(InputStream inStream) throws IOException, CoderException {
            return new CloningBundleFactoryTest.Record() {
                @Override
                public String toString() {
                    return "DecodedRecord";
                }
            };
        }

        @Override
        public boolean consistentWithEquals() {
            return false;
        }

        @Override
        public Object structuralValue(CloningBundleFactoryTest.Record value) {
            return value;
        }
    }

    private static class IdentityDoFn extends DoFn<CloningBundleFactoryTest.Record, CloningBundleFactoryTest.Record> {
        @ProcessElement
        public void proc(ProcessContext ctxt) {
            ctxt.output(ctxt.element());
        }
    }

    private static class SimpleIdentity extends SimpleFunction<CloningBundleFactoryTest.Record, CloningBundleFactoryTest.Record> {
        @Override
        public CloningBundleFactoryTest.Record apply(CloningBundleFactoryTest.Record input) {
            return input;
        }
    }
}

