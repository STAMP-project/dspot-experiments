/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.protocols.postgres.types;


import TimestampType.INSTANCE;
import com.carrotsearch.randomizedtesting.annotations.Name;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.hamcrest.Matchers;
import org.junit.Test;


public class TimestampTypeStringDecodeTest extends BasePGTypeTest<Long> {
    private final int numberOfFractionDigits;

    private final int timezoneDiffInHours;

    private final String era;

    public TimestampTypeStringDecodeTest(@Name("numberOfFractionDigits")
    int numberOfFractionDigits, @Name("era")
    String era, @Name("timezoneDiffInHours")
    int timezoneDiffInHours) {
        super(INSTANCE);
        this.numberOfFractionDigits = numberOfFractionDigits;
        this.era = era;
        this.timezoneDiffInHours = timezoneDiffInHours;
    }

    @Test
    public void testDecodeEncodeUTF8Text() {
        long expectedMsecs = 1514764800000L;
        String prefix = "2018-01-01 00:00:00";
        String tzString = String.format(Locale.ENGLISH, "%+03d:00", timezoneDiffInHours);
        StringBuilder fullTimestamp = new StringBuilder(prefix);
        appendFractionOfSecDigits(fullTimestamp);
        fullTimestamp.append(tzString);
        fullTimestamp.append(era);
        // Calculate expected result because of fraction of second digits
        long msecs = 0;
        if (((numberOfFractionDigits) > 0) && ((numberOfFractionDigits) <= 3)) {
            msecs = ((long) (Math.pow(10, (3 - (numberOfFractionDigits)))));
        }
        long tzMsecs = (((timezoneDiffInHours) * 60) * 60) * 1000;
        if (era.equals(" BC")) {
            expectedMsecs *= -1;
            expectedMsecs -= 124302816000000L;
        }
        expectedMsecs += msecs;
        expectedMsecs -= tzMsecs;
        assertThat(INSTANCE.decodeUTF8Text(fullTimestamp.toString().getBytes(StandardCharsets.UTF_8)), Matchers.is(expectedMsecs));
        // Check that the "round-trip" also works.
        // We cannot assert against the fullTimestamp since the decoding truncates <= 1msec fraction digits
        // and also the timezone originally passed is calculated and the encodeAsUTF8Text always sends as UTC.
        assertThat(INSTANCE.decodeUTF8Text(INSTANCE.encodeAsUTF8Text(expectedMsecs)), Matchers.is(expectedMsecs));
    }
}

