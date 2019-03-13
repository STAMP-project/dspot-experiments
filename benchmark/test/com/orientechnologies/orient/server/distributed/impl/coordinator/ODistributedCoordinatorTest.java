package com.orientechnologies.orient.server.distributed.impl.coordinator;


import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.server.distributed.impl.coordinator.transaction.OSessionOperationId;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralNodeRequest;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralNodeResponse;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralSubmitRequest;
import com.orientechnologies.orient.server.distributed.impl.structural.OStructuralSubmitResponse;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ODistributedCoordinatorTest {
    @Test
    public void simpleOperationTest() throws InterruptedException {
        CountDownLatch responseReceived = new CountDownLatch(1);
        OOperationLog operationLog = new MockOperationLog();
        ODistributedCoordinator coordinator = new ODistributedCoordinator(Executors.newSingleThreadExecutor(), operationLog, null, null);
        ODistributedCoordinatorTest.MockDistributedChannel channel = new ODistributedCoordinatorTest.MockDistributedChannel();
        channel.coordinator = coordinator;
        ODistributedMember one = new ODistributedMember("one", null, channel);
        channel.member = one;
        coordinator.join(one);
        coordinator.submit(one, new OSessionOperationId(), new OSubmitRequest() {
            @Override
            public void begin(ODistributedMember requester, OSessionOperationId operationId, ODistributedCoordinator coordinator) {
                ODistributedCoordinatorTest.MockNodeRequest nodeRequest = new ODistributedCoordinatorTest.MockNodeRequest();
                coordinator.sendOperation(this, nodeRequest, new OResponseHandler() {
                    @Override
                    public boolean receive(ODistributedCoordinator coordinator, ORequestContext context, ODistributedMember member, ONodeResponse response) {
                        if ((context.getResponses().size()) == 1) {
                            responseReceived.countDown();
                        }
                        return (context.getResponses().size()) == (context.getInvolvedMembers().size());
                    }

                    @Override
                    public boolean timeout(ODistributedCoordinator coordinator, ORequestContext context) {
                        return true;
                    }
                });
            }

            @Override
            public void serialize(DataOutput output) {
            }

            @Override
            public void deserialize(DataInput input) {
            }

            @Override
            public int getRequestType() {
                return 0;
            }
        });
        Assert.assertTrue(responseReceived.await(1, TimeUnit.SECONDS));
        coordinator.close();
        Assert.assertEquals(0, coordinator.getContexts().size());
    }

    @Test
    public void testTwoPhase() throws InterruptedException {
        CountDownLatch responseReceived = new CountDownLatch(1);
        OOperationLog operationLog = new MockOperationLog();
        ODistributedCoordinator coordinator = new ODistributedCoordinator(Executors.newSingleThreadExecutor(), operationLog, null, null);
        ODistributedCoordinatorTest.MockDistributedChannel channel = new ODistributedCoordinatorTest.MockDistributedChannel();
        channel.coordinator = coordinator;
        channel.reply = responseReceived;
        ODistributedMember one = new ODistributedMember("one", null, channel);
        channel.member = one;
        coordinator.join(one);
        coordinator.submit(one, new OSessionOperationId(), new OSubmitRequest() {
            @Override
            public void begin(ODistributedMember requester, OSessionOperationId operationId, ODistributedCoordinator coordinator) {
                ODistributedCoordinatorTest.MockNodeRequest nodeRequest = new ODistributedCoordinatorTest.MockNodeRequest();
                coordinator.sendOperation(this, nodeRequest, new OResponseHandler() {
                    @Override
                    public boolean receive(ODistributedCoordinator coordinator, ORequestContext context, ODistributedMember member, ONodeResponse response) {
                        if ((context.getResponses().size()) == 1) {
                            coordinator.sendOperation(null, new ONodeRequest() {
                                @Override
                                public ONodeResponse execute(ODistributedMember nodeFrom, OLogId opId, ODistributedExecutor executor, ODatabaseDocumentInternal session) {
                                    return null;
                                }

                                @Override
                                public void serialize(DataOutput output) {
                                }

                                @Override
                                public void deserialize(DataInput input) {
                                }

                                @Override
                                public int getRequestType() {
                                    return 0;
                                }
                            }, new OResponseHandler() {
                                @Override
                                public boolean receive(ODistributedCoordinator coordinator, ORequestContext context, ODistributedMember member, ONodeResponse response) {
                                    if ((context.getResponses().size()) == 1) {
                                        member.reply(new OSessionOperationId(), new OSubmitResponse() {
                                            @Override
                                            public void serialize(DataOutput output) throws IOException {
                                            }

                                            @Override
                                            public void deserialize(DataInput input) throws IOException {
                                            }

                                            @Override
                                            public int getResponseType() {
                                                return 0;
                                            }
                                        });
                                    }
                                    return (context.getResponses().size()) == (context.getInvolvedMembers().size());
                                }

                                @Override
                                public boolean timeout(ODistributedCoordinator coordinator, ORequestContext context) {
                                    return true;
                                }
                            });
                        }
                        return (context.getResponses().size()) == (context.getInvolvedMembers().size());
                    }

                    @Override
                    public boolean timeout(ODistributedCoordinator coordinator, ORequestContext context) {
                        return true;
                    }
                });
            }

            @Override
            public void serialize(DataOutput output) {
            }

            @Override
            public void deserialize(DataInput input) {
            }

            @Override
            public int getRequestType() {
                return 0;
            }
        });
        Assert.assertTrue(responseReceived.await(1, TimeUnit.SECONDS));
        coordinator.close();
        Assert.assertEquals(0, coordinator.getContexts().size());
    }

    @Test
    public void simpleTimeoutTest() throws InterruptedException {
        CountDownLatch timedOut = new CountDownLatch(1);
        OOperationLog operationLog = new MockOperationLog();
        ODistributedCoordinator coordinator = new ODistributedCoordinator(Executors.newSingleThreadExecutor(), operationLog, null, null);
        ODistributedCoordinatorTest.MockDistributedChannel channel = new ODistributedCoordinatorTest.MockDistributedChannel();
        channel.coordinator = coordinator;
        ODistributedMember one = new ODistributedMember("one", null, channel);
        channel.member = one;
        coordinator.join(one);
        coordinator.submit(one, new OSessionOperationId(), new OSubmitRequest() {
            @Override
            public void begin(ODistributedMember requester, OSessionOperationId operationId, ODistributedCoordinator coordinator) {
                ODistributedCoordinatorTest.MockNodeRequest nodeRequest = new ODistributedCoordinatorTest.MockNodeRequest();
                coordinator.sendOperation(this, nodeRequest, new OResponseHandler() {
                    @Override
                    public boolean receive(ODistributedCoordinator coordinator, ORequestContext context, ODistributedMember member, ONodeResponse response) {
                        return false;
                    }

                    @Override
                    public boolean timeout(ODistributedCoordinator coordinator, ORequestContext context) {
                        timedOut.countDown();
                        return true;
                    }
                });
            }

            @Override
            public void serialize(DataOutput output) throws IOException {
            }

            @Override
            public void deserialize(DataInput input) throws IOException {
            }

            @Override
            public int getRequestType() {
                return 0;
            }
        });
        // This is 2 seconds because timeout is hard coded with 1 sec now
        Assert.assertTrue(timedOut.await(2, TimeUnit.SECONDS));
        coordinator.close();
        Assert.assertEquals(0, coordinator.getContexts().size());
    }

    private static class MockDistributedChannel implements ODistributedChannel {
        public ODistributedCoordinator coordinator;

        public CountDownLatch reply;

        public ODistributedMember member;

        @Override
        public void sendRequest(String database, OLogId id, ONodeRequest request) {
            coordinator.receive(member, id, new ONodeResponse() {
                @Override
                public void serialize(DataOutput output) throws IOException {
                }

                @Override
                public void deserialize(DataInput input) throws IOException {
                }

                @Override
                public int getResponseType() {
                    return 0;
                }
            });
        }

        @Override
        public void sendResponse(String database, OLogId id, ONodeResponse nodeResponse) {
            Assert.assertTrue(false);
        }

        @Override
        public void sendResponse(OLogId opId, OStructuralNodeResponse response) {
        }

        @Override
        public void sendRequest(OLogId id, OStructuralNodeRequest request) {
        }

        @Override
        public void reply(OSessionOperationId operationId, OStructuralSubmitResponse response) {
        }

        @Override
        public void submit(OSessionOperationId operationId, OStructuralSubmitRequest request) {
        }

        @Override
        public void submit(String database, OSessionOperationId operationId, OSubmitRequest request) {
        }

        @Override
        public void reply(String database, OSessionOperationId operationId, OSubmitResponse response) {
            reply.countDown();
        }
    }

    private static class MockNodeRequest implements ONodeRequest {
        @Override
        public ONodeResponse execute(ODistributedMember nodeFrom, OLogId opId, ODistributedExecutor executor, ODatabaseDocumentInternal session) {
            return null;
        }

        @Override
        public void serialize(DataOutput output) throws IOException {
        }

        @Override
        public void deserialize(DataInput input) throws IOException {
        }

        @Override
        public int getRequestType() {
            return 0;
        }
    }
}

