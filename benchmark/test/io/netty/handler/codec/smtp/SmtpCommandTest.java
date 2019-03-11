/**
 * Copyright 2017 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.smtp;


import SmtpCommand.DATA;
import SmtpCommand.EHLO;
import SmtpCommand.HELO;
import SmtpCommand.HELP;
import SmtpCommand.MAIL;
import org.junit.Assert;
import org.junit.Test;


public class SmtpCommandTest {
    @Test
    public void getCommandFromCache() {
        Assert.assertSame(DATA, SmtpCommand.valueOf("DATA"));
        Assert.assertSame(EHLO, SmtpCommand.valueOf("EHLO"));
        Assert.assertNotSame(EHLO, SmtpCommand.valueOf("ehlo"));
    }

    @Test
    public void equalsIgnoreCase() {
        Assert.assertEquals(MAIL, SmtpCommand.valueOf("mail"));
        Assert.assertEquals(SmtpCommand.valueOf("test"), SmtpCommand.valueOf("TEST"));
    }

    @Test
    public void isContentExpected() {
        Assert.assertTrue(SmtpCommand.valueOf("DATA").isContentExpected());
        Assert.assertTrue(SmtpCommand.valueOf("data").isContentExpected());
        Assert.assertFalse(HELO.isContentExpected());
        Assert.assertFalse(HELP.isContentExpected());
        Assert.assertFalse(SmtpCommand.valueOf("DATA2").isContentExpected());
    }
}

