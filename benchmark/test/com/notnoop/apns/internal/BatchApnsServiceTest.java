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
package com.notnoop.apns.internal;


import com.notnoop.apns.ApnsNotification;
import java.io.IOException;
import org.junit.Test;
import org.mockito.Mockito;


public class BatchApnsServiceTest {
    private ApnsConnection prototype;

    private BatchApnsService service;

    private int delayTimeInSec = 2;

    private int delayTimeInSec_millis = (delayTimeInSec) * 1000;/* 2000 */


    private int delayTimeInSec1_2_millis = ((delayTimeInSec) * 1000) / 2;/* 1000 */


    private int delayTimeInSec1_4_millis = ((delayTimeInSec) * 1000) / 4;/* 500 */


    private int maxDelayTimeInSec = 2 * (delayTimeInSec);

    @Test
    public void simpleBatchWait_one() throws IOException, InterruptedException {
        // send message
        ApnsNotification message = service.push("1234", "{}");
        // make sure no message was send yet
        Mockito.verify(prototype, Mockito.times(0)).copy();
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message);
        Mockito.verify(prototype, Mockito.times(0)).close();
        Thread.sleep(((delayTimeInSec_millis) + 250));
        // verify batch sends and close the connection
        Mockito.verify(prototype, Mockito.times(1)).copy();
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message);
        Mockito.verify(prototype, Mockito.times(1)).close();
    }

    @Test
    public void simpleBatchWait_multiple() throws IOException, InterruptedException {
        // send message
        ApnsNotification message1 = service.push("1234", "{}");
        Thread.sleep(delayTimeInSec1_2_millis);
        ApnsNotification message2 = service.push("4321", "{}");
        // make sure no message was send yet
        Mockito.verify(prototype, Mockito.times(0)).copy();
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message1);
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message2);
        Mockito.verify(prototype, Mockito.times(0)).close();
        Thread.sleep(((delayTimeInSec1_4_millis) * 3));
        // still no send
        Mockito.verify(prototype, Mockito.times(0)).copy();
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message1);
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message2);
        Mockito.verify(prototype, Mockito.times(0)).close();
        Thread.sleep(((delayTimeInSec1_4_millis) + 250));
        // verify batch sends and close the connection
        Mockito.verify(prototype, Mockito.times(1)).copy();
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message1);
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message2);
        Mockito.verify(prototype, Mockito.times(1)).close();
    }

    @Test
    public void simpleBatchWait_maxDelay() throws IOException, InterruptedException {
        // send message
        ApnsNotification message1 = service.push("1234", "{}");
        Thread.sleep(((delayTimeInSec1_4_millis) * 3));
        ApnsNotification message2 = service.push("4321", "{}");
        Thread.sleep(((delayTimeInSec1_4_millis) * 3));
        ApnsNotification message3 = service.push("4321", "{}");
        Thread.sleep(((delayTimeInSec1_4_millis) * 3));
        ApnsNotification message4 = service.push("4321", "{}");
        // make sure no message was send yet
        Mockito.verify(prototype, Mockito.times(0)).copy();
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message1);
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message2);
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message3);
        Mockito.verify(prototype, Mockito.times(0)).sendMessage(message4);
        Mockito.verify(prototype, Mockito.times(0)).close();
        Thread.sleep(((delayTimeInSec1_4_millis) + 250));
        // verify batch sends and close the connection
        Mockito.verify(prototype, Mockito.times(1)).copy();
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message1);
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message2);
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message3);
        Mockito.verify(prototype, Mockito.times(1)).sendMessage(message4);
        Mockito.verify(prototype, Mockito.times(1)).close();
    }
}

