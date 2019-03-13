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


import com.notnoop.apns.ApnsService;
import com.notnoop.apns.DeliveryError;
import com.notnoop.apns.EnhancedApnsNotification;
import com.notnoop.apns.utils.Simulator.ApnsResponse;
import com.notnoop.apns.utils.Simulator.ApnsSimulatorWithVerification;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ApnsConnectionResendTest {
    private static EnhancedApnsNotification NOTIFICATION_0 = ApnsConnectionResendTest.buildNotification(0);

    private static EnhancedApnsNotification NOTIFICATION_1 = ApnsConnectionResendTest.buildNotification(1);

    private static EnhancedApnsNotification NOTIFICATION_2 = ApnsConnectionResendTest.buildNotification(2);

    private static ApnsSimulatorWithVerification apnsSim;

    private ApnsDelegateRecorder delegateRecorder;

    private ApnsService testee;

    /* Test when we submit 3 messages to APNS 0, 1, 2.  0 is an error but we don't see the error response back until
    1,2 have already been submitted.  Then at this point the network connection to APNS cannot be made, so that
    when retrying the submissions we have to notify the client that delivery failed for 1 and 2.
     */
    @Test
    public void testGivenFailedSubmissionDueToErrorThenApnsDownWithNotificationsInBufferEnsureClientNotified() throws Exception {
        final DeliveryError deliveryError = DeliveryError.INVALID_PAYLOAD_SIZE;
        ApnsConnectionResendTest.apnsSim.when(ApnsConnectionResendTest.NOTIFICATION_0).thenDoNothing();
        ApnsConnectionResendTest.apnsSim.when(ApnsConnectionResendTest.NOTIFICATION_1).thenDoNothing();
        ApnsConnectionResendTest.apnsSim.when(ApnsConnectionResendTest.NOTIFICATION_2).thenRespond(ApnsResponse.returnErrorAndShutdown(deliveryError, ApnsConnectionResendTest.NOTIFICATION_0));
        testee.push(ApnsConnectionResendTest.NOTIFICATION_0);
        testee.push(ApnsConnectionResendTest.NOTIFICATION_1);
        testee.push(ApnsConnectionResendTest.NOTIFICATION_2);
        // Give some time for connection failure to take place
        Thread.sleep(5000);
        // Verify received expected notifications
        ApnsConnectionResendTest.apnsSim.verify();
        // verify delegate calls
        Assert.assertEquals(3, delegateRecorder.getSent().size());
        final List<ApnsDelegateRecorder.MessageSentFailedRecord> failed = delegateRecorder.getFailed();
        Assert.assertEquals(3, failed.size());
        // first is failed delivery due to payload size
        failed.get(0).assertRecord(ApnsConnectionResendTest.NOTIFICATION_0, new com.notnoop.exceptions.ApnsDeliveryErrorException(deliveryError));
        // second and third are due to not being able to connect to APNS
        assertNetworkIoExForRedelivery(ApnsConnectionResendTest.NOTIFICATION_1, failed.get(1));
        assertNetworkIoExForRedelivery(ApnsConnectionResendTest.NOTIFICATION_2, failed.get(2));
    }
}

