/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.codec;


import feign.codec.ErrorDecoder.RetryAfterDecoder;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;

import static RetryAfterDecoder.RFC822_FORMAT;


public class RetryAfterDecoderTest {
    private RetryAfterDecoder decoder = new RetryAfterDecoder(RFC822_FORMAT) {
        protected long currentTimeMillis() {
            try {
                return RetryAfterDecoder.RFC822_FORMAT.parse("Sat, 1 Jan 2000 00:00:00 GMT").getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Test
    public void malformDateFailsGracefully() {
        Assert.assertFalse(((decoder.apply("Fri, 31 Dec 1999 23:59:59 ZBW")) != null));
    }

    @Test
    public void rfc822Parses() throws ParseException {
        Assert.assertEquals(RetryAfterDecoder.RFC822_FORMAT.parse("Fri, 31 Dec 1999 23:59:59 GMT"), decoder.apply("Fri, 31 Dec 1999 23:59:59 GMT"));
    }

    @Test
    public void relativeSecondsParses() throws ParseException {
        Assert.assertEquals(RetryAfterDecoder.RFC822_FORMAT.parse("Sun, 2 Jan 2000 00:00:00 GMT"), decoder.apply("86400"));
    }
}

