/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.openid;


import AxMessage.OPENID_NS_AX;
import OpenIDAuthenticationStatus.FAILURE;
import OpenIDAuthenticationStatus.SUCCESS;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchResponse;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Luke Taylor
 */
public class OpenID4JavaConsumerTests {
    List<OpenIDAttribute> attributes = Arrays.asList(new OpenIDAttribute("a", "b"), new OpenIDAttribute("b", "b", Arrays.asList("c")));

    @SuppressWarnings("deprecation")
    @Test
    public void beginConsumptionCreatesExpectedSessionData() throws Exception {
        ConsumerManager mgr = Mockito.mock(ConsumerManager.class);
        AuthRequest authReq = Mockito.mock(AuthRequest.class);
        DiscoveryInformation di = Mockito.mock(DiscoveryInformation.class);
        Mockito.when(mgr.authenticate(ArgumentMatchers.any(DiscoveryInformation.class), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(authReq);
        Mockito.when(mgr.associate(ArgumentMatchers.any())).thenReturn(di);
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(mgr, new OpenID4JavaConsumerTests.MockAttributesFactory());
        MockHttpServletRequest request = new MockHttpServletRequest();
        consumer.beginConsumption(request, "", "", "");
        assertThat(request.getSession().getAttribute("SPRING_SECURITY_OPEN_ID_ATTRIBUTES_FETCH_LIST")).isEqualTo(attributes);
        assertThat(request.getSession().getAttribute(DiscoveryInformation.class.getName())).isEqualTo(di);
        // Check with empty attribute fetch list
        consumer = new OpenID4JavaConsumer(mgr, new NullAxFetchListFactory());
        request = new MockHttpServletRequest();
        consumer.beginConsumption(request, "", "", "");
    }

    @Test(expected = OpenIDConsumerException.class)
    public void discoveryExceptionRaisesOpenIDException() throws Exception {
        ConsumerManager mgr = Mockito.mock(ConsumerManager.class);
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(mgr, new NullAxFetchListFactory());
        Mockito.when(mgr.discover(ArgumentMatchers.any())).thenThrow(new DiscoveryException("msg"));
        consumer.beginConsumption(new MockHttpServletRequest(), "", "", "");
    }

    @Test
    public void messageOrConsumerAuthenticationExceptionRaisesOpenIDException() throws Exception {
        ConsumerManager mgr = Mockito.mock(ConsumerManager.class);
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(mgr, new NullAxFetchListFactory());
        Mockito.when(mgr.authenticate(ArgumentMatchers.<DiscoveryInformation>any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new MessageException("msg"), new ConsumerException("msg"));
        try {
            consumer.beginConsumption(new MockHttpServletRequest(), "", "", "");
            fail("OpenIDConsumerException was not thrown");
        } catch (OpenIDConsumerException expected) {
        }
        try {
            consumer.beginConsumption(new MockHttpServletRequest(), "", "", "");
            fail("OpenIDConsumerException was not thrown");
        } catch (OpenIDConsumerException expected) {
        }
    }

    @Test
    public void failedVerificationReturnsFailedAuthenticationStatus() throws Exception {
        ConsumerManager mgr = Mockito.mock(ConsumerManager.class);
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(mgr, new NullAxFetchListFactory());
        VerificationResult vr = Mockito.mock(VerificationResult.class);
        DiscoveryInformation di = Mockito.mock(DiscoveryInformation.class);
        Mockito.when(mgr.verify(ArgumentMatchers.any(), ArgumentMatchers.any(ParameterList.class), ArgumentMatchers.any(DiscoveryInformation.class))).thenReturn(vr);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(DiscoveryInformation.class.getName(), di);
        OpenIDAuthenticationToken auth = consumer.endConsumption(request);
        assertThat(auth.getStatus()).isEqualTo(FAILURE);
    }

    @Test
    public void verificationExceptionsRaiseOpenIDException() throws Exception {
        ConsumerManager mgr = Mockito.mock(ConsumerManager.class);
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(mgr, new NullAxFetchListFactory());
        Mockito.when(mgr.verify(ArgumentMatchers.any(), ArgumentMatchers.any(ParameterList.class), ArgumentMatchers.any(DiscoveryInformation.class))).thenThrow(new MessageException("")).thenThrow(new AssociationException("")).thenThrow(new DiscoveryException(""));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("x=5");
        try {
            consumer.endConsumption(request);
            fail("OpenIDConsumerException was not thrown");
        } catch (OpenIDConsumerException expected) {
        }
        try {
            consumer.endConsumption(request);
            fail("OpenIDConsumerException was not thrown");
        } catch (OpenIDConsumerException expected) {
        }
        try {
            consumer.endConsumption(request);
            fail("OpenIDConsumerException was not thrown");
        } catch (OpenIDConsumerException expected) {
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void successfulVerificationReturnsExpectedAuthentication() throws Exception {
        ConsumerManager mgr = Mockito.mock(ConsumerManager.class);
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(mgr, new NullAxFetchListFactory());
        VerificationResult vr = Mockito.mock(VerificationResult.class);
        DiscoveryInformation di = Mockito.mock(DiscoveryInformation.class);
        Identifier id = new Identifier() {
            public String getIdentifier() {
                return "id";
            }
        };
        Message msg = Mockito.mock(Message.class);
        Mockito.when(mgr.verify(ArgumentMatchers.any(), ArgumentMatchers.any(ParameterList.class), ArgumentMatchers.any(DiscoveryInformation.class))).thenReturn(vr);
        Mockito.when(vr.getVerifiedId()).thenReturn(id);
        Mockito.when(vr.getAuthResponse()).thenReturn(msg);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(DiscoveryInformation.class.getName(), di);
        request.getSession().setAttribute("SPRING_SECURITY_OPEN_ID_ATTRIBUTES_FETCH_LIST", attributes);
        OpenIDAuthenticationToken auth = consumer.endConsumption(request);
        assertThat(auth.getStatus()).isEqualTo(SUCCESS);
    }

    @Test
    public void fetchAttributesReturnsExpectedValues() throws Exception {
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(new NullAxFetchListFactory());
        Message msg = Mockito.mock(Message.class);
        FetchResponse fr = Mockito.mock(FetchResponse.class);
        Mockito.when(msg.hasExtension(OPENID_NS_AX)).thenReturn(true);
        Mockito.when(msg.getExtension(OPENID_NS_AX)).thenReturn(fr);
        Mockito.when(fr.getAttributeValues("a")).thenReturn(Arrays.asList("x", "y"));
        List<OpenIDAttribute> fetched = consumer.fetchAxAttributes(msg, attributes);
        assertThat(fetched).hasSize(1);
        assertThat(fetched.get(0).getValues()).hasSize(2);
    }

    @Test(expected = OpenIDConsumerException.class)
    public void messageExceptionFetchingAttributesRaisesOpenIDException() throws Exception {
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(new NullAxFetchListFactory());
        Message msg = Mockito.mock(Message.class);
        FetchResponse fr = Mockito.mock(FetchResponse.class);
        Mockito.when(msg.hasExtension(OPENID_NS_AX)).thenReturn(true);
        Mockito.when(msg.getExtension(OPENID_NS_AX)).thenThrow(new MessageException(""));
        Mockito.when(fr.getAttributeValues("a")).thenReturn(Arrays.asList("x", "y"));
        consumer.fetchAxAttributes(msg, attributes);
    }

    @Test(expected = OpenIDConsumerException.class)
    public void missingDiscoveryInformationThrowsException() throws Exception {
        OpenID4JavaConsumer consumer = new OpenID4JavaConsumer(new NullAxFetchListFactory());
        consumer.endConsumption(new MockHttpServletRequest());
    }

    @Test
    public void additionalConstructorsWork() throws Exception {
        new OpenID4JavaConsumer();
        new OpenID4JavaConsumer(new OpenID4JavaConsumerTests.MockAttributesFactory());
    }

    private class MockAttributesFactory implements AxFetchListFactory {
        public List<OpenIDAttribute> createAttributeList(String identifier) {
            return attributes;
        }
    }
}

