package com.orientechnologies.orient.test.server.network.http;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import static com.orientechnologies.orient.test.server.network.http.BaseHttpTest.CONTENT.TEXT;


/**
 * Created by tglman on 16/03/16.
 */
public class HttpImportTest extends BaseHttpDatabaseTest {
    @Test
    public void testImport() throws IOException {
        String content = "{\"records\": [{\"@type\": \"d\", \"@rid\": \"#9:0\",\"@version\": 1,\"@class\": \"V\"}]}";
        post((("import/" + (getDatabaseName())) + "?merge=true")).payload(content, TEXT);
        HttpResponse response = getResponse();
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        InputStream is = response.getEntity().getContent();
        List<String> out = new LinkedList<>();
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = r.readLine()) != null) {
                out.add(line);
            } 
            System.out.println(out);
        } catch (IOException var5) {
            throw new IllegalArgumentException(("Problems reading from: " + is), var5);
        }
    }
}

