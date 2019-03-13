package org.jboss.as.test.integration.jsf.duplicateid;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.jsf.duplicateid.deployment.IncludeBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test case based on reproducer for https://issues.jboss.org/browse/JBEAP-10758
 *
 * Original reproducer: https://github.com/tuner/mojarra-dynamic-include-reproducer
 * Original reproducer author: Kari Lavikka <tuner@bdb.fi>
 *
 * @author Jan Kasik <jkasik@redhat.com>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DuplicateIDIntegrationTestCase {
    private static final Logger log = LoggerFactory.getLogger(DuplicateIDIntegrationTestCase.class);

    @ContainerResource
    private ManagementClient managementClient;

    private static final String HTTP = "http";

    private static final String APP_NAME = "duplicate-id-reproducer";

    private static final String WEB_XML = "web.xml";

    private static final String INDEX_XHTML = "index.xhtml";

    private static final String BUTTON_XHTML = "button.xhtml";

    private static final String COMP_XHTML = "comp.xhtml";

    private static final String FACES_CONFIG_XML = "faces-config.xml";

    private static final String COMPONENTS = "components";

    private static final String RESOURCES = "resources";

    private static final int APPLICATION_PORT = 8080;

    /**
     * Verify, that loading two dynamically loaded elements won't cause a server error.
     * <ol>
     *     <li>Send an initial request to obtain session id and view state.</li>
     *     <li>Verify this request went OK.</li>
     *     <li>Simulate clicking on a button labeled "Show 2" to "reveal" a text element which has assigned ID.</li>
     *     <li>Verify that second request went OK too.</li>
     *     <li>Simulate clicking on a button labeled "Show 3" to "reveal" a text element which has assigned ID. In state
     *     when the bug is not fixed, new element receives same ID and response code is 500 - server error. Thus
     *     verifying, that "clicking" went OK is crucial.</li>
     *     <li>Also verify, that respond contains both displayed dynamic elements.</li>
     * </ol>
     */
    @Test
    public void testDuplicateIDIsNotGenerated() throws IOException {
        final URL baseURL = new URL(DuplicateIDIntegrationTestCase.HTTP, managementClient.getMgmtAddress(), DuplicateIDIntegrationTestCase.APPLICATION_PORT, ((((("/" + (DuplicateIDIntegrationTestCase.APP_NAME)) + "/") + (IncludeBean.class.getPackage().getName().replaceAll("\\.", "/"))) + "/") + (DuplicateIDIntegrationTestCase.INDEX_XHTML)));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            DuplicateIDIntegrationTestCase.log.debug("Sending initial request to '{}'.", baseURL.toString());
            final HttpResponse initialResponse = httpClient.execute(new HttpPost(baseURL.toString()));
            Assert.assertEquals(HttpURLConnection.HTTP_OK, initialResponse.getStatusLine().getStatusCode());
            final DuplicateIDIntegrationTestCase.Form initialResponseForm = new DuplicateIDIntegrationTestCase.Form(initialResponse);
            final HttpResponse afterFirstClickResponse = simulateClickingOnButton(httpClient, initialResponseForm, "Show 3");
            Assert.assertEquals(HttpURLConnection.HTTP_OK, afterFirstClickResponse.getStatusLine().getStatusCode());
            final DuplicateIDIntegrationTestCase.Form formAfterFirstClick = new DuplicateIDIntegrationTestCase.Form(afterFirstClickResponse);
            final HttpResponse afterSecondClickResponse = simulateClickingOnButton(httpClient, formAfterFirstClick, "Show 2");
            Assert.assertEquals(HttpURLConnection.HTTP_OK, afterSecondClickResponse.getStatusLine().getStatusCode());
            final String responseString = new BasicResponseHandler().handleResponse(afterSecondClickResponse);
            Assert.assertTrue("There should be text which appears after clicking on second button in response!", responseString.contains(">OutputText 2</span>"));
            Assert.assertTrue("There should be text which appears after clicking on first button in response!", responseString.contains(">OutputText 3</span>"));
        }
    }

    private final class Form {
        static final String NAME = "name";

        static final String VALUE = "value";

        static final String INPUT = "input";

        static final String TYPE = "type";

        static final String ACTION = "action";

        static final String FORM = "form";

        final HttpResponse response;

        final String action;

        final List<DuplicateIDIntegrationTestCase.Input> inputFields = new LinkedList<>();

        public Form(HttpResponse response) throws IOException {
            this.response = response;
            DuplicateIDIntegrationTestCase.log.debug(response.getStatusLine().toString());
            final String responseString = new BasicResponseHandler().handleResponse(response);
            final Document doc = Jsoup.parse(responseString);
            final Element form = doc.select(DuplicateIDIntegrationTestCase.Form.FORM).first();
            this.action = form.attr(DuplicateIDIntegrationTestCase.Form.ACTION);
            for (Element input : form.select(DuplicateIDIntegrationTestCase.Form.INPUT)) {
                DuplicateIDIntegrationTestCase.Input.Type type = null;
                switch (input.attr(DuplicateIDIntegrationTestCase.Form.TYPE)) {
                    case "submit" :
                        type = DuplicateIDIntegrationTestCase.Input.Type.SUBMIT;
                        break;
                    case "hidden" :
                        type = DuplicateIDIntegrationTestCase.Input.Type.HIDDEN;
                        break;
                }
                inputFields.add(new DuplicateIDIntegrationTestCase.Input(input.attr(DuplicateIDIntegrationTestCase.Form.NAME), input.attr(DuplicateIDIntegrationTestCase.Form.VALUE), type));
            }
        }

        public String getAction() {
            return action;
        }

        public List<DuplicateIDIntegrationTestCase.Input> getInputFields() {
            return inputFields;
        }
    }

    private static final class Input {
        final String name;

        final String value;

        final DuplicateIDIntegrationTestCase.Input.Type type;

        public Input(String name, String value, DuplicateIDIntegrationTestCase.Input.Type type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public enum Type {

            HIDDEN,
            SUBMIT;}
    }
}

