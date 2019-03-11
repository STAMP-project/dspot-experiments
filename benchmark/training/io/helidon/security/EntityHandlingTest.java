/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security;


import io.helidon.common.OptionalHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.common.reactive.SubmissionPublisher;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Test of handling message entities.
 */
public class EntityHandlingTest {
    private static final String REQUEST_BYTES = "request bytes";

    private static Security security;

    private static int counter = 1;

    private SecurityContext context;

    private String reactiveTestResult;

    private Throwable reactiveTestException;

    @Test
    public void reactiveTest() throws Throwable {
        CountDownLatch cdl = new CountDownLatch(1);
        context.env(context.env().derive().method("POST").path("/post"));
        SubmissionPublisher<ByteBuffer> publisher = new SubmissionPublisher(Runnable::run, 10);
        SecurityRequest request = context.securityRequestBuilder().requestMessage(message(publisher)).buildRequest();
        AuthenticationClientImpl authClient = ((AuthenticationClientImpl) (context.atnClientBuilder().build()));
        Optional<Entity> requestMessage = request.requestEntity();
        OptionalHelper.from(requestMessage).ifPresentOrElse(( message) -> message.filter(( byteBufferPublisher) -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SubmissionPublisher<ByteBuffer> bPublisher = new SubmissionPublisher<>();
            byteBufferPublisher.subscribe(new Flow.Subscriber<ByteBuffer>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscription.request(2);
                }

                @Override
                public void onNext(ByteBuffer item) {
                    try {
                        baos.write(item.array());
                        bPublisher.submit(item);
                    } catch ( e) {
                        throw new <e>RuntimeException();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    reactiveTestException = throwable;
                }

                @Override
                public void onComplete() {
                    reactiveTestResult = new String(baos.toByteArray());
                    cdl.countDown();
                }
            });
            return bPublisher;
        }), () -> fail("Request message should have been present"));
        request.responseEntity().ifPresent(( message) -> fail("Response message should not be present"));
        publisher.submit(ByteBuffer.wrap(EntityHandlingTest.REQUEST_BYTES.getBytes()));
        publisher.close();
        cdl.await();
        if (null != (reactiveTestException)) {
            throw reactiveTestException;
        }
        MatcherAssert.assertThat(reactiveTestResult, CoreMatchers.is(EntityHandlingTest.REQUEST_BYTES));
    }
}

