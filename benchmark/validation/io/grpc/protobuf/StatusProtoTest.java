/**
 * Copyright 2017 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.protobuf;


import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import com.google.protobuf.Any;
import com.google.rpc.Status;
import io.grpc.Metadata;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.com.google.rpc.Status;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link StatusProto}.
 */
@RunWith(JUnit4.class)
public class StatusProtoTest {
    private Metadata metadata;

    @Test
    public void toStatusRuntimeException() throws Exception {
        StatusRuntimeException sre = StatusProto.toStatusRuntimeException(StatusProtoTest.STATUS_PROTO);
        Status extractedStatusProto = StatusProto.fromThrowable(sre);
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getCode(), sre.getStatus().getCode().value());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getMessage(), sre.getStatus().getDescription());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO, extractedStatusProto);
    }

    @Test
    public void toStatusRuntimeExceptionWithMetadata_shouldIncludeMetadata() throws Exception {
        StatusRuntimeException sre = StatusProto.toStatusRuntimeException(StatusProtoTest.STATUS_PROTO, metadata);
        Status extractedStatusProto = StatusProto.fromThrowable(sre);
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getCode(), sre.getStatus().getCode().value());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getMessage(), sre.getStatus().getDescription());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO, extractedStatusProto);
        Assert.assertNotNull(sre.getTrailers());
        Assert.assertEquals(StatusProtoTest.METADATA_VALUE, sre.getTrailers().get(io.grpc.protobuf.METADATA_KEY));
    }

    @Test
    public void toStatusRuntimeExceptionWithMetadata_shouldThrowIfMetadataIsNull() throws Exception {
        try {
            StatusProto.toStatusRuntimeException(StatusProtoTest.STATUS_PROTO, null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException npe) {
            Assert.assertEquals("metadata must not be null", npe.getMessage());
        }
    }

    @Test
    public void toStatusRuntimeException_shouldThrowIfStatusCodeInvalid() throws Exception {
        try {
            StatusProto.toStatusRuntimeException(StatusProtoTest.INVALID_STATUS_PROTO);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expectedException) {
            Assert.assertEquals("invalid status code", expectedException.getMessage());
        }
    }

    @Test
    public void toStatusException() throws Exception {
        StatusException se = StatusProto.toStatusException(StatusProtoTest.STATUS_PROTO);
        Status extractedStatusProto = StatusProto.fromThrowable(se);
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getCode(), se.getStatus().getCode().value());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getMessage(), se.getStatus().getDescription());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO, extractedStatusProto);
    }

    @Test
    public void toStatusExceptionWithMetadata_shouldIncludeMetadata() throws Exception {
        StatusException se = StatusProto.toStatusException(StatusProtoTest.STATUS_PROTO, metadata);
        Status extractedStatusProto = StatusProto.fromThrowable(se);
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getCode(), se.getStatus().getCode().value());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO.getMessage(), se.getStatus().getDescription());
        Assert.assertEquals(StatusProtoTest.STATUS_PROTO, extractedStatusProto);
        Assert.assertNotNull(se.getTrailers());
        Assert.assertEquals(StatusProtoTest.METADATA_VALUE, se.getTrailers().get(io.grpc.protobuf.METADATA_KEY));
    }

    @Test
    public void toStatusExceptionWithMetadata_shouldThrowIfMetadataIsNull() throws Exception {
        try {
            StatusProto.toStatusException(StatusProtoTest.STATUS_PROTO, null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException npe) {
            Assert.assertEquals("metadata must not be null", npe.getMessage());
        }
    }

    @Test
    public void toStatusException_shouldThrowIfStatusCodeInvalid() throws Exception {
        try {
            StatusProto.toStatusException(StatusProtoTest.INVALID_STATUS_PROTO);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expectedException) {
            Assert.assertEquals("invalid status code", expectedException.getMessage());
        }
    }

    @Test
    public void fromThrowable_shouldReturnNullIfTrailersAreNull() {
        io.grpc.Status status = io.grpc.Status.fromCodeValue(0);
        Assert.assertNull(StatusProto.fromThrowable(status.asRuntimeException()));
        Assert.assertNull(StatusProto.fromThrowable(status.asException()));
    }

    @Test
    public void fromThrowable_shouldReturnNullIfStatusDetailsKeyIsMissing() {
        io.grpc.Status status = io.grpc.Status.fromCodeValue(0);
        Metadata emptyMetadata = new Metadata();
        Assert.assertNull(StatusProto.fromThrowable(status.asRuntimeException(emptyMetadata)));
        Assert.assertNull(StatusProto.fromThrowable(status.asException(emptyMetadata)));
    }

    @Test
    public void fromThrowableWithNestedStatusRuntimeException() {
        StatusRuntimeException sre = StatusProto.toStatusRuntimeException(StatusProtoTest.STATUS_PROTO);
        Throwable nestedSre = new Throwable(sre);
        Status extractedStatusProto = StatusProto.fromThrowable(sre);
        Status extractedStatusProtoFromNestedSre = StatusProto.fromThrowable(nestedSre);
        Assert.assertEquals(extractedStatusProto, extractedStatusProtoFromNestedSre);
    }

    @Test
    public void fromThrowableWithNestedStatusException() {
        StatusException se = StatusProto.toStatusException(StatusProtoTest.STATUS_PROTO);
        Throwable nestedSe = new Throwable(se);
        Status extractedStatusProto = StatusProto.fromThrowable(se);
        Status extractedStatusProtoFromNestedSe = StatusProto.fromThrowable(nestedSe);
        Assert.assertEquals(extractedStatusProto, extractedStatusProtoFromNestedSe);
    }

    @Test
    public void fromThrowable_shouldReturnNullIfNoEmbeddedStatus() {
        Throwable nestedSe = new Throwable(new Throwable("no status found"));
        Assert.assertNull(StatusProto.fromThrowable(nestedSe));
    }

    private static final Metadata.Key<String> METADATA_KEY = Key.of("test-metadata", ASCII_STRING_MARSHALLER);

    private static final String METADATA_VALUE = "test metadata value";

    private static final io.grpc.Status STATUS_PROTO = com.google.rpc.Status.newBuilder().setCode(2).setMessage("status message").addDetails(Any.pack(com.google.rpc.Status.newBuilder().setCode(13).setMessage("nested message").build())).build();

    private static final io.grpc.Status INVALID_STATUS_PROTO = com.google.rpc.Status.newBuilder().setCode((-1)).build();
}

