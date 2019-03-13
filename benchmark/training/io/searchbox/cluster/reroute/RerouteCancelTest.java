package io.searchbox.cluster.reroute;


import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class RerouteCancelTest {
    @Test
    public void allowPrimaryTrue() throws JSONException {
        RerouteCancel rerouteCancel = new RerouteCancel("index1", 1, "node1", true);
        Assert.assertEquals(rerouteCancel.getType(), "cancel");
        String actualJson = new Gson().toJson(rerouteCancel.getData());
        String expectedJson = "{\"index\":\"index1\", \"shard\": 1, \"node\": \"node1\", \"allow_primary\": true}";
        JSONAssert.assertEquals(actualJson, expectedJson, false);
    }

    @Test
    public void allowPrimaryFalse() throws JSONException {
        RerouteCancel rerouteCancel = new RerouteCancel("index1", 1, "node1", false);
        Assert.assertEquals(rerouteCancel.getType(), "cancel");
        String actualJson = new Gson().toJson(rerouteCancel.getData());
        String expectedJson = "{\"index\":\"index1\", \"shard\": 1, \"node\": \"node1\", \"allow_primary\": false}";
        JSONAssert.assertEquals(actualJson, expectedJson, false);
    }
}

