/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.volley;


import com.android.volley.mock.MockRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ResponseDeliveryTest {
    private ExecutorDelivery mDelivery;

    private MockRequest mRequest;

    private Response<byte[]> mSuccessResponse;

    @Test
    public void postResponseCallsDeliverResponse() {
        mDelivery.postResponse(mRequest, mSuccessResponse);
        Assert.assertTrue(mRequest.deliverResponse_called);
        Assert.assertFalse(mRequest.deliverError_called);
    }

    @Test
    public void postResponseSuppressesCanceled() {
        mRequest.cancel();
        mDelivery.postResponse(mRequest, mSuccessResponse);
        Assert.assertFalse(mRequest.deliverResponse_called);
        Assert.assertFalse(mRequest.deliverError_called);
    }

    @Test
    public void postErrorCallsDeliverError() {
        Response<byte[]> errorResponse = Response.error(new ServerError());
        mDelivery.postResponse(mRequest, errorResponse);
        Assert.assertTrue(mRequest.deliverError_called);
        Assert.assertFalse(mRequest.deliverResponse_called);
    }
}

