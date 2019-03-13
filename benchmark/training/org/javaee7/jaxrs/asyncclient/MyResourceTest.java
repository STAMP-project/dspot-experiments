package org.javaee7.jaxrs.asyncclient;


import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * In this sample we're going to explore how to communicate with a +JAX-RS+
 * service via an asynchronous invocation from the client.
 *
 * First step; we need a service to invoke.
 *
 * Let's create a simple +GET+ method.
 *
 * include::MyResource#getList[]
 *
 * For +JAX-RS+ to expose our service we need to provide an implementation of
 * the +JAX-RS+ +Application+ class to define our root path.
 *
 * include::MyApplication[]
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    @ArquillianResource
    private URL base;

    private static WebTarget target;

    /**
     * Before we can invoke our service we need to setup the client.
     *
     * include::MyResourceTest#setUpClass[]
     *
     * Now we are free to invoke our deployed service by using the +JAX-RS+
     * client library.
     *
     * The asynchronous client library comes with multiple option on how
     * to invoke the methods. First let's look at using the +Future+ option
     * with access to the complete +Response+.
     */
    @Test
    public void testPollingResponse() throws InterruptedException, ExecutionException {
        Future<Response> r1 = MyResourceTest.target.request().async().get();// <1> Build an asynchronous request handler for the +Response+ object

        String response = r1.get().readEntity(String.class);// <2> Read the entity from the body of the +Response+

        Assert.assertEquals("apple", response);// <3> Validate we got the expected value

    }

    /**
     * Another possibility is to use the +Future+ option with access to only the +Response+ body.
     */
    @Test
    public void testPollingString() throws InterruptedException, ExecutionException {
        Future<String> r1 = MyResourceTest.target.request().async().get(String.class);// <1> Build an asynchronous request handler for the body of the +Response+

        String response = r1.get();// <2> Read the entity directly from the +Future+

        Assert.assertEquals("apple", response);// <3> Validate we got the expected value

    }

    /**
     * You can also register a +InvocationCallback+ and get a callback when the +Request+ is done.
     */
    @Test
    public void testInvocationCallback() throws InterruptedException, ExecutionException {
        MyResourceTest.target.request().async().get(new javax.ws.rs.client.InvocationCallback<String>() {
            // <1> Build an asynchronous request callback for the body of the +Response+
            @Override
            public void completed(String r) {
                // <2> Called when the +Request+ is completed and our entiy parsed
                Assert.assertEquals("apple", r);
            }

            @Override
            public void failed(Throwable t) {
                // <3> Called if the +Request+ failed to complete
                Assert.fail(t.getMessage());
            }
        });
    }
}

