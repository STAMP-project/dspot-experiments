package org.javaee7.jaxrs.server.negotiation;


import JSONCompareMode.STRICT;
import java.io.IOException;
import java.net.URL;
import javax.ws.rs.client.WebTarget;
import org.custommonkey.xmlunit.XMLAssert;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xml.sax.SAXException;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    @ArquillianResource
    private URL base;

    private WebTarget target;

    @Test
    public void testJson() throws JSONException {
        String response = target.request().accept("application/*").get(String.class);
        JSONAssert.assertEquals("[{\"name\":\"Penny\",\"age\":1},{\"name\":\"Leonard\",\"age\":2},{\"name\":\"Sheldon\",\"age\":3}]", response, STRICT);
    }

    @Test
    public void testJson2() throws JSONException {
        String response = target.request().get(String.class);
        JSONAssert.assertEquals("[{\"name\":\"Penny\",\"age\":1},{\"name\":\"Leonard\",\"age\":2},{\"name\":\"Sheldon\",\"age\":3}]", response, STRICT);
    }

    @Test
    public void testXml() throws IOException, JSONException, SAXException {
        String response = target.request().accept("application/xml").get(String.class);
        XMLAssert.assertXMLEqual("<people><person><age>1</age><name>Penny</name></person><person><age>2</age><name>Leonard</name></person><person><age>3</age><name>Sheldon</name></person></people>", response);
    }
}

