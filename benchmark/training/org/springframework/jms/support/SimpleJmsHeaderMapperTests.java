/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.jms.support;


import DeliveryMode.NON_PERSISTENT;
import DeliveryMode.PERSISTENT;
import JmsHeaderMapper.CONTENT_TYPE_PROPERTY;
import JmsHeaders.CORRELATION_ID;
import JmsHeaders.DELIVERY_MODE;
import JmsHeaders.DESTINATION;
import JmsHeaders.EXPIRATION;
import JmsHeaders.MESSAGE_ID;
import JmsHeaders.PRIORITY;
import JmsHeaders.REDELIVERED;
import JmsHeaders.REPLY_TO;
import JmsHeaders.TIMESTAMP;
import JmsHeaders.TYPE;
import MessageHeaders.CONTENT_TYPE;
import java.util.Date;
import javax.jms.Destination;
import javax.jms.JMSException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jms.StubTextMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.javax.jms.Message;


/**
 *
 *
 * @author Mark Fisher
 * @author Gary Russel
 * @author Stephane Nicoll
 */
public class SimpleJmsHeaderMapperTests {
    private final SimpleJmsHeaderMapper mapper = new SimpleJmsHeaderMapper();

    // Outbound mapping
    @Test
    public void jmsReplyToMappedFromHeader() throws JMSException {
        Destination replyTo = new Destination() {};
        Message<String> message = initBuilder().setHeader(REPLY_TO, replyTo).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNotNull(jmsMessage.getJMSReplyTo());
        Assert.assertSame(replyTo, jmsMessage.getJMSReplyTo());
    }

