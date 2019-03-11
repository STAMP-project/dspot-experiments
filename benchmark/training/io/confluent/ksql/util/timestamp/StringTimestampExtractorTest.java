/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.util.timestamp;


import io.confluent.ksql.GenericRow;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class StringTimestampExtractorTest {
    private static final String format = "yyyy-MMM-dd";

    @SuppressWarnings("unchecked")
    @Test
    public void shouldExtractTimestampFromStringWithFormat() throws ParseException {
        final StringTimestampExtractor timestampExtractor = new StringTimestampExtractor(StringTimestampExtractorTest.format, 0);
        final String stringTime = "2010-Jan-11";
        final long expectedTime = new SimpleDateFormat(StringTimestampExtractorTest.format).parse(stringTime).getTime();
        final long actualTime = timestampExtractor.extract(new org.apache.kafka.clients.consumer.ConsumerRecord("topic", 1, 1, null, new GenericRow(Collections.singletonList(stringTime))), 1);
        MatcherAssert.assertThat(actualTime, CoreMatchers.equalTo(expectedTime));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfColumnIndexIsNegative() {
        new StringTimestampExtractor(StringTimestampExtractorTest.format, (-1));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullFormat() {
        new StringTimestampExtractor(null, (-1));
    }
}

