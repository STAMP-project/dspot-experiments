package io.vertx.example.osgi.it;


import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.example.osgi.service.DataService;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 *
 *
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class VertxServiceIT {
    @Inject
    private Vertx vertxService;

    @Inject
    private EventBus eventBus;

    @Inject
    private DataService data;

    @Inject
    private BundleContext context;

    @Test
    public void testThatTheServiceIsAvailable() throws Exception {
        for (Bundle bundle : context.getBundles()) {
            System.out.println(((((("[" + (bundle.getBundleId())) + "] - ") + (bundle.getSymbolicName())) + " - ") + (bundle.getVersion())));
        }
        Class<?> loadClass = context.getBundle(0).loadClass("org.hsqldb.jdbcDriver");
        System.out.println(("Driver: " + loadClass));
        Assert.assertNotNull(vertxService);
        Assert.assertNotNull(eventBus);
        // Wait for verticle deployment
        VertxServiceIT.await(() -> {
            System.out.println(vertxService.deploymentIDs());
            return (vertxService.deploymentIDs().size()) >= 2;
        });
        // ==== Web Application
        // Check that the static assets has been published by the vert.x web app
        URL url = new URL("http://localhost:8081/assets/index.html");
        String page = IOUtils.toString(url.toURI(), "utf-8");
        Assert.assertNotNull(page);
        Assert.assertFalse(page.isEmpty());
        Assert.assertTrue(page.contains("Static web server"));
        url = new URL("http://localhost:8081/products");
        page = IOUtils.toString(url.toURI(), "utf-8");
        Assert.assertNotNull(page);
        Assert.assertFalse(page.isEmpty());
        Assert.assertTrue(page.contains("Egg Whisk"));
        // ==== Http Server
        url = new URL("http://localhost:8080");
        page = IOUtils.toString(url.toURI(), "utf-8");
        Assert.assertNotNull(page);
        Assert.assertFalse(page.isEmpty());
        Assert.assertTrue(page.contains("Hello from OSGi"));
        // ==== Data service (JDBC)
        Assert.assertNotNull(data);
        List<String> results = new CopyOnWriteArrayList<>();
        data.retrieve(( ar) -> {
            if (ar.succeeded()) {
                results.addAll(ar.result());
            }
        });
        VertxServiceIT.await(() -> (results.size()) != 0);
        Assert.assertThat(results.size(), CoreMatchers.is(1));
        System.out.println(("JDBC test results: " + results));
    }
}

