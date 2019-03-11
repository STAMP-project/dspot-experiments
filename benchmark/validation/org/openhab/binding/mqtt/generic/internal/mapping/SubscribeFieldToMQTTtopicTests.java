/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mqtt.generic.internal.mapping;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.openhab.binding.mqtt.generic.internal.mapping.SubscribeFieldToMQTTtopic.FieldChanged;


/**
 * Tests cases for {@link SubscribeFieldToMQTTtopic}.
 *
 * @author David Graeff - Initial contribution
 */
public class SubscribeFieldToMQTTtopicTests {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    private @interface TestValue {
        String value() default "";
    }

    @TopicPrefix
    public static class Attributes extends AbstractMqttAttributeClass {
        @SuppressWarnings("unused")
        public transient String ignoreTransient = "";

        @SuppressWarnings("unused")
        public final String ignoreFinal = "";

        @SubscribeFieldToMQTTtopicTests.TestValue("string")
        public String aString;

        @SubscribeFieldToMQTTtopicTests.TestValue("false")
        public Boolean aBoolean;

        @SubscribeFieldToMQTTtopicTests.TestValue("10")
        public Long aLong;

        @SubscribeFieldToMQTTtopicTests.TestValue("10")
        public Integer aInteger;

        @SubscribeFieldToMQTTtopicTests.TestValue("10")
        public BigDecimal aDecimal;

        @SubscribeFieldToMQTTtopicTests.TestValue("10")
        @TopicPrefix("a")
        public int Int = 24;

        @SubscribeFieldToMQTTtopicTests.TestValue("false")
        public boolean aBool = true;

        @SubscribeFieldToMQTTtopicTests.TestValue("abc,def")
        @MQTTvalueTransform(splitCharacter = ",")
        public String[] properties;

        public enum ReadyState {

            unknown,
            init,
            ready;}

        @SubscribeFieldToMQTTtopicTests.TestValue("init")
        public SubscribeFieldToMQTTtopicTests.Attributes.ReadyState state = SubscribeFieldToMQTTtopicTests.Attributes.ReadyState.unknown;

        public enum DataTypeEnum {

            unknown,
            integer_,
            float_;}

        @SubscribeFieldToMQTTtopicTests.TestValue("integer")
        @MQTTvalueTransform(suffix = "_")
        public SubscribeFieldToMQTTtopicTests.Attributes.DataTypeEnum datatype = SubscribeFieldToMQTTtopicTests.Attributes.DataTypeEnum.unknown;

        @Override
        @NonNull
        public Object getFieldsOf() {
            return this;
        }
    }

    SubscribeFieldToMQTTtopicTests.Attributes attributes = new SubscribeFieldToMQTTtopicTests.Attributes();

    @Mock
    MqttBrokerConnection connection;

    @Mock
    SubscribeFieldToMQTTtopic fieldSubscriber;

    @Mock
    FieldChanged fieldChanged;

    @Test(expected = TimeoutException.class)
    public void TimeoutIfNoMessageReceive() throws InterruptedException, NoSuchFieldException, ExecutionException, TimeoutException {
        final Field field = SubscribeFieldToMQTTtopicTests.Attributes.class.getField("Int");
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        SubscribeFieldToMQTTtopic subscriber = new SubscribeFieldToMQTTtopic(scheduler, field, fieldChanged, "homie/device123", false);
        subscriber.subscribeAndReceive(connection, 1000).get(50, TimeUnit.MILLISECONDS);
    }

    @Test(expected = ExecutionException.class)
    public void MandatoryMissing() throws InterruptedException, NoSuchFieldException, ExecutionException, TimeoutException {
        final Field field = SubscribeFieldToMQTTtopicTests.Attributes.class.getField("Int");
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        SubscribeFieldToMQTTtopic subscriber = new SubscribeFieldToMQTTtopic(scheduler, field, fieldChanged, "homie/device123", true);
        subscriber.subscribeAndReceive(connection, 50).get();
    }

    @Test
    public void MessageReceive() throws InterruptedException, NoSuchFieldException, ExecutionException, TimeoutException {
        final FieldChanged changed = ( field, value) -> {
            try {
                field.set(attributes.getFieldsOf(), value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                fail(e.getMessage());
            }
        };
        final Field field = SubscribeFieldToMQTTtopicTests.Attributes.class.getField("Int");
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        SubscribeFieldToMQTTtopic subscriber = new SubscribeFieldToMQTTtopic(scheduler, field, changed, "homie/device123", false);
        CompletableFuture<@Nullable
        Void> future = subscriber.subscribeAndReceive(connection, 1000);
        // Simulate a received MQTT message
        subscriber.processMessage("ignored", "10".getBytes());
        // No timeout should happen
        future.get(50, TimeUnit.MILLISECONDS);
        Assert.assertThat(attributes.Int, CoreMatchers.is(10));
    }
}

