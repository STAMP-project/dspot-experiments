/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.messaging.internal.sender;


import com.liferay.portal.kernel.executor.PortalExecutorManager;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.SerialDestination;
import com.liferay.portal.kernel.messaging.SynchronousDestination;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class DefaultSynchronousMessageSenderTest {
    @Test
    public void testSendToAsyncDestination() throws MessageBusException {
        SerialDestination serialDestination = new SerialDestination() {
            @Override
            public void open() {
                portalExecutorManager = _portalExecutorManager;
                super.open();
            }
        };
        serialDestination.setName("testSerialDestination");
        serialDestination.afterPropertiesSet();
        serialDestination.open();
        doTestSend(serialDestination);
    }

    @Test
    public void testSendToSynchronousDestination() throws MessageBusException {
        SynchronousDestination synchronousDestination = new SynchronousDestination();
        synchronousDestination.setName("testSynchronousDestination");
        synchronousDestination.afterPropertiesSet();
        synchronousDestination.open();
        doTestSend(synchronousDestination);
    }

    private DefaultSynchronousMessageSender _defaultSynchronousMessageSender;

    private MessageBus _messageBus;

    private PortalExecutorManager _portalExecutorManager;

    private class ReplayMessageListener implements MessageListener {
        public ReplayMessageListener(Object response) {
            _response = response;
        }

        @Override
        public void receive(Message message) {
            Message responseMessage = new Message();
            responseMessage.setDestinationName(message.getResponseDestinationName());
            responseMessage.setResponseId(message.getResponseId());
            responseMessage.setPayload(_response);
            _messageBus.sendMessage(message.getResponseDestinationName(), responseMessage);
        }

        private final Object _response;
    }
}

