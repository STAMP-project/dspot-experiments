package com.orientechnologies.orient.test.server.network.http;


import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import static com.orientechnologies.orient.test.server.network.http.BaseHttpTest.CONTENT.JSON;


/**
 * Test HTTP "index" command.
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com) (l.garulli--at-orientdb.com)
 */
public class HttpIndexTest extends BaseHttpDatabaseTest {
    @Test
    public void create() throws IOException {
        put((("index/" + (getDatabaseName())) + "/ManualIndex/luca")).payload("{@class:'V', name:'Harry', surname:'Potter',age:18}", JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 201);
    }

    @Test
    public void retrieve() throws IOException {
        get((("index/" + (getDatabaseName())) + "/ManualIndex/jay")).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 200);
        String response = EntityUtils.toString(getResponse().getEntity());
        Assert.assertEquals(response.charAt(0), '[');
        Assert.assertEquals(response.charAt(((response.length()) - 1)), ']');
        response = response.substring(1, ((response.length()) - 1));
        final ODocument jay = new ODocument().fromJSON(response);
        Assert.assertEquals(jay.field("name"), "Jay");
        Assert.assertEquals(jay.field("surname"), "Miner");
        Assert.assertEquals(jay.<Object>field("age"), 99);
        Assert.assertEquals(jay.getVersion(), 1);
    }

    @Test
    public void retrieveNonExistent() throws IOException {
        get((("index/" + (getDatabaseName())) + "/ManualIndex/NonExistent")).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 404);
    }

    @Test
    public void updateKey() throws IOException {
        put((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).payload("{@class:'V', name:'Harry', surname:'Potter',age:18}", JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 201);
        put((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).payload("{@class:'V', name:'Harry2', surname:'Potter2',age:182}", JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 201);
        get((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 200);
        String response = EntityUtils.toString(getResponse().getEntity());
        Assert.assertEquals(response.charAt(0), '[');
        Assert.assertEquals(response.charAt(((response.length()) - 1)), ']');
        response = response.substring(1, ((response.length()) - 1));
        final ODocument jay = new ODocument().fromJSON(response);
        Assert.assertEquals(jay.field("name"), "Harry2");
        Assert.assertEquals(jay.field("surname"), "Potter2");
        Assert.assertEquals(jay.<Object>field("age"), 182);
        Assert.assertEquals(jay.getVersion(), 1);
    }

    @Test
    public void updateValue() throws IOException {
        put((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).payload("{@class:'V', name:'Harry', surname:'Potter',age:18}", JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 201);
        get((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 200);
        String response = EntityUtils.toString(getResponse().getEntity());
        Assert.assertEquals(response.charAt(0), '[');
        Assert.assertEquals(response.charAt(((response.length()) - 1)), ']');
        response = response.substring(1, ((response.length()) - 1));
        ODocument harry = new ODocument().fromJSON(response);
        put((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).payload((((("{name:'Harry3', surname:'Potter3',age:183,@rid:'" + (harry.getIdentity())) + "',@version:") + (harry.getVersion())) + "}"), JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 204);
        get((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 200);
        response = EntityUtils.toString(getResponse().getEntity());
        Assert.assertEquals(response.charAt(0), '[');
        Assert.assertEquals(response.charAt(((response.length()) - 1)), ']');
        response = response.substring(1, ((response.length()) - 1));
        harry = new ODocument().fromJSON(response);
        Assert.assertEquals(harry.field("name"), "Harry3");
        Assert.assertEquals(harry.field("surname"), "Potter3");
        Assert.assertEquals(harry.<Object>field("age"), 183);
        Assert.assertEquals(harry.getVersion(), 2);
    }

    @Test
    public void updateValueMVCCError() throws IOException {
        put((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).payload("{@class:'V', name:'Harry', surname:'Potter',age:18}", JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 201);
        get((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 200);
        String response = EntityUtils.toString(getResponse().getEntity());
        Assert.assertEquals(response.charAt(0), '[');
        Assert.assertEquals(response.charAt(((response.length()) - 1)), ']');
        response = response.substring(1, ((response.length()) - 1));
        ODocument harry = new ODocument().fromJSON(response);
        put((("index/" + (getDatabaseName())) + "/ManualIndex/Harry2")).payload((("{name:'Harry3', surname:'Potter3',age:183,@rid:'" + (harry.getIdentity())) + "'}"), JSON).exec();
        Assert.assertEquals(getResponse().getStatusLine().getStatusCode(), 409);
    }
}

