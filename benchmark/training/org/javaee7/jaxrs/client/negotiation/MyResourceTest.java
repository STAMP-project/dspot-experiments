package org.javaee7.jaxrs.client.negotiation;


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
    private WebTarget target;

    @ArquillianResource
    private URL base;

    @Test
    public void testXML() throws IOException, SAXException {
        String xml = target.request("application/xml").get(String.class);
        System.out.println(xml);
        XMLAssert.assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><people><person><age>1</age><name>Penny</name></person><person><age>2</age><name>Leonard</name></person><person><age>3</age><name>Sheldon</name></person></people>", xml);
    }

    @Test
    public void testJSON() throws JSONException {
        String json = target.request("application/json").get(String.class);
        JSONAssert.assertEquals("[{\"age\":1,\"name\":\"Penny\"},{\"age\":2,\"name\":\"Leonard\"},{\"age\":3,\"name\":\"Sheldon\"}]", json, false);
    }
}

