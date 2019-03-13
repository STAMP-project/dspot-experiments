/**
 * Copyright 2016-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.protocol.json;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSystem;


public class SdkStructuredIonFactoryTest {
    private static final String ERROR_PREFIX = "aws-type:";

    private static final String ERROR_TYPE = "InvalidParameterException";

    private static final String ERROR_MESSAGE = "foo";

    private static final String NO_SERVICE_NAME = null;

    private static final HttpRequestBase NO_HTTP_REQUEST = null;

    private static final String NO_CUSTOM_ERROR_CODE_FIELD_NAME = null;

    private static IonSystem system;

    @Test
    public void handlesErrorsUsingHttpHeader() throws Exception {
        IonStruct payload = SdkStructuredIonFactoryTest.createPayload();
        HttpResponse error = SdkStructuredIonFactoryTest.createResponse(payload);
        error.addHeader("x-amzn-ErrorType", SdkStructuredIonFactoryTest.ERROR_TYPE);
        AmazonServiceException exception = handleError(error);
        Assert.assertThat(exception, Matchers.instanceOf(SdkStructuredIonFactoryTest.InvalidParameterException.class));
        Assert.assertEquals(SdkStructuredIonFactoryTest.ERROR_MESSAGE, exception.getErrorMessage());
    }

    @Test
    public void handlesErrorsUsingMagicField() throws Exception {
        IonStruct payload = SdkStructuredIonFactoryTest.createPayload();
        payload.add("__type", SdkStructuredIonFactoryTest.system.newString(SdkStructuredIonFactoryTest.ERROR_TYPE));
        HttpResponse error = SdkStructuredIonFactoryTest.createResponse(payload);
        AmazonServiceException exception = handleError(error);
        Assert.assertThat(exception, Matchers.instanceOf(SdkStructuredIonFactoryTest.InvalidParameterException.class));
        Assert.assertEquals(SdkStructuredIonFactoryTest.ERROR_MESSAGE, exception.getErrorMessage());
    }

    @Test
    public void handlesErrorsUsingAnnotation() throws Exception {
        IonStruct payload = SdkStructuredIonFactoryTest.createPayload();
        payload.addTypeAnnotation(((SdkStructuredIonFactoryTest.ERROR_PREFIX) + (SdkStructuredIonFactoryTest.ERROR_TYPE)));
        HttpResponse error = SdkStructuredIonFactoryTest.createResponse(payload);
        AmazonServiceException exception = handleError(error);
        Assert.assertThat(exception, Matchers.instanceOf(SdkStructuredIonFactoryTest.InvalidParameterException.class));
        Assert.assertEquals(SdkStructuredIonFactoryTest.ERROR_MESSAGE, exception.getErrorMessage());
    }

    @Test(expected = AmazonClientException.class)
    public void rejectPayloadsWithMultipleErrorAnnotations() throws Exception {
        IonStruct payload = SdkStructuredIonFactoryTest.createPayload();
        payload.addTypeAnnotation(((SdkStructuredIonFactoryTest.ERROR_PREFIX) + (SdkStructuredIonFactoryTest.ERROR_TYPE)));
        payload.addTypeAnnotation(((SdkStructuredIonFactoryTest.ERROR_PREFIX) + "foo"));
        HttpResponse error = SdkStructuredIonFactoryTest.createResponse(payload);
        handleError(error);
    }

    @Test
    public void handlesErrorsWithMutipleAnnotations() throws Exception {
        IonStruct payload = SdkStructuredIonFactoryTest.createPayload();
        payload.addTypeAnnotation("foo");
        payload.addTypeAnnotation(((SdkStructuredIonFactoryTest.ERROR_PREFIX) + (SdkStructuredIonFactoryTest.ERROR_TYPE)));
        payload.addTypeAnnotation("bar");
        HttpResponse error = SdkStructuredIonFactoryTest.createResponse(payload);
        AmazonServiceException exception = handleError(error);
        Assert.assertThat(exception, Matchers.instanceOf(SdkStructuredIonFactoryTest.InvalidParameterException.class));
        Assert.assertEquals(SdkStructuredIonFactoryTest.ERROR_MESSAGE, exception.getErrorMessage());
    }

    private static class InvalidParameterException extends AmazonServiceException {
        private static final long serialVersionUID = 0;

        public InvalidParameterException(String errorMessage) {
            super(errorMessage);
        }
    }
}

