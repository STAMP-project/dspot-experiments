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
package org.apache.beam.sdk.fn.data;


import org.apache.beam.model.fnexecution.v1.BeamFnApi.RemoteGrpcPort;
import org.apache.beam.model.pipeline.v1.Endpoints.ApiServiceDescriptor;
import org.apache.beam.model.pipeline.v1.Endpoints.OAuth2ClientCredentialsGrant;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.InvalidProtocolBufferException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RemoteGrpcPortWrite}.
 */
@RunWith(JUnit4.class)
public class RemoteGrpcPortWriteTest {
    @Test
    public void getPortSucceeds() {
        RemoteGrpcPort port = RemoteGrpcPort.newBuilder().setApiServiceDescriptor(ApiServiceDescriptor.newBuilder().setUrl("foo").setOauth2ClientCredentialsGrant(OAuth2ClientCredentialsGrant.getDefaultInstance()).build()).build();
        RemoteGrpcPortWrite write = RemoteGrpcPortWrite.writeToPort("myPort", port);
        Assert.assertThat(write.getPort(), Matchers.equalTo(port));
    }

    @Test
    public void toFromPTransform() throws InvalidProtocolBufferException {
        RemoteGrpcPort port = RemoteGrpcPort.newBuilder().setApiServiceDescriptor(ApiServiceDescriptor.newBuilder().setUrl("foo").setOauth2ClientCredentialsGrant(OAuth2ClientCredentialsGrant.getDefaultInstance()).build()).build();
        RemoteGrpcPortWrite write = RemoteGrpcPortWrite.writeToPort("myPort", port);
        PTransform ptransform = PTransform.parseFrom(write.toPTransform().toByteArray());
        RemoteGrpcPortWrite serDeWrite = RemoteGrpcPortWrite.fromPTransform(ptransform);
        Assert.assertThat(serDeWrite, Matchers.equalTo(write));
        Assert.assertThat(serDeWrite.getPort(), Matchers.equalTo(write.getPort()));
        Assert.assertThat(serDeWrite.toPTransform(), Matchers.equalTo(ptransform));
    }
}

