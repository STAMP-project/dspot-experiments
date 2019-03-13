/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.messaging.simp.user;


import SimpMessageType.CONNECT;
import SimpMessageType.MESSAGE;
import SimpMessageType.SUBSCRIBE;
import SimpMessageType.UNSUBSCRIBE;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.TestPrincipal;
import org.springframework.util.StringUtils;


/**
 * Unit tests for
 * {@link org.springframework.messaging.simp.user.DefaultUserDestinationResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultUserDestinationResolverTests {
    private DefaultUserDestinationResolver resolver;

    private SimpUserRegistry registry;

    @Test
    public void handleSubscribe() {
        TestPrincipal user = new TestPrincipal("joe");
        String sourceDestination = "/user/queue/foo";
        Message<?> message = createMessage(SUBSCRIBE, user, "123", sourceDestination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(sourceDestination, actual.getSourceDestination());
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-user123", actual.getTargetDestinations().iterator().next());
        Assert.assertEquals(sourceDestination, actual.getSubscribeDestination());
        Assert.assertEquals(user.getName(), actual.getUser());
    }

    // SPR-14044
    @Test
    public void handleSubscribeForDestinationWithoutLeadingSlash() {
        this.resolver.setRemoveLeadingSlash(true);
        TestPrincipal user = new TestPrincipal("joe");
        String destination = "/user/jms.queue.call";
        Message<?> message = createMessage(SUBSCRIBE, user, "123", destination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("jms.queue.call-user123", actual.getTargetDestinations().iterator().next());
        Assert.assertEquals(destination, actual.getSubscribeDestination());
    }

    // SPR-11325
    @Test
    public void handleSubscribeOneUserMultipleSessions() {
        TestSimpUser simpUser = new TestSimpUser("joe");
        simpUser.addSessions(new TestSimpSession("123"), new TestSimpSession("456"));
        Mockito.when(this.registry.getUser("joe")).thenReturn(simpUser);
        TestPrincipal user = new TestPrincipal("joe");
        Message<?> message = createMessage(SUBSCRIBE, user, "456", "/user/queue/foo");
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-user456", actual.getTargetDestinations().iterator().next());
    }

    @Test
    public void handleSubscribeNoUser() {
        String sourceDestination = "/user/queue/foo";
        Message<?> message = createMessage(SUBSCRIBE, null, "123", sourceDestination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(sourceDestination, actual.getSourceDestination());
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals(("/queue/foo-user" + "123"), actual.getTargetDestinations().iterator().next());
        Assert.assertEquals(sourceDestination, actual.getSubscribeDestination());
        Assert.assertNull(actual.getUser());
    }

    @Test
    public void handleUnsubscribe() {
        TestPrincipal user = new TestPrincipal("joe");
        Message<?> message = createMessage(UNSUBSCRIBE, user, "123", "/user/queue/foo");
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-user123", actual.getTargetDestinations().iterator().next());
    }

    @Test
    public void handleMessage() {
        TestPrincipal user = new TestPrincipal("joe");
        String sourceDestination = "/user/joe/queue/foo";
        Message<?> message = createMessage(MESSAGE, user, "123", sourceDestination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(sourceDestination, actual.getSourceDestination());
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-user123", actual.getTargetDestinations().iterator().next());
        Assert.assertEquals("/user/queue/foo", actual.getSubscribeDestination());
        Assert.assertEquals(user.getName(), actual.getUser());
    }

    // SPR-14044
    @Test
    public void handleMessageForDestinationWithDotSeparator() {
        this.resolver.setRemoveLeadingSlash(true);
        TestPrincipal user = new TestPrincipal("joe");
        String destination = "/user/joe/jms.queue.call";
        Message<?> message = createMessage(MESSAGE, user, "123", destination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("jms.queue.call-user123", actual.getTargetDestinations().iterator().next());
        Assert.assertEquals("/user/jms.queue.call", actual.getSubscribeDestination());
    }

    // SPR-12444
    @Test
    public void handleMessageToOtherUser() {
        TestSimpUser otherSimpUser = new TestSimpUser("anna");
        otherSimpUser.addSessions(new TestSimpSession("456"));
        Mockito.when(this.registry.getUser("anna")).thenReturn(otherSimpUser);
        TestPrincipal user = new TestPrincipal("joe");
        TestPrincipal otherUser = new TestPrincipal("anna");
        String sourceDestination = "/user/anna/queue/foo";
        Message<?> message = createMessage(MESSAGE, user, "456", sourceDestination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(sourceDestination, actual.getSourceDestination());
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-user456", actual.getTargetDestinations().iterator().next());
        Assert.assertEquals("/user/queue/foo", actual.getSubscribeDestination());
        Assert.assertEquals(otherUser.getName(), actual.getUser());
    }

    @Test
    public void handleMessageEncodedUserName() {
        String userName = "http://joe.openid.example.org/";
        TestSimpUser simpUser = new TestSimpUser(userName);
        simpUser.addSessions(new TestSimpSession("openid123"));
        Mockito.when(this.registry.getUser(userName)).thenReturn(simpUser);
        String destination = ("/user/" + (StringUtils.replace(userName, "/", "%2F"))) + "/queue/foo";
        Message<?> message = createMessage(MESSAGE, new TestPrincipal("joe"), null, destination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-useropenid123", actual.getTargetDestinations().iterator().next());
    }

    @Test
    public void handleMessageWithNoUser() {
        String sourceDestination = "/user/" + ("123" + "/queue/foo");
        Message<?> message = createMessage(MESSAGE, null, "123", sourceDestination);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertEquals(sourceDestination, actual.getSourceDestination());
        Assert.assertEquals(1, actual.getTargetDestinations().size());
        Assert.assertEquals("/queue/foo-user123", actual.getTargetDestinations().iterator().next());
        Assert.assertEquals("/user/queue/foo", actual.getSubscribeDestination());
        Assert.assertNull(actual.getUser());
    }

    @Test
    public void ignoreMessage() {
        // no destination
        TestPrincipal user = new TestPrincipal("joe");
        Message<?> message = createMessage(MESSAGE, user, "123", null);
        UserDestinationResult actual = this.resolver.resolveDestination(message);
        Assert.assertNull(actual);
        // not a user destination
        message = createMessage(MESSAGE, user, "123", "/queue/foo");
        actual = this.resolver.resolveDestination(message);
        Assert.assertNull(actual);
        // subscribe + not a user destination
        message = createMessage(SUBSCRIBE, user, "123", "/queue/foo");
        actual = this.resolver.resolveDestination(message);
        Assert.assertNull(actual);
        // no match on message type
        message = createMessage(CONNECT, user, "123", "user/joe/queue/foo");
        actual = this.resolver.resolveDestination(message);
        Assert.assertNull(actual);
    }
}

