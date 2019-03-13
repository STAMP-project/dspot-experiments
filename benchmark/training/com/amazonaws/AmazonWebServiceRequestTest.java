/**
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.SyncProgressListener;
import com.amazonaws.handlers.HandlerContextKey;
import com.amazonaws.metrics.RequestMetricCollector;
import org.junit.Assert;
import org.junit.Test;
import utils.model.EmptyAmazonWebServiceRequest;


public class AmazonWebServiceRequestTest {
    @Test
    public void cloneSourceAndRootBehaviorShouldBeCorrect() {
        AmazonWebServiceRequest root = new AmazonWebServiceRequest() {};
        Assert.assertNull(root.getCloneSource());
        Assert.assertNull(root.getCloneRoot());
        AmazonWebServiceRequest clone = root.clone();
        Assert.assertEquals(root, clone.getCloneSource());
        Assert.assertEquals(root, clone.getCloneRoot());
        AmazonWebServiceRequest clone2 = clone.clone();
        Assert.assertEquals(clone, clone2.getCloneSource());
        Assert.assertEquals(root, clone2.getCloneRoot());
    }

    @Test
    public void contextBehaviorShouldBeCorrect() {
        final String givenContextValue = "Hello";
        HandlerContextKey<String> context = new HandlerContextKey<String>("");
        AmazonWebServiceRequest request = new AmazonWebServiceRequest() {};
        // Requests should store context
        request.addHandlerContext(context, "Hello");
        Assert.assertEquals(request.getHandlerContext().get(context), givenContextValue);
        Assert.assertEquals(request.getHandlerContext(context), givenContextValue);
        // Clones should inherit context
        AmazonWebServiceRequest clone = request.clone();
        Assert.assertEquals(clone.getHandlerContext().get(context), givenContextValue);
        Assert.assertEquals(clone.getHandlerContext(context), givenContextValue);
        // Modifications of request and clone context should not affect each other
        clone.addHandlerContext(context, "Hello2");
        request.addHandlerContext(context, "Hello3");
        Assert.assertEquals(clone.getHandlerContext(context), "Hello2");
        Assert.assertEquals(request.getHandlerContext(context), "Hello3");
    }

    @Test
    public void copyBaseTo() {
        final ProgressListener listener = new SyncProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
            }
        };
        final AWSCredentials credentials = new BasicAWSCredentials("accesskey", "accessid");
        final RequestMetricCollector collector = new RequestMetricCollector() {
            @Override
            public void collectMetrics(Request<?> request, Response<?> response) {
            }
        };
        final AmazonWebServiceRequest from = new AmazonWebServiceRequest() {};
        from.setGeneralProgressListener(listener);
        from.setRequestCredentials(credentials);
        from.setRequestMetricCollector(collector);
        from.putCustomRequestHeader("k1", "v1");
        from.putCustomRequestHeader("k2", "v2");
        from.putCustomQueryParameter("k1", "v1");
        from.putCustomQueryParameter("k2", "v2a");
        from.putCustomQueryParameter("k2", "v2b");
        from.getRequestClientOptions().setReadLimit(1234);
        final AmazonWebServiceRequest to = new AmazonWebServiceRequest() {};
        // Before copy
        RequestClientOptions toOptions;
        AmazonWebServiceRequestTest.verifyBaseBeforeCopy(to);
        // After copy
        from.copyBaseTo(to);
        AmazonWebServiceRequestTest.verifyBaseAfterCopy(listener, credentials, collector, from, to);
    }

    @Test
    public void nullCredentialsSet_ReturnsNullProvider() {
        AmazonWebServiceRequest request = new EmptyAmazonWebServiceRequest();
        request.setRequestCredentials(null);
        Assert.assertNull(request.getRequestCredentialsProvider());
    }
}

