/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.webapp;


import com.google.inject.Inject;
import java.net.URLEncoder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.yarn.MockApps;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.hadoop.yarn.webapp.view.RobotsTextPage;
import org.apache.hadoop.yarn.webapp.view.TextPage;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestWebApp {
    static final Logger LOG = LoggerFactory.getLogger(TestWebApp.class);

    static class FooController extends Controller {
        final TestWebApp test;

        @Inject
        FooController(TestWebApp test) {
            this.test = test;
        }

        @Override
        public void index() {
            set("key", test.echo("foo"));
        }

        public void bar() {
            set("key", "bar");
        }

        public void names() {
            for (int i = 0; i < 20; ++i) {
                renderText(((MockApps.newAppName()) + "\n"));
            }
        }

        public void ex() {
            boolean err = $("clear").isEmpty();
            renderText((err ? "Should redirect to an error page." : "No error!"));
            if (err) {
                throw new RuntimeException("exception test");
            }
        }

        public void tables() {
            render(TestWebApp.TablesView.class);
        }
    }

    static class FooView extends TextPage {
        @Override
        public void render() {
            puts($("key"), $("foo"));
        }
    }

    static class DefaultController extends Controller {
        @Override
        public void index() {
            set("key", "default");
            render(TestWebApp.FooView.class);
        }
    }

    static class TablesView extends HtmlPage {
        @Override
        public void render(Page.HTML<__> html) {
            set(JQueryUI.DATATABLES_ID, "t1 t2 t3 t4");
            set(JQueryUI.initID(JQueryUI.DATATABLES, "t1"), JQueryUI.tableInit().append("}").toString());
            set(JQueryUI.initID(JQueryUI.DATATABLES, "t2"), StringHelper.join("{bJQueryUI:true, sDom:'t',", "aoColumns:[null, {bSortable:false, bSearchable:false}]}"));
            set(JQueryUI.initID(JQueryUI.DATATABLES, "t3"), "{bJQueryUI:true, sDom:'t'}");
            set(JQueryUI.initID(JQueryUI.DATATABLES, "t4"), "{bJQueryUI:true, sDom:'t'}");
            // ditto
            // th wouldn't work as of dt 1.7.5
            html.title("Test DataTables").link("/static/yarn.css").__(JQueryUI.class).style(".wrapper { padding: 1em }", ".wrapper h2 { margin: 0.5em 0 }", ".dataTables_wrapper { min-height: 1em }").div(".wrapper").h2("Default table init").table("#t1").thead().tr().th("Column1").th("Column2").__().__().tbody().tr().td("c1r1").td("c2r1").__().tr().td("c1r2").td("c2r2").__().__().__().h2("Nested tables").div(JQueryUI._INFO_WRAP).table("#t2").thead().tr().th(JQueryUI._TH, "Column1").th(JQueryUI._TH, "Column2").__().__().tbody().tr().td("r1").td().$class(JQueryUI.C_TABLE).table("#t3").thead().tr().th("SubColumn1").th("SubColumn2").__().__().tbody().tr().td("subc1r1").td("subc2r1").__().tr().td("subc1r2").td("subc2r2").__().__().__().__().__().tr().td("r2").td().$class(JQueryUI.C_TABLE).table("#t4").thead().tr().th("SubColumn1").th("SubColumn2").__().__().tbody().tr().td("subc1r1").td("subc2r1").__().tr().td("subc1r2").td("subc2r2").__().__().__().__().__().__().__().__().__().__();
        }
    }

    @Test
    public void testCreate() {
        WebApp app = WebApps.$for(this).start();
        app.stop();
    }

    @Test
    public void testCreateWithPort() {
        // see if the ephemeral port is updated
        WebApp app = WebApps.$for(this).at(0).start();
        int port = app.getListenerAddress().getPort();
        Assert.assertTrue((port > 0));
        app.stop();
        // try to reuse the port
        app = WebApps.$for(this).at(port).start();
        Assert.assertEquals(port, app.getListenerAddress().getPort());
        app.stop();
    }

    @Test(expected = WebAppException.class)
    public void testCreateWithBindAddressNonZeroPort() {
        WebApp app = WebApps.$for(this).at("0.0.0.0:50000").start();
        int port = app.getListenerAddress().getPort();
        Assert.assertEquals(50000, port);
        // start another WebApp with same NonZero port
        WebApp app2 = WebApps.$for(this).at("0.0.0.0:50000").start();
        // An exception occurs (findPort disabled)
        app.stop();
        app2.stop();
    }

    @Test(expected = WebAppException.class)
    public void testCreateWithNonZeroPort() {
        WebApp app = WebApps.$for(this).at(50000).start();
        int port = app.getListenerAddress().getPort();
        Assert.assertEquals(50000, port);
        // start another WebApp with same NonZero port
        WebApp app2 = WebApps.$for(this).at(50000).start();
        // An exception occurs (findPort disabled)
        app.stop();
        app2.stop();
    }

    @Test
    public void testServePaths() {
        WebApp app = WebApps.$for("test", this).start();
        Assert.assertEquals("/test", app.getRedirectPath());
        String[] expectedPaths = new String[]{ "/test", "/test/*" };
        String[] pathSpecs = app.getServePathSpecs();
        Assert.assertEquals(2, pathSpecs.length);
        for (int i = 0; i < (expectedPaths.length); i++) {
            Assert.assertTrue(ArrayUtils.contains(pathSpecs, expectedPaths[i]));
        }
        app.stop();
    }

    @Test
    public void testServePathsNoName() {
        WebApp app = WebApps.$for("", this).start();
        Assert.assertEquals("/", app.getRedirectPath());
        String[] expectedPaths = new String[]{ "/*" };
        String[] pathSpecs = app.getServePathSpecs();
        Assert.assertEquals(1, pathSpecs.length);
        for (int i = 0; i < (expectedPaths.length); i++) {
            Assert.assertTrue(ArrayUtils.contains(pathSpecs, expectedPaths[i]));
        }
        app.stop();
    }

    @Test
    public void testDefaultRoutes() throws Exception {
        WebApp app = WebApps.$for("test", this).start();
        String baseUrl = TestWebApp.baseUrl(app);
        try {
            Assert.assertEquals("foo", TestWebApp.getContent((baseUrl + "test/foo")).trim());
            Assert.assertEquals("foo", TestWebApp.getContent((baseUrl + "test/foo/index")).trim());
            Assert.assertEquals("bar", TestWebApp.getContent((baseUrl + "test/foo/bar")).trim());
            Assert.assertEquals("default", TestWebApp.getContent((baseUrl + "test")).trim());
            Assert.assertEquals("default", TestWebApp.getContent((baseUrl + "test/")).trim());
            Assert.assertEquals("default", TestWebApp.getContent(baseUrl).trim());
        } finally {
            app.stop();
        }
    }

    @Test
    public void testCustomRoutes() throws Exception {
        WebApp app = WebApps.$for("test", TestWebApp.class, this, "ws").start(new WebApp() {
            @Override
            public void setup() {
                bind(MyTestJAXBContextResolver.class);
                bind(MyTestWebService.class);
                route("/:foo", TestWebApp.FooController.class);
                route("/bar/foo", TestWebApp.FooController.class, "bar");
                route("/foo/:foo", TestWebApp.DefaultController.class);
                route("/foo/bar/:foo", TestWebApp.DefaultController.class, "index");
            }
        });
        String baseUrl = TestWebApp.baseUrl(app);
        try {
            Assert.assertEquals("foo", TestWebApp.getContent(baseUrl).trim());
            Assert.assertEquals("foo", TestWebApp.getContent((baseUrl + "test")).trim());
            Assert.assertEquals("foo1", TestWebApp.getContent((baseUrl + "test/1")).trim());
            Assert.assertEquals("bar", TestWebApp.getContent((baseUrl + "test/bar/foo")).trim());
            Assert.assertEquals("default", TestWebApp.getContent((baseUrl + "test/foo/bar")).trim());
            Assert.assertEquals("default1", TestWebApp.getContent((baseUrl + "test/foo/1")).trim());
            Assert.assertEquals("default2", TestWebApp.getContent((baseUrl + "test/foo/bar/2")).trim());
            Assert.assertEquals(404, TestWebApp.getResponseCode((baseUrl + "test/goo")));
            Assert.assertEquals(200, TestWebApp.getResponseCode((baseUrl + "ws/v1/test")));
            Assert.assertTrue(TestWebApp.getContent((baseUrl + "ws/v1/test")).contains("myInfo"));
        } finally {
            app.stop();
        }
    }

    @Test
    public void testEncodedUrl() throws Exception {
        WebApp app = WebApps.$for("test", TestWebApp.class, this, "ws").start(new WebApp() {
            @Override
            public void setup() {
                bind(MyTestJAXBContextResolver.class);
                bind(MyTestWebService.class);
                route("/:foo", TestWebApp.FooController.class);
            }
        });
        String baseUrl = TestWebApp.baseUrl(app);
        try {
            // Test encoded url
            String rawPath = "localhost:8080";
            String encodedUrl = (baseUrl + "test/") + (URLEncoder.encode(rawPath, "UTF-8"));
            Assert.assertEquals(("foo" + rawPath), TestWebApp.getContent(encodedUrl).trim());
            rawPath = "@;%$";
            encodedUrl = (baseUrl + "test/") + (URLEncoder.encode(rawPath, "UTF-8"));
            Assert.assertEquals(("foo" + rawPath), TestWebApp.getContent(encodedUrl).trim());
        } finally {
            app.stop();
        }
    }

    @Test
    public void testRobotsText() throws Exception {
        WebApp app = WebApps.$for("test", TestWebApp.class, this, "ws").start(new WebApp() {
            @Override
            public void setup() {
                bind(MyTestJAXBContextResolver.class);
                bind(MyTestWebService.class);
            }
        });
        String baseUrl = TestWebApp.baseUrl(app);
        try {
            // using system line separator here since that is what
            // TextView (via PrintWriter) seems to use.
            String[] robotsTxtOutput = TestWebApp.getContent((baseUrl + (RobotsTextPage.ROBOTS_TXT))).trim().split(System.getProperty(("line" + ".separator")));
            Assert.assertEquals(2, robotsTxtOutput.length);
            Assert.assertEquals("User-agent: *", robotsTxtOutput[0]);
            Assert.assertEquals("Disallow: /", robotsTxtOutput[1]);
        } finally {
            app.stop();
        }
    }

    // This is to test the GuiceFilter should only be applied to webAppContext,
    // not to logContext;
    @Test
    public void testYARNWebAppContext() throws Exception {
        // setting up the log context
        System.setProperty("hadoop.log.dir", "/Not/Existing/dir");
        WebApp app = WebApps.$for("test", this).start(new WebApp() {
            @Override
            public void setup() {
                route("/", TestWebApp.FooController.class);
            }
        });
        String baseUrl = TestWebApp.baseUrl(app);
        try {
            // Not able to access a non-existing dir, should not redirect to foo.
            Assert.assertEquals(404, TestWebApp.getResponseCode((baseUrl + "logs")));
            // should be able to redirect to foo.
            Assert.assertEquals("foo", TestWebApp.getContent(baseUrl).trim());
        } finally {
            app.stop();
        }
    }

    @Test
    public void testPortRanges() throws Exception {
        WebApp app = WebApps.$for("test", this).start();
        String baseUrl = TestWebApp.baseUrl(app);
        WebApp app1 = null;
        WebApp app2 = null;
        WebApp app3 = null;
        WebApp app4 = null;
        WebApp app5 = null;
        try {
            int port = ServerSocketUtil.waitForPort(48000, 60);
            Assert.assertEquals("foo", TestWebApp.getContent((baseUrl + "test/foo")).trim());
            app1 = WebApps.$for("test", this).at(port).start();
            Assert.assertEquals(port, app1.getListenerAddress().getPort());
            app2 = WebApps.$for("test", this).at("0.0.0.0", port, true).start();
            Assert.assertTrue(((app2.getListenerAddress().getPort()) > port));
            Configuration conf = new Configuration();
            port = ServerSocketUtil.waitForPort(47000, 60);
            app3 = WebApps.$for("test", this).at(port).withPortRange(conf, "abc").start();
            Assert.assertEquals(port, app3.getListenerAddress().getPort());
            ServerSocketUtil.waitForPort(46000, 60);
            conf.set("abc", "46000-46500");
            app4 = WebApps.$for("test", this).at(port).withPortRange(conf, "abc").start();
            Assert.assertEquals(46000, app4.getListenerAddress().getPort());
            app5 = WebApps.$for("test", this).withPortRange(conf, "abc").start();
            Assert.assertTrue(((app5.getListenerAddress().getPort()) > 46000));
        } finally {
            TestWebApp.stopWebApp(app);
            TestWebApp.stopWebApp(app1);
            TestWebApp.stopWebApp(app2);
            TestWebApp.stopWebApp(app3);
            TestWebApp.stopWebApp(app4);
            TestWebApp.stopWebApp(app5);
        }
    }
}

