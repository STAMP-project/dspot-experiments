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
package org.apache.beam.fn.harness.control;


import BeamFnApi.InstructionRequest;
import BeamFnApi.InstructionResponse;
import BeamFnApi.ProcessBundleDescriptor;
import BeamFnApi.RegisterRequest;
import RunnerApi.Coder;
import RunnerApi.FunctionSpec;
import RunnerApi.SdkFunctionSpec;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.RegisterResponse;
import org.apache.beam.sdk.fn.test.TestExecutors;
import org.apache.beam.sdk.fn.test.TestExecutors.TestExecutorService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RegisterHandler}.
 */
@RunWith(JUnit4.class)
public class RegisterHandlerTest {
    @Rule
    public TestExecutorService executor = TestExecutors.from(Executors::newCachedThreadPool);

    private static final InstructionRequest REGISTER_REQUEST = InstructionRequest.newBuilder().setInstructionId("1L").setRegister(RegisterRequest.newBuilder().addProcessBundleDescriptor(ProcessBundleDescriptor.newBuilder().setId("1L").putCoders("10L", Coder.newBuilder().setSpec(SdkFunctionSpec.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("urn:10L").build()).build()).build()).build()).addProcessBundleDescriptor(ProcessBundleDescriptor.newBuilder().setId("2L").putCoders("20L", Coder.newBuilder().setSpec(SdkFunctionSpec.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("urn:20L").build()).build()).build()).build()).build()).build();

    private static final InstructionResponse REGISTER_RESPONSE = InstructionResponse.newBuilder().setRegister(RegisterResponse.getDefaultInstance()).build();

    @Test
    public void testRegistration() throws Exception {
        RegisterHandler handler = new RegisterHandler();
        Future<BeamFnApi.InstructionResponse> responseFuture = executor.submit(() -> {
            // Purposefully wait a small amount of time making it likely that
            // a downstream caller needs to block.
            Thread.sleep(100);
            return handler.register(REGISTER_REQUEST).build();
        });
        Assert.assertEquals(RegisterHandlerTest.REGISTER_REQUEST.getRegister().getProcessBundleDescriptor(0), handler.getById("1L"));
        Assert.assertEquals(RegisterHandlerTest.REGISTER_REQUEST.getRegister().getProcessBundleDescriptor(1), handler.getById("2L"));
        Assert.assertEquals(RegisterHandlerTest.REGISTER_REQUEST.getRegister().getProcessBundleDescriptor(0).getCodersOrThrow("10L"), handler.getById("10L"));
        Assert.assertEquals(RegisterHandlerTest.REGISTER_REQUEST.getRegister().getProcessBundleDescriptor(1).getCodersOrThrow("20L"), handler.getById("20L"));
        Assert.assertEquals(RegisterHandlerTest.REGISTER_RESPONSE, responseFuture.get());
    }
}

