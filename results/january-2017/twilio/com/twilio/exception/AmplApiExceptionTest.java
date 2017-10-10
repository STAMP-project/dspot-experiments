

package com.twilio.exception;


@java.lang.SuppressWarnings(value = "ThrowableInstanceNeverThrown")
public class AmplApiExceptionTest {
    private final java.lang.String anyMessage = "message for test";

    private final java.lang.Throwable anyCause = new java.lang.RuntimeException("some root cause");

    private final java.lang.String anyMoreInfo = "more info";

    private final int anyErrorCode = 123;

    private final int anyHttpStatus = 200;

    @org.junit.Test
    public void singleArgConstructorShouldPreserveMessage() {
        com.twilio.exception.ApiException error = new com.twilio.exception.ApiException(anyMessage);
        org.junit.Assert.assertEquals(anyMessage, error.getMessage());
    }

    @org.junit.Test
    public void twoArgConstructorShouldPreserveMessageAndCause() {
        com.twilio.exception.ApiException error = new com.twilio.exception.ApiException(anyMessage, anyCause);
        org.junit.Assert.assertEquals("Message", anyMessage, error.getMessage());
        org.junit.Assert.assertSame("Cause", anyCause, error.getCause());
    }

    @org.junit.Test
    public void fullConstructorShouldPreserveAllValues() {
        com.twilio.exception.ApiException error = new com.twilio.exception.ApiException(anyMessage, anyErrorCode, anyMoreInfo, anyHttpStatus, anyCause);
        org.junit.Assert.assertEquals("Message", anyMessage, error.getMessage());
        org.junit.Assert.assertSame("Cause", anyCause, error.getCause());
        org.junit.Assert.assertEquals("More info", anyMoreInfo, error.getMoreInfo());
        org.junit.Assert.assertEquals("Error code", anyErrorCode, error.getCode().intValue());
        org.junit.Assert.assertEquals("Status code", anyHttpStatus, error.getStatusCode().intValue());
    }

    @org.junit.Test
    public void getCodeShouldNotThrowExceptionWhenCodeIsNull() {
        com.twilio.exception.ApiException error = new com.twilio.exception.ApiException(anyMessage);
        org.junit.Assert.assertEquals(null, error.getCode());
    }

    @org.junit.Test
    public void getStatusCodeShouldNotThrowExceptionWhenCodeIsNull() {
        com.twilio.exception.ApiException error = new com.twilio.exception.ApiException(anyMessage);
        org.junit.Assert.assertEquals(null, error.getStatusCode());
    }
}

