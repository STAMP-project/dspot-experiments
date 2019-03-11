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
package io.confluent.ksql.serde.delimited;


import io.confluent.ksql.GenericRow;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class KsqlDelimitedSerializerTest {
    private Schema orderSchema;

    @Test
    public void shouldSerializeRowCorrectly() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0);
        final GenericRow genericRow = new GenericRow(columns);
        final KsqlDelimitedSerializer ksqlDelimitedSerializer = new KsqlDelimitedSerializer(orderSchema);
        final byte[] bytes = ksqlDelimitedSerializer.serialize("t1", genericRow);
        final String delimitedString = new String(bytes, StandardCharsets.UTF_8);
        Assert.assertThat("Incorrect serialization.", delimitedString, CoreMatchers.equalTo("1511897796092,1,item_1,10.0"));
    }

    @Test
    public void shouldSerializeRowWithNull() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", null);
        final GenericRow genericRow = new GenericRow(columns);
        final KsqlDelimitedSerializer ksqlDelimitedSerializer = new KsqlDelimitedSerializer(orderSchema);
        final byte[] bytes = ksqlDelimitedSerializer.serialize("t1", genericRow);
        final String delimitedString = new String(bytes, StandardCharsets.UTF_8);
        Assert.assertThat("Incorrect serialization.", delimitedString, CoreMatchers.equalTo("1511897796092,1,item_1,"));
    }
}

