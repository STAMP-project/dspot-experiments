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
package io.confluent.ksql.serde.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.GenericRow;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class KsqlJsonSerializerTest {
    private Schema orderSchema;

    private Schema addressSchema;

    private Schema itemSchema;

    private Schema categorySchema;

    @Test
    public void shouldSerializeRowCorrectly() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0, Arrays.asList(100.0), Collections.singletonMap("key1", 100.0));
        final GenericRow genericRow = new GenericRow(columns);
        final KsqlJsonSerializer ksqlJsonDeserializer = new KsqlJsonSerializer(orderSchema);
        final byte[] bytes = ksqlJsonDeserializer.serialize("t1", genericRow);
        final String jsonString = new String(bytes, StandardCharsets.UTF_8);
        Assert.assertThat("Incorrect serialization.", jsonString, CoreMatchers.equalTo("{\"ORDERTIME\":1511897796092,\"ORDERID\":1,\"ITEMID\":\"item_1\",\"ORDERUNITS\":10.0,\"ARRAYCOL\":[100.0],\"MAPCOL\":{\"key1\":100.0}}"));
    }

    @Test
    public void shouldSerializeRowWithNull() {
        final List columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0, null, null);
        final GenericRow genericRow = new GenericRow(columns);
        final KsqlJsonSerializer ksqlJsonDeserializer = new KsqlJsonSerializer(orderSchema);
        final byte[] bytes = ksqlJsonDeserializer.serialize("t1", genericRow);
        final String jsonString = new String(bytes, StandardCharsets.UTF_8);
        Assert.assertThat("Incorrect serialization.", jsonString, CoreMatchers.equalTo("{\"ORDERTIME\":1511897796092,\"ORDERID\":1,\"ITEMID\":\"item_1\",\"ORDERUNITS\":10.0,\"ARRAYCOL\":null,\"MAPCOL\":null}"));
    }

    @Test
    public void shouldHandleStruct() throws IOException {
        final KsqlJsonSerializer jsonSerializer = new KsqlJsonSerializer(getSchemaWithStruct());
        final GenericRow genericRow = getGenericRow();
        final byte[] bytes = jsonSerializer.serialize("", genericRow);
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(bytes);
        Assert.assertThat(jsonNode.size(), CoreMatchers.equalTo(7));
        Assert.assertThat(jsonNode.get("ordertime").asLong(), CoreMatchers.equalTo(genericRow.getColumns().get(0)));
        Assert.assertThat(jsonNode.get("itemid").get("NAME").asText(), CoreMatchers.equalTo("Item_10"));
    }
}