    @Test
    public void JmsReplyToIgnoredIfIncorrectType() throws JMSException {
        Message<String> message = initBuilder().setHeader(REPLY_TO, "not-a-destination").build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSReplyTo());
    }

    @Test
    public void jmsCorrelationIdMappedFromHeader() throws JMSException {
        String jmsCorrelationId = "ABC-123";
        Message<String> message = initBuilder().setHeader(CORRELATION_ID, jmsCorrelationId).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNotNull(jmsMessage.getJMSCorrelationID());
        Assert.assertEquals(jmsCorrelationId, jmsMessage.getJMSCorrelationID());
    }

    @Test
    public void jmsCorrelationIdNumberConvertsToString() throws JMSException {
        Message<String> message = initBuilder().setHeader(CORRELATION_ID, 123).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertEquals("123", jmsMessage.getJMSCorrelationID());
    }

    @Test
    public void jmsCorrelationIdIgnoredIfIncorrectType() throws JMSException {
        Message<String> message = initBuilder().setHeader(CORRELATION_ID, new Date()).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSCorrelationID());
    }

    @Test
    public void jmsTypeMappedFromHeader() throws JMSException {
        String jmsType = "testing";
        Message<String> message = initBuilder().setHeader(TYPE, jmsType).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNotNull(jmsMessage.getJMSType());
        Assert.assertEquals(jmsType, jmsMessage.getJMSType());
    }

    @Test
    public void jmsTypeIgnoredIfIncorrectType() throws JMSException {
        Message<String> message = initBuilder().setHeader(TYPE, 123).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSType());
    }

    @Test
    public void jmsReadOnlyPropertiesNotMapped() throws JMSException {
        Message<String> message = initBuilder().setHeader(DESTINATION, new Destination() {}).setHeader(DELIVERY_MODE, NON_PERSISTENT).setHeader(EXPIRATION, 1000L).setHeader(MESSAGE_ID, "abc-123").setHeader(PRIORITY, 9).setHeader(REDELIVERED, true).setHeader(TIMESTAMP, System.currentTimeMillis()).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSDestination());
        Assert.assertEquals(PERSISTENT, jmsMessage.getJMSDeliveryMode());
        Assert.assertEquals(0, jmsMessage.getJMSExpiration());
        Assert.assertNull(jmsMessage.getJMSMessageID());
        Assert.assertEquals(javax.jms.Message, jmsMessage.getJMSPriority());
        Assert.assertFalse(jmsMessage.getJMSRedelivered());
        Assert.assertEquals(0, jmsMessage.getJMSTimestamp());
    }

    @Test
    public void contentTypePropertyMappedFromHeader() throws JMSException {
        Message<String> message = initBuilder().setHeader(CONTENT_TYPE, "foo").build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Object value = jmsMessage.getObjectProperty(CONTENT_TYPE_PROPERTY);
        Assert.assertNotNull(value);
        Assert.assertEquals("foo", value);
    }

    @Test
    public void userDefinedPropertyMappedFromHeader() throws JMSException {
        Message<String> message = initBuilder().setHeader("foo", 123).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Object value = jmsMessage.getObjectProperty("foo");
        Assert.assertNotNull(value);
        Assert.assertEquals(Integer.class, value.getClass());
        Assert.assertEquals(123, ((Integer) (value)).intValue());
    }

    @Test
    public void userDefinedPropertyMappedFromHeaderWithCustomPrefix() throws JMSException {
        Message<String> message = initBuilder().setHeader("foo", 123).build();
        mapper.setOutboundPrefix("custom_");
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Object value = jmsMessage.getObjectProperty("custom_foo");
        Assert.assertNotNull(value);
        Assert.assertEquals(Integer.class, value.getClass());
        Assert.assertEquals(123, ((Integer) (value)).intValue());
    }

    @Test
    public void userDefinedPropertyWithUnsupportedType() throws JMSException {
        Destination destination = new Destination() {};
        Message<String> message = initBuilder().setHeader("destination", destination).build();
        javax.jms.Message jmsMessage = new StubTextMessage();
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Object value = jmsMessage.getObjectProperty("destination");
        Assert.assertNull(value);
    }

    @Test
    public void attemptToReadDisallowedCorrelationIdPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public String getJMSCorrelationID() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, CORRELATION_ID);
    }

    @Test
    public void attemptToReadDisallowedDestinationPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public Destination getJMSDestination() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, DESTINATION);
    }

    @Test
    public void attemptToReadDisallowedDeliveryModePropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public int getJMSDeliveryMode() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, DELIVERY_MODE);
    }

    @Test
    public void attemptToReadDisallowedExpirationPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public long getJMSExpiration() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, EXPIRATION);
    }

    @Test
    public void attemptToReadDisallowedMessageIdPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public String getJMSMessageID() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, MESSAGE_ID);
    }

    @Test
    public void attemptToReadDisallowedPriorityPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public int getJMSPriority() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, PRIORITY);
    }

    @Test
    public void attemptToReadDisallowedReplyToPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public Destination getJMSReplyTo() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, REPLY_TO);
    }

    @Test
    public void attemptToReadDisallowedRedeliveredPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public boolean getJMSRedelivered() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, REDELIVERED);
    }

    @Test
    public void attemptToReadDisallowedTypePropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public String getJMSType() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, TYPE);
    }

    @Test
    public void attemptToReadDisallowedTimestampPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public long getJMSTimestamp() throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, TIMESTAMP);
    }

    @Test
    public void attemptToReadDisallowedUserPropertyIsNotFatal() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public Object getObjectProperty(String name) throws JMSException {
                if (name.equals("fail")) {
                    throw new JMSException("illegal property");
                } else {
                    return super.getObjectProperty(name);
                }
            }
        };
        jmsMessage.setBooleanProperty("fail", true);
        assertAttemptReadDisallowedPropertyIsNotFatal(jmsMessage, "fail");
    }

    // Inbound mapping
    @Test
    public void jmsCorrelationIdMappedToHeader() throws JMSException {
        String correlationId = "ABC-123";
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSCorrelationID(correlationId);
        assertInboundHeader(jmsMessage, CORRELATION_ID, correlationId);
    }

    @Test
    public void destinationMappedToHeader() throws JMSException {
        Destination destination = new Destination() {};
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSDestination(destination);
        assertInboundHeader(jmsMessage, DESTINATION, destination);
    }

    @Test
    public void jmsDeliveryModeMappedToHeader() throws JMSException {
        int deliveryMode = 1;
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSDeliveryMode(deliveryMode);
        assertInboundHeader(jmsMessage, DELIVERY_MODE, deliveryMode);
    }

    @Test
    public void jmsExpirationMappedToHeader() throws JMSException {
        long expiration = 1000L;
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSExpiration(expiration);
        assertInboundHeader(jmsMessage, EXPIRATION, expiration);
    }

    @Test
    public void jmsMessageIdMappedToHeader() throws JMSException {
        String messageId = "ID:ABC-123";
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSMessageID(messageId);
        assertInboundHeader(jmsMessage, MESSAGE_ID, messageId);
    }

    @Test
    public void jmsPriorityMappedToHeader() throws JMSException {
        int priority = 8;
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSPriority(priority);
        assertInboundHeader(jmsMessage, PRIORITY, priority);
    }

    @Test
    public void jmsReplyToMappedToHeader() throws JMSException {
        Destination replyTo = new Destination() {};
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSReplyTo(replyTo);
        assertInboundHeader(jmsMessage, REPLY_TO, replyTo);
    }

    @Test
    public void jmsTypeMappedToHeader() throws JMSException {
        String type = "testing";
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSType(type);
        assertInboundHeader(jmsMessage, TYPE, type);
    }

    @Test
    public void jmsTimestampMappedToHeader() throws JMSException {
        long timestamp = 123L;
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setJMSTimestamp(timestamp);
        assertInboundHeader(jmsMessage, TIMESTAMP, timestamp);
    }

    @Test
    public void contentTypePropertyMappedToHeader() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setStringProperty("content_type", "foo");
        assertInboundHeader(jmsMessage, CONTENT_TYPE, "foo");
    }

    @Test
    public void userDefinedPropertyMappedToHeader() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setIntProperty("foo", 123);
        assertInboundHeader(jmsMessage, "foo", 123);
    }

    @Test
    public void userDefinedPropertyMappedToHeaderWithCustomPrefix() throws JMSException {
        javax.jms.Message jmsMessage = new StubTextMessage();
        jmsMessage.setIntProperty("foo", 123);
        mapper.setInboundPrefix("custom_");
        assertInboundHeader(jmsMessage, "custom_foo", 123);
    }

    @Test
    public void propertyMappingExceptionIsNotFatal() throws JMSException {
        Message<String> message = initBuilder().setHeader("foo", 123).setHeader("bad", 456).setHeader("bar", 789).build();
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public void setObjectProperty(String name, Object value) throws JMSException {
                if (name.equals("bad")) {
                    throw new JMSException("illegal property");
                }
                super.setObjectProperty(name, value);
            }
        };
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Object foo = jmsMessage.getObjectProperty("foo");
        Assert.assertNotNull(foo);
        Object bar = jmsMessage.getObjectProperty("bar");
        Assert.assertNotNull(bar);
        Object bad = jmsMessage.getObjectProperty("bad");
        Assert.assertNull(bad);
    }

    @Test
    public void illegalArgumentExceptionIsNotFatal() throws JMSException {
        Message<String> message = initBuilder().setHeader("foo", 123).setHeader("bad", 456).setHeader("bar", 789).build();
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public void setObjectProperty(String name, Object value) throws JMSException {
                if (name.equals("bad")) {
                    throw new IllegalArgumentException("illegal property");
                }
                super.setObjectProperty(name, value);
            }
        };
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Object foo = jmsMessage.getObjectProperty("foo");
        Assert.assertNotNull(foo);
        Object bar = jmsMessage.getObjectProperty("bar");
        Assert.assertNotNull(bar);
        Object bad = jmsMessage.getObjectProperty("bad");
        Assert.assertNull(bad);
    }

    @Test
    public void attemptToWriteDisallowedReplyToPropertyIsNotFatal() throws JMSException {
        Message<String> message = initBuilder().setHeader(REPLY_TO, new Destination() {}).setHeader("foo", "bar").build();
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public void setJMSReplyTo(Destination replyTo) throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSReplyTo());
        Assert.assertNotNull(jmsMessage.getStringProperty("foo"));
        Assert.assertEquals("bar", jmsMessage.getStringProperty("foo"));
    }

    @Test
    public void attemptToWriteDisallowedTypePropertyIsNotFatal() throws JMSException {
        Message<String> message = initBuilder().setHeader(TYPE, "someType").setHeader("foo", "bar").build();
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public void setJMSType(String type) throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSType());
        Assert.assertNotNull(jmsMessage.getStringProperty("foo"));
        Assert.assertEquals("bar", jmsMessage.getStringProperty("foo"));
    }

    @Test
    public void attemptToWriteDisallowedCorrelationIdStringPropertyIsNotFatal() throws JMSException {
        Message<String> message = initBuilder().setHeader(CORRELATION_ID, "abc").setHeader("foo", "bar").build();
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public void setJMSCorrelationID(String correlationId) throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSCorrelationID());
        Assert.assertNotNull(jmsMessage.getStringProperty("foo"));
        Assert.assertEquals("bar", jmsMessage.getStringProperty("foo"));
    }

    @Test
    public void attemptToWriteDisallowedCorrelationIdNumberPropertyIsNotFatal() throws JMSException {
        Message<String> message = initBuilder().setHeader(CORRELATION_ID, 123).setHeader("foo", "bar").build();
        javax.jms.Message jmsMessage = new StubTextMessage() {
            @Override
            public void setJMSCorrelationID(String correlationId) throws JMSException {
                throw new JMSException("illegal property");
            }
        };
        mapper.fromHeaders(message.getHeaders(), jmsMessage);
        Assert.assertNull(jmsMessage.getJMSCorrelationID());
        Assert.assertNotNull(jmsMessage.getStringProperty("foo"));
        Assert.assertEquals("bar", jmsMessage.getStringProperty("foo"));
    }
}

