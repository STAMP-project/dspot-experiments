/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.alts.internal;


import HandshakerResp.Builder;
import io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link AltsHandshakerStub}.
 */
@RunWith(JUnit4.class)
public class AltsHandshakerStubTest {
    /**
     * Mock status of handshaker service.
     */
    private static enum Status {

        OK,
        ERROR,
        COMPLETE;}

    private AltsHandshakerStub stub;

    private AltsHandshakerStubTest.MockWriter writer;

    @Test
    public void sendSuccessfulMessageTest() throws Exception {
        writer.setServiceStatus(AltsHandshakerStubTest.Status.OK);
        sendSuccessfulMessage();
        stub.close();
    }

    @Test
    public void getServiceErrorTest() throws InterruptedException {
        writer.setServiceStatus(AltsHandshakerStubTest.Status.ERROR);
        sendAndExpectError();
        stub.close();
    }

    @Test
    public void getServiceCompleteTest() throws Exception {
        writer.setServiceStatus(AltsHandshakerStubTest.Status.COMPLETE);
        sendAndExpectComplete();
        stub.close();
    }

    @Test
    public void getUnexpectedMessageTest() throws Exception {
        writer.setServiceStatus(AltsHandshakerStubTest.Status.OK);
        writer.sendUnexpectedResponse();
        sendAndExpectUnexpectedMessage();
        stub.close();
    }

    @Test
    public void closeEarlyTest() throws InterruptedException {
        stub.close();
        sendAndExpectComplete();
    }

    private static class MockWriter implements StreamObserver<HandshakerReq> {
        private StreamObserver<HandshakerResp> reader;

        private AltsHandshakerStubTest.Status status = AltsHandshakerStubTest.Status.OK;

        private void setReader(StreamObserver<HandshakerResp> reader) {
            this.reader = reader;
        }

        private void setServiceStatus(AltsHandshakerStubTest.Status status) {
            this.status = status;
        }

        /**
         * Send a handshaker response to reader.
         */
        private void sendUnexpectedResponse() {
            reader.onNext(HandshakerResp.newBuilder().build());
        }

        /**
         * Mock writer onNext. Will respond based on the server status.
         */
        @Override
        public void onNext(final HandshakerReq req) {
            switch (status) {
                case OK :
                    HandshakerResp.Builder resp = HandshakerResp.newBuilder();
                    reader.onNext(resp.setOutFrames(req.getNext().getInBytes()).build());
                    break;
                case ERROR :
                    reader.onError(new RuntimeException());
                    break;
                case COMPLETE :
                    reader.onCompleted();
                    break;
                default :
                    return;
            }
        }

        @Override
        public void onError(Throwable t) {
        }

        /**
         * Mock writer onComplete.
         */
        @Override
        public void onCompleted() {
            reader.onCompleted();
        }
    }
}

