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
package org.apache.beam.runners.dataflow.worker;


import org.apache.beam.runners.dataflow.util.PropertyNames;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFnSchemaInformation;
import org.apache.beam.sdk.util.DoFnInfo;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DoFnInstanceManagers}.
 */
@RunWith(JUnit4.class)
public class DoFnInstanceManagersTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static class TestFn extends DoFn<Object, Object> {
        boolean tornDown = false;

        @ProcessElement
        public void processElement(ProcessContext processContext) throws Exception {
        }

        @Teardown
        public void teardown() throws Exception {
            if (tornDown) {
                Assert.fail("Should not be torn down twice");
            }
            tornDown = true;
        }
    }

    private DoFn<?, ?> initialFn = new DoFnInstanceManagersTest.TestFn();

    @Test
    public void testInstanceReturnsInstance() throws Exception {
        DoFnInfo<?, ?> info = /* side input views */
        /* input coder */
        /* main output id */
        DoFnInfo.forFn(initialFn, WindowingStrategy.globalDefault(), null, null, new org.apache.beam.sdk.values.TupleTag(PropertyNames.OUTPUT), DoFnSchemaInformation.create());
        DoFnInstanceManager mgr = DoFnInstanceManagers.singleInstance(info);
        Assert.assertThat(mgr.peek(), Matchers.<DoFnInfo<?, ?>>theInstance(info));
        Assert.assertThat(mgr.get(), Matchers.<DoFnInfo<?, ?>>theInstance(info));
        // This call should be identical to the above, and return the same object
        Assert.assertThat(mgr.get(), Matchers.<DoFnInfo<?, ?>>theInstance(info));
        // Peek should always return the same object
        Assert.assertThat(mgr.peek(), Matchers.<DoFnInfo<?, ?>>theInstance(info));
    }

    @Test
    public void testInstanceIgnoresAbort() throws Exception {
        DoFnInfo<?, ?> info = /* side input views */
        /* input coder */
        /* main output id */
        DoFnInfo.forFn(initialFn, WindowingStrategy.globalDefault(), null, null, new org.apache.beam.sdk.values.TupleTag(PropertyNames.OUTPUT), DoFnSchemaInformation.create());
        DoFnInstanceManager mgr = DoFnInstanceManagers.singleInstance(info);
        mgr.abort(mgr.get());
        // TestFn#teardown would fail the test after multiple calls
        mgr.abort(mgr.get());
        // The returned info is still the initial info
        Assert.assertThat(mgr.get(), Matchers.<DoFnInfo<?, ?>>theInstance(info));
        Assert.assertThat(mgr.get().getDoFn(), Matchers.theInstance(initialFn));
    }

    @Test
    public void testCloningPoolReusesAfterComplete() throws Exception {
        DoFnInfo<?, ?> info = /* side input views */
        /* input coder */
        /* main output id */
        DoFnInfo.forFn(initialFn, WindowingStrategy.globalDefault(), null, null, new org.apache.beam.sdk.values.TupleTag(PropertyNames.OUTPUT), DoFnSchemaInformation.create());
        DoFnInstanceManager mgr = DoFnInstanceManagers.cloningPool(info);
        DoFnInfo<?, ?> retrievedInfo = mgr.get();
        Assert.assertThat(retrievedInfo, Matchers.not(Matchers.<DoFnInfo<?, ?>>theInstance(info)));
        Assert.assertThat(retrievedInfo.getDoFn(), Matchers.not(Matchers.theInstance(info.getDoFn())));
        mgr.complete(retrievedInfo);
        DoFnInfo<?, ?> afterCompleteInfo = mgr.get();
        Assert.assertThat(afterCompleteInfo, Matchers.<DoFnInfo<?, ?>>theInstance(retrievedInfo));
        Assert.assertThat(afterCompleteInfo.getDoFn(), Matchers.theInstance(retrievedInfo.getDoFn()));
    }

    @Test
    public void testCloningPoolTearsDownAfterAbort() throws Exception {
        DoFnInfo<?, ?> info = /* side input views */
        /* input coder */
        /* main output id */
        DoFnInfo.forFn(initialFn, WindowingStrategy.globalDefault(), null, null, new org.apache.beam.sdk.values.TupleTag(PropertyNames.OUTPUT), DoFnSchemaInformation.create());
        DoFnInstanceManager mgr = DoFnInstanceManagers.cloningPool(info);
        DoFnInfo<?, ?> retrievedInfo = mgr.get();
        mgr.abort(retrievedInfo);
        DoFnInstanceManagersTest.TestFn fn = ((DoFnInstanceManagersTest.TestFn) (retrievedInfo.getDoFn()));
        Assert.assertThat(fn.tornDown, Matchers.is(true));
        DoFnInfo<?, ?> afterAbortInfo = mgr.get();
        Assert.assertThat(afterAbortInfo, Matchers.not(Matchers.<DoFnInfo<?, ?>>theInstance(retrievedInfo)));
        Assert.assertThat(afterAbortInfo.getDoFn(), Matchers.not(Matchers.theInstance(retrievedInfo.getDoFn())));
        Assert.assertThat(((DoFnInstanceManagersTest.TestFn) (afterAbortInfo.getDoFn())).tornDown, Matchers.is(false));
    }

    @Test
    public void testCloningPoolMultipleOutstanding() throws Exception {
        DoFnInfo<?, ?> info = /* side input views */
        /* input coder */
        /* main output id */
        DoFnInfo.forFn(initialFn, WindowingStrategy.globalDefault(), null, null, new org.apache.beam.sdk.values.TupleTag(PropertyNames.OUTPUT), DoFnSchemaInformation.create());
        DoFnInstanceManager mgr = DoFnInstanceManagers.cloningPool(info);
        DoFnInfo<?, ?> firstInfo = mgr.get();
        DoFnInfo<?, ?> secondInfo = mgr.get();
        Assert.assertThat(firstInfo, Matchers.not(Matchers.<DoFnInfo<?, ?>>theInstance(secondInfo)));
        Assert.assertThat(firstInfo.getDoFn(), Matchers.not(Matchers.theInstance(secondInfo.getDoFn())));
    }
}

