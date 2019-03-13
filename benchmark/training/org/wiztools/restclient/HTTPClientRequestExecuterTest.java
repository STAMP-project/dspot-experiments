package org.wiztools.restclient;


import HTTPMethod.GET;
import HTTPMethod.POST;
import org.junit.Assert;
import org.junit.Test;
import org.wiztools.commons.Charsets;


/**
 *
 *
 * @author subwiz
 */
public class HTTPClientRequestExecuterTest {
    public HTTPClientRequestExecuterTest() {
    }

    @Test
    public void testPremptiveAuth() throws Exception {
        System.out.println("testPreemptiveAuth");
        RequestBean req = getRequestBean();
        req.setMethod(GET);
        BasicAuthBean auth = new BasicAuthBean();
        auth.setPreemptive(true);
        auth.setUsername("subhash");
        auth.setPassword("subhash".toCharArray());
        req.setAuth(auth);
        View view = new View() {
            @Override
            public void doStart(Request request) {
                System.out.println("Starting request...");
            }

            @Override
            public void doResponse(Response response) {
                System.out.println("in doResponse()...");
                byte[] bodyByte = response.getResponseBody();
                String body = new String(bodyByte, Charsets.UTF_8);
                if (!(body.contains("Authorization: Basic c3ViaGFzaDpzdWJoYXNo"))) {
                    Assert.fail("Pre-emptive Authorization does not happen");
                }
            }

            @Override
            public void doEnd() {
            }

            @Override
            public void doError(String error) {
                System.out.println(error);
            }

            @Override
            public void doCancelled() {
            }
        };
        // Execute:
        RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
        executer.execute(req, view);
    }

    /**
     * Test of run method, of class HTTPRequestThread.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        final ContentType contentType = new ContentTypeBean("test/text", Charsets.UTF_8);
        RequestBean request = getRequestBean();
        request.setMethod(POST);
        ReqEntityStringBean rBean = new ReqEntityStringBean("", contentType);
        request.setBody(rBean);
        View view = new View() {
            public void doStart(Request request) {
                // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doResponse(Response response) {
                System.out.println(response);
                // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doEnd() {
                // throw new UnsupportedOperationException("Not supported yet.");
            }

            public void doError(String error) {
                System.out.println(error);
            }

            public void doCancelled() {
            }
        };
        // Execute:
        RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
        executer.execute(request, view);
    }

    @Test
    public void testMultipleExecution() throws Exception {
        try {
            RequestBean request = getRequestBean();
            request.setMethod(GET);
            RequestExecuter executer = ServiceLocator.getInstance(RequestExecuter.class);
            View view = new ViewAdapter();
            executer.execute(request, view);
            // Second execution should throw exception:
            executer.execute(request, view);
            Assert.fail("Multiple execution not allowed for same RequestExecuter object!");
        } catch (MultipleRequestInSameRequestExecuterException ex) {
            // This is the success path.
        }
    }
}

