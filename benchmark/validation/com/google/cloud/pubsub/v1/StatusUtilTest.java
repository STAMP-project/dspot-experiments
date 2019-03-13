package com.google.cloud.pubsub.v1;


import Code.UNAVAILABLE;
import com.google.api.gax.grpc.GrpcStatusCode;
import org.junit.Test;


public class StatusUtilTest {
    @Test
    public void testIsRetryable() {
        assertThat(StatusUtil.isRetryable(new com.google.api.gax.rpc.ApiException("derp", null, GrpcStatusCode.of(UNAVAILABLE), false))).isTrue();
        assertThat(StatusUtil.isRetryable(new com.google.api.gax.rpc.ApiException("Server shutdownNow invoked", null, GrpcStatusCode.of(UNAVAILABLE), false))).isFalse();
    }
}

