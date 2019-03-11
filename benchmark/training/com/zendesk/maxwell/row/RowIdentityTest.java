package com.zendesk.maxwell.row;


import RowMap.KeyFormat.ARRAY;
import RowMap.KeyFormat.HASH;
import com.google.common.collect.Lists;
import com.zendesk.maxwell.MaxwellTestJSON;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;


public class RowIdentityTest {
    @Test
    public void testToJson() throws IOException {
        RowIdentity rowId = new RowIdentity("MyDatabase", "MyTable", Arrays.asList(Pair.of("id", 111), Pair.of("account", 123)));
        String jsonHash = rowId.toKeyJson(HASH);
        Assert.assertEquals("{\"database\":\"MyDatabase\",\"table\":\"MyTable\",\"pk.id\":111,\"pk.account\":123}", jsonHash);
        String jsonArray = rowId.toKeyJson(ARRAY);
        Assert.assertEquals("[\"MyDatabase\",\"MyTable\",[{\"id\":111},{\"account\":123}]]", jsonArray);
    }

    @Test
    public void testToFallbackValueWithReason() throws IOException {
        RowIdentity rowId = new RowIdentity("MyDatabase", "MyTable", Collections.singletonList(Pair.of("id", 111)));
        String jsonString = rowId.toFallbackValueWithReason("too big");
        Assert.assertEquals("{\"database\":\"MyDatabase\",\"table\":\"MyTable\",\"reason\":\"too big\",\"data\":{\"id\":111}}", jsonString);
    }

    @Test
    public void testPkToJsonArrayWithListData() throws Exception {
        RowIdentity rowId = new RowIdentity("MyDatabase", "MyTable", Arrays.asList(Pair.of("id", "9001"), Pair.of("name", Lists.newArrayList("example"))));
        String jsonString = rowId.toKeyJson(ARRAY);
        Assert.assertEquals("[\"MyDatabase\",\"MyTable\",[{\"id\":\"9001\"},{\"name\":[\"example\"]}]]", jsonString);
    }

    @Test
    public void testPkToJsonHashWithEmptyData() throws Exception {
        RowIdentity rowId = new RowIdentity("MyDatabase", "MyTable", Arrays.asList());
        String jsonString = rowId.toKeyJson(HASH);
        Map<String, Object> jsonMap = MaxwellTestJSON.parseJSON(jsonString);
        Assert.assertTrue(jsonMap.containsKey("_uuid"));
        Assert.assertEquals("MyDatabase", jsonMap.get("database"));
        Assert.assertEquals("MyTable", jsonMap.get("table"));
    }
}

