/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.amqp;


import RabbitProperties.DirectContainer;
import RabbitProperties.SimpleContainer;
import org.junit.Test;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;


/**
 * Tests for {@link RabbitProperties}.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class RabbitPropertiesTests {
    private final RabbitProperties properties = new RabbitProperties();

    @Test
    public void hostDefaultsToLocalhost() {
        assertThat(this.properties.getHost()).isEqualTo("localhost");
    }

    @Test
    public void customHost() {
        this.properties.setHost("rabbit.example.com");
        assertThat(this.properties.getHost()).isEqualTo("rabbit.example.com");
    }

    @Test
    public void hostIsDeterminedFromFirstAddress() {
        this.properties.setAddresses("rabbit1.example.com:1234,rabbit2.example.com:2345");
        assertThat(this.properties.determineHost()).isEqualTo("rabbit1.example.com");
    }

    @Test
    public void determineHostReturnsHostPropertyWhenNoAddresses() {
        this.properties.setHost("rabbit.example.com");
        assertThat(this.properties.determineHost()).isEqualTo("rabbit.example.com");
    }

    @Test
    public void portDefaultsTo5672() {
        assertThat(this.properties.getPort()).isEqualTo(5672);
    }

    @Test
    public void customPort() {
        this.properties.setPort(1234);
        assertThat(this.properties.getPort()).isEqualTo(1234);
    }

    @Test
    public void determinePortReturnsPortOfFirstAddress() {
        this.properties.setAddresses("rabbit1.example.com:1234,rabbit2.example.com:2345");
        assertThat(this.properties.determinePort()).isEqualTo(1234);
    }

    @Test
    public void determinePortReturnsPortPropertyWhenNoAddresses() {
        this.properties.setPort(1234);
        assertThat(this.properties.determinePort()).isEqualTo(1234);
    }

    @Test
    public void determinePortReturnsDefaultAmqpPortWhenFirstAddressHasNoExplicitPort() {
        this.properties.setPort(1234);
        this.properties.setAddresses("rabbit1.example.com,rabbit2.example.com:2345");
        assertThat(this.properties.determinePort()).isEqualTo(5672);
    }

    @Test
    public void virtualHostDefaultsToNull() {
        assertThat(this.properties.getVirtualHost()).isNull();
    }

    @Test
    public void customVirtualHost() {
        this.properties.setVirtualHost("alpha");
        assertThat(this.properties.getVirtualHost()).isEqualTo("alpha");
    }

    @Test
    public void virtualHostRetainsALeadingSlash() {
        this.properties.setVirtualHost("/alpha");
        assertThat(this.properties.getVirtualHost()).isEqualTo("/alpha");
    }

    @Test
    public void determineVirtualHostReturnsVirtualHostOfFirstAddress() {
        this.properties.setAddresses("rabbit1.example.com:1234/alpha,rabbit2.example.com:2345/bravo");
        assertThat(this.properties.determineVirtualHost()).isEqualTo("alpha");
    }

    @Test
    public void determineVirtualHostReturnsPropertyWhenNoAddresses() {
        this.properties.setVirtualHost("alpha");
        assertThat(this.properties.determineVirtualHost()).isEqualTo("alpha");
    }

    @Test
    public void determineVirtualHostReturnsPropertyWhenFirstAddressHasNoVirtualHost() {
        this.properties.setVirtualHost("alpha");
        this.properties.setAddresses("rabbit1.example.com:1234,rabbit2.example.com:2345/bravo");
        assertThat(this.properties.determineVirtualHost()).isEqualTo("alpha");
    }

    @Test
    public void determineVirtualHostIsSlashWhenAddressHasTrailingSlash() {
        this.properties.setAddresses("amqp://root:password@otherhost:1111/");
        assertThat(this.properties.determineVirtualHost()).isEqualTo("/");
    }

    @Test
    public void emptyVirtualHostIsCoercedToASlash() {
        this.properties.setVirtualHost("");
        assertThat(this.properties.getVirtualHost()).isEqualTo("/");
    }

    @Test
    public void usernameDefaultsToGuest() {
        assertThat(this.properties.getUsername()).isEqualTo("guest");
    }

    @Test
    public void customUsername() {
        this.properties.setUsername("user");
        assertThat(this.properties.getUsername()).isEqualTo("user");
    }

    @Test
    public void determineUsernameReturnsUsernameOfFirstAddress() {
        this.properties.setAddresses(("user:secret@rabbit1.example.com:1234/alpha," + "rabbit2.example.com:2345/bravo"));
        assertThat(this.properties.determineUsername()).isEqualTo("user");
    }

    @Test
    public void determineUsernameReturnsPropertyWhenNoAddresses() {
        this.properties.setUsername("alice");
        assertThat(this.properties.determineUsername()).isEqualTo("alice");
    }

    @Test
    public void determineUsernameReturnsPropertyWhenFirstAddressHasNoUsername() {
        this.properties.setUsername("alice");
        this.properties.setAddresses(("rabbit1.example.com:1234/alpha," + "user:secret@rabbit2.example.com:2345/bravo"));
        assertThat(this.properties.determineUsername()).isEqualTo("alice");
    }

    @Test
    public void passwordDefaultsToGuest() {
        assertThat(this.properties.getPassword()).isEqualTo("guest");
    }

    @Test
    public void customPassword() {
        this.properties.setPassword("secret");
        assertThat(this.properties.getPassword()).isEqualTo("secret");
    }

    @Test
    public void determinePasswordReturnsPasswordOfFirstAddress() {
        this.properties.setAddresses(("user:secret@rabbit1.example.com:1234/alpha," + "rabbit2.example.com:2345/bravo"));
        assertThat(this.properties.determinePassword()).isEqualTo("secret");
    }

    @Test
    public void determinePasswordReturnsPropertyWhenNoAddresses() {
        this.properties.setPassword("secret");
        assertThat(this.properties.determinePassword()).isEqualTo("secret");
    }

    @Test
    public void determinePasswordReturnsPropertyWhenFirstAddressHasNoPassword() {
        this.properties.setPassword("12345678");
        this.properties.setAddresses(("rabbit1.example.com:1234/alpha," + "user:secret@rabbit2.example.com:2345/bravo"));
        assertThat(this.properties.determinePassword()).isEqualTo("12345678");
    }

    @Test
    public void addressesDefaultsToNull() {
        assertThat(this.properties.getAddresses()).isNull();
    }

    @Test
    public void customAddresses() {
        this.properties.setAddresses("user:secret@rabbit1.example.com:1234/alpha,rabbit2.example.com");
        assertThat(this.properties.getAddresses()).isEqualTo("user:secret@rabbit1.example.com:1234/alpha,rabbit2.example.com");
    }

    @Test
    public void determineAddressesReturnsAddressesWithJustHostAndPort() {
        this.properties.setAddresses("user:secret@rabbit1.example.com:1234/alpha,rabbit2.example.com");
        assertThat(this.properties.determineAddresses()).isEqualTo("rabbit1.example.com:1234,rabbit2.example.com:5672");
    }

    @Test
    public void determineAddressesUsesHostAndPortPropertiesWhenNoAddressesSet() {
        this.properties.setHost("rabbit.example.com");
        this.properties.setPort(1234);
        assertThat(this.properties.determineAddresses()).isEqualTo("rabbit.example.com:1234");
    }

    @Test
    public void simpleContainerUseConsistentDefaultValues() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        SimpleMessageListenerContainer container = factory.createListenerContainer();
        RabbitProperties.SimpleContainer simple = this.properties.getListener().getSimple();
        assertThat(simple.isAutoStartup()).isEqualTo(container.isAutoStartup());
        assertThat(container).hasFieldOrPropertyWithValue("missingQueuesFatal", simple.isMissingQueuesFatal());
    }

    @Test
    public void directContainerUseConsistentDefaultValues() {
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        DirectMessageListenerContainer container = factory.createListenerContainer();
        RabbitProperties.DirectContainer direct = this.properties.getListener().getDirect();
        assertThat(direct.isAutoStartup()).isEqualTo(container.isAutoStartup());
        assertThat(container).hasFieldOrPropertyWithValue("missingQueuesFatal", direct.isMissingQueuesFatal());
    }
}

