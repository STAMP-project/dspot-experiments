package com.zendesk.maxwell.producer;


import KafkaProducerDiagnostic.DiagnosticCallback;
import org.junit.Assert;
import org.junit.Test;


public class DiagnosticCallbackTest {
    @Test
    public void testCompleteNormally() throws Exception {
        // Given
        KafkaProducerDiagnostic.DiagnosticCallback callback = new KafkaProducerDiagnostic.DiagnosticCallback();
        // When
        callback.onCompletion(null, null);
        // Then
        callback.latency.get();
    }

    @Test
    public void testCompleteExceptionally() {
        // Given
        KafkaProducerDiagnostic.DiagnosticCallback callback = new KafkaProducerDiagnostic.DiagnosticCallback();
        // When
        callback.onCompletion(null, new RuntimeException("blah"));
        // Then
        Assert.assertTrue(callback.latency.isCompletedExceptionally());
    }
}

