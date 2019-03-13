/**
 * Copyright 2009, Mahmood Ali.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following disclaimer
 *      in the documentation and/or other materials provided with the
 *      distribution.
 *    * Neither the name of Mahmood Ali. nor the names of its
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.notnoop.apns.integration;


import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.internal.ApnsFeedbackParsingUtils;
import com.notnoop.apns.utils.ApnsServerStub;
import com.notnoop.apns.utils.FixedCertificates;
import java.io.IOException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLContext;
import org.junit.Assert;
import org.junit.Test;


public class FeedbackTest {
    ApnsServerStub server;

    SSLContext clientContext = FixedCertificates.clientContext();

    @Test
    public void simpleFeedback() throws IOException {
        server.getToSend().write(ApnsFeedbackParsingUtils.simple);
        ApnsService service = APNS.newService().withSSLContext(clientContext).withGatewayDestination(FixedCertificates.LOCALHOST, server.getEffectiveGatewayPort()).withFeedbackDestination(FixedCertificates.LOCALHOST, server.getEffectiveFeedbackPort()).build();
        ApnsFeedbackParsingUtils.checkParsedSimple(service.getInactiveDevices());
    }

    @Test
    public void simpleFeedbackWithoutTimeout() throws IOException {
        server.getToSend().write(ApnsFeedbackParsingUtils.simple);
        server.getToWaitBeforeSend().set(2000);
        ApnsService service = APNS.newService().withSSLContext(clientContext).withGatewayDestination(FixedCertificates.LOCALHOST, server.getEffectiveGatewayPort()).withFeedbackDestination(FixedCertificates.LOCALHOST, server.getEffectiveFeedbackPort()).withReadTimeout(3000).build();
        ApnsFeedbackParsingUtils.checkParsedSimple(service.getInactiveDevices());
    }

    @Test
    public void simpleFeedbackWithTimeout() throws IOException {
        server.getToSend().write(ApnsFeedbackParsingUtils.simple);
        server.getToWaitBeforeSend().set(5000);
        ApnsService service = APNS.newService().withSSLContext(clientContext).withGatewayDestination(FixedCertificates.LOCALHOST, server.getEffectiveGatewayPort()).withFeedbackDestination(FixedCertificates.LOCALHOST, server.getEffectiveFeedbackPort()).withReadTimeout(1000).build();
        try {
            service.getInactiveDevices();
            Assert.fail("RuntimeException expected");
        } catch (RuntimeException e) {
            Assert.assertEquals("Socket timeout exception expected", SocketTimeoutException.class, e.getCause().getClass());
        }
    }

    @Test
    public void threeFeedback() throws IOException {
        server.getToSend().write(ApnsFeedbackParsingUtils.three);
        ApnsService service = APNS.newService().withSSLContext(clientContext).withGatewayDestination(FixedCertificates.LOCALHOST, server.getEffectiveGatewayPort()).withFeedbackDestination(FixedCertificates.LOCALHOST, server.getEffectiveFeedbackPort()).build();
        ApnsFeedbackParsingUtils.checkParsedThree(service.getInactiveDevices());
    }

    @Test
    public void simpleQueuedFeedback() throws IOException {
        server.getToSend().write(ApnsFeedbackParsingUtils.simple);
        ApnsService service = APNS.newService().withSSLContext(clientContext).withGatewayDestination(FixedCertificates.LOCALHOST, server.getEffectiveGatewayPort()).withFeedbackDestination(FixedCertificates.LOCALHOST, server.getEffectiveFeedbackPort()).asQueued().build();
        ApnsFeedbackParsingUtils.checkParsedSimple(service.getInactiveDevices());
    }

    @Test
    public void threeQueuedFeedback() throws IOException {
        server.getToSend().write(ApnsFeedbackParsingUtils.three);
        ApnsService service = APNS.newService().withSSLContext(clientContext).withGatewayDestination(FixedCertificates.LOCALHOST, server.getEffectiveGatewayPort()).withFeedbackDestination(FixedCertificates.LOCALHOST, server.getEffectiveFeedbackPort()).asQueued().build();
        ApnsFeedbackParsingUtils.checkParsedThree(service.getInactiveDevices());
    }
}

