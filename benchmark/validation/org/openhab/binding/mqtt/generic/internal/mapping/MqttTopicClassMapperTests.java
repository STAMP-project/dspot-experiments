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
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.openhab.binding.mqtt.generic.internal.mapping.AbstractMqttAttributeClass.AttributeChanged;


/**
 * Tests cases for {@link AbstractMqttAttributeClass}.
 *
 * @author David Graeff - Initial contribution
 */
public class MqttTopicClassMapperTests {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    private @interface TestValue {
        String value() default "";
    }

    @TopicPrefix
    public static class Attributes extends AbstractMqttAttributeClass {
        public transient String ignoreTransient = "";

        public final String ignoreFinal = "";

        @MqttTopicClassMapperTests.TestValue("string")
        public String aString;

        @MqttTopicClassMapperTests.TestValue("false")
        public Boolean aBoolean;

        @MqttTopicClassMapperTests.TestValue("10")
        public Long aLong;

        @MqttTopicClassMapperTests.TestValue("10")
        public Integer aInteger;

        @MqttTopicClassMapperTests.TestValue("10")
        public BigDecimal aDecimal;

        @MqttTopicClassMapperTests.TestValue("10")
        @TopicPrefix("a")
        public int Int = 24;

        @MqttTopicClassMapperTests.TestValue("false")
        public boolean aBool = true;

        @MqttTopicClassMapperTests.TestValue("abc,def")
        @MQTTvalueTransform(splitCharacter = ",")
        public String[] properties;

        public enum ReadyState {

            unknown,
            init,
            ready;}

        @MqttTopicClassMapperTests.TestValue("init")
        public MqttTopicClassMapperTests.Attributes.ReadyState state = MqttTopicClassMapperTests.Attributes.ReadyState.unknown;

        public enum DataTypeEnum {

            unknown,
            integer_,
            float_;}

        @MqttTopicClassMapperTests.TestValue("integer")
        @MQTTvalueTransform(suffix = "_")
        public MqttTopicClassMapperTests.Attributes.DataTypeEnum datatype = MqttTopicClassMapperTests.Attributes.DataTypeEnum.unknown;

        @Override
        @NonNull
        public Object getFieldsOf() {
            return this;
        }
    }

    @Mock
    MqttBrokerConnection connection;

    @Mock
    ScheduledExecutorService executor;

    @Mock
    AttributeChanged fieldChangedObserver;

    @Spy
    Object countInjectedFields = new Object();

    int injectedFields = 0;

    // A completed future is returned for a subscribe call to the attributes
    final CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);

    @Test
    public void subscribeToCorrectFields() {
        MqttTopicClassMapperTests.Attributes attributes = Mockito.spy(new MqttTopicClassMapperTests.Attributes());
        createSubscriber(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        // Subscribe now to all fields
        CompletableFuture<Void> future = attributes.subscribeAndReceive(connection, executor, "homie/device123", null, 10);
        Assert.assertThat(future.isDone(), CoreMatchers.is(true));
        Assert.assertThat(attributes.subscriptions.size(), CoreMatchers.is((10 + (injectedFields))));
    }

    // TODO timeout
    @SuppressWarnings({ "null", "unused" })
    @Test
    public void subscribeAndReceive() throws IllegalAccessException, IllegalArgumentException {
        final MqttTopicClassMapperTests.Attributes attributes = Mockito.spy(new MqttTopicClassMapperTests.Attributes());
        createSubscriber(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.verify(connection, Mockito.times(0)).subscribe(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        // Subscribe now to all fields
        CompletableFuture<Void> future = attributes.subscribeAndReceive(connection, executor, "homie/device123", fieldChangedObserver, 10);
        Assert.assertThat(future.isDone(), CoreMatchers.is(true));
        // We expect 10 subscriptions now
        Assert.assertThat(attributes.subscriptions.size(), CoreMatchers.is((10 + (injectedFields))));
        int loopCounter = 0;
        // Assign each field the value of the test annotation via the processMessage method
        for (SubscribeFieldToMQTTtopic f : attributes.subscriptions) {
            @Nullable
            MqttTopicClassMapperTests.TestValue annotation = f.field.getAnnotation(MqttTopicClassMapperTests.TestValue.class);
            // A non-annotated field means a Mockito injected field.
            // Ignore that and complete the corresponding future.
            if (annotation == null) {
                f.future.complete(null);
                continue;
            }
            Mockito.verify(f).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
            // Simulate a received MQTT value and use the annotation data as input.
            f.processMessage(f.topic, annotation.value().getBytes());
            Mockito.verify(fieldChangedObserver, Mockito.times((++loopCounter))).attributeChanged(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
            // Check each value if the assignment worked
            if (!(f.field.getType().isArray())) {
                Assert.assertNotNull(((f.field.getName()) + " is null"), f.field.get(attributes));
                // Consider if a mapToField was used that would manipulate the received value
                MQTTvalueTransform mapToField = f.field.getAnnotation(MQTTvalueTransform.class);
                String prefix = (mapToField != null) ? mapToField.prefix() : "";
                String suffix = (mapToField != null) ? mapToField.suffix() : "";
                Assert.assertThat(f.field.get(attributes).toString(), CoreMatchers.is(((prefix + (annotation.value())) + suffix)));
            } else {
                Assert.assertThat(Stream.of(((String[]) (f.field.get(attributes)))).reduce(( v, i) -> (v + ",") + i).orElse(""), CoreMatchers.is(annotation.value()));
            }
        }
        Assert.assertThat(future.isDone(), CoreMatchers.is(true));
    }
}

