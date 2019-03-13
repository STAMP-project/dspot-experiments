/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.client.result;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link SMSParsedResult}.
 *
 * @author Sean Owen
 */
public final class SMSMMSParsedResultTestCase extends Assert {
    @Test
    public void testSMS() {
        SMSMMSParsedResultTestCase.doTest("sms:+15551212", "+15551212", null, null, null, "sms:+15551212");
        SMSMMSParsedResultTestCase.doTest("sms:+15551212?subject=foo&body=bar", "+15551212", "foo", "bar", null, "sms:+15551212?body=bar&subject=foo");
        SMSMMSParsedResultTestCase.doTest("sms:+15551212;via=999333", "+15551212", null, null, "999333", "sms:+15551212;via=999333");
    }

    @Test
    public void testMMS() {
        SMSMMSParsedResultTestCase.doTest("mms:+15551212", "+15551212", null, null, null, "sms:+15551212");
        SMSMMSParsedResultTestCase.doTest("mms:+15551212?subject=foo&body=bar", "+15551212", "foo", "bar", null, "sms:+15551212?body=bar&subject=foo");
        SMSMMSParsedResultTestCase.doTest("mms:+15551212;via=999333", "+15551212", null, null, "999333", "sms:+15551212;via=999333");
    }
}

