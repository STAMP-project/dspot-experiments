/**
 * The MIT License
 * Copyright (c) 2014 Ilkka Sepp?l?
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.serverless.faas.api;


import com.amazonaws.services.lambda.runtime.Context;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for LambdaInfoApiHandler
 */
@RunWith(MockitoJUnitRunner.class)
public class LambdaInfoApiHandlerTest {
    @Test
    public void handleRequestWithMockContext() {
        LambdaInfoApiHandler lambdaInfoApiHandler = new LambdaInfoApiHandler();
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getAwsRequestId()).thenReturn("mock aws request id");
        MatcherAssert.assertThat(lambdaInfoApiHandler.handleRequest(null, context), IsNull.notNullValue());
    }
}

