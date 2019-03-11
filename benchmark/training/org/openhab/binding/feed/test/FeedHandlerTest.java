/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.feed.test;


import HttpStatus.FORBIDDEN_403;
import HttpStatus.INTERNAL_SERVER_ERROR_500;
import HttpStatus.NOT_FOUND_404;
import HttpStatus.UNAUTHORIZED_401;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ManagedThingProvider;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.eclipse.smarthome.test.storage.VolatileStorageService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openhab.binding.feed.internal.handler.FeedHandler;


/**
 * Tests for {@link FeedHandler}
 *
 * @author Svilen Valkanov - Initial contribution
 * @author Wouter Born - Migrate Groovy to Java tests
 */
public class FeedHandlerTest extends JavaOSGiTest {
    // Servlet URL configuration
    private static final String MOCK_SERVLET_PROTOCOL = "http";

    private static final String MOCK_SERVLET_HOSTNAME = "localhost";

    private static final int MOCK_SERVLET_PORT = 9090;

    private static final String MOCK_SERVLET_PATH = "/test/feed";

    // Files used for the test as input. They are located in /src/test/resources directory
    /**
     * The default mock content in the test is RSS 2.0 format, as this is the most popular format
     */
    private static final String DEFAULT_MOCK_CONTENT = "rss_2.0.xml";

    /**
     * One new entry is added to {@link #DEFAULT_MOCK_CONTENT}
     */
    private static final String MOCK_CONTENT_CHANGED = "rss_2.0_changed.xml";

    private static final String ITEM_NAME = "testItem";

    private static final String THING_NAME = "testFeedThing";

    /**
     * Default auto refresh interval for the test is 1 Minute.
     */
    private static final int DEFAULT_TEST_AUTOREFRESH_TIME = 1;

    /**
     * It is updated from mocked {@link StateChangeListener#stateUpdated()}
     */
    private StringType currentItemState = null;

    // Required services for the test
    private ManagedThingProvider managedThingProvider;

    private VolatileStorageService volatileStorageService;

    private ThingRegistry thingRegistry;

    private FeedHandlerTest.FeedServiceMock servlet;

    private Thing feedThing;

    private FeedHandler feedHandler;

    private ChannelUID channelUID;

    /**
     * This class is used as a mock for HTTP web server, serving XML feed content.
     */
    class FeedServiceMock extends HttpServlet {
        private static final long serialVersionUID = -7810045624309790473L;

        String feedContent;

        int httpStatus;

        public FeedServiceMock(String feedContentFile) {
            super();
            try {
                setFeedContent(feedContentFile);
            } catch (IOException e) {
                throw new IllegalArgumentException(("Error loading feed content from: " + feedContentFile));
            }
            // By default the servlet returns HTTP Status code 200 OK
            this.httpStatus = HttpStatus.OK_200;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.getOutputStream().println(feedContent);
            // Recommended RSS MIME type - http://www.rssboard.org/rss-mime-type-application.txt
            // Atom MIME type is - application/atom+xml
            // Other MIME types - text/plan, text/xml, text/html are tested and accepted as well
            response.setContentType("application/rss+xml");
            response.setStatus(httpStatus);
        }

        public void setFeedContent(String feedContentFile) throws IOException {
            String path = "input/" + feedContentFile;
            feedContent = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(path));
        }
    }

    @Test
    public void assertThatInvalidConfigurationFallsBackToDefaultValues() {
        String mockServletURL = generateURLString(FeedHandlerTest.MOCK_SERVLET_PROTOCOL, FeedHandlerTest.MOCK_SERVLET_HOSTNAME, FeedHandlerTest.MOCK_SERVLET_PORT, FeedHandlerTest.MOCK_SERVLET_PATH);
        BigDecimal defaultTestRefreshInterval = new BigDecimal((-10));
        initializeFeedHandler(mockServletURL, defaultTestRefreshInterval);
    }

    @Test
    @Category(SlowTests.class)
    public void assertThatItemsStateIsNotUpdatedOnAutoRefreshIfContentIsNotChanged() throws IOException, InterruptedException {
        boolean commandReceived = false;
        boolean contentChanged = false;
        testIfItemStateIsUpdated(commandReceived, contentChanged);
    }

    @Test
    @Category(SlowTests.class)
    public void assertThatItemsStateIsUpdatedOnAutoRefreshIfContentChanged() throws IOException, InterruptedException {
        boolean commandReceived = false;
        boolean contentChanged = true;
        testIfItemStateIsUpdated(commandReceived, contentChanged);
    }

    @Test
    public void assertThatThingsStatusIsUpdatedWhenHTTP500ErrorCodeIsReceived() throws InterruptedException {
        testIfThingStatusIsUpdated(INTERNAL_SERVER_ERROR_500);
    }

    @Test
    public void assertThatThingsStatusIsUpdatedWhenHTTP401ErrorCodeIsReceived() throws InterruptedException {
        testIfThingStatusIsUpdated(UNAUTHORIZED_401);
    }

    @Test
    public void assertThatThingsStatusIsUpdatedWhenHTTP403ErrorCodeIsReceived() throws InterruptedException {
        testIfThingStatusIsUpdated(FORBIDDEN_403);
    }

    @Test
    public void assertThatThingsStatusIsUpdatedWhenHTTP404ErrorCodeIsReceived() throws InterruptedException {
        testIfThingStatusIsUpdated(NOT_FOUND_404);
    }

    @Test
    public void createThingWithInvalidUrlProtocol() {
        String invalidProtocol = "gdfs";
        String invalidURL = generateURLString(invalidProtocol, FeedHandlerTest.MOCK_SERVLET_HOSTNAME, FeedHandlerTest.MOCK_SERVLET_PORT, FeedHandlerTest.MOCK_SERVLET_PATH);
        initializeFeedHandler(invalidURL);
        waitForAssert(() -> {
            assertThat(feedThing.getStatus(), is(equalTo(OFFLINE)));
            assertThat(feedThing.getStatusInfo().getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void createThingWithInvalidUrlHostname() {
        String invalidHostname = "invalidhost";
        String invalidURL = generateURLString(FeedHandlerTest.MOCK_SERVLET_PROTOCOL, invalidHostname, FeedHandlerTest.MOCK_SERVLET_PORT, FeedHandlerTest.MOCK_SERVLET_PATH);
        initializeFeedHandler(invalidURL);
        waitForAssert(() -> {
            assertThat(feedThing.getStatus(), is(equalTo(OFFLINE)));
            assertThat(feedThing.getStatusInfo().getStatusDetail(), is(equalTo(ThingStatusDetail.COMMUNICATION_ERROR)));
        });
    }

    @Test
    public void createThingWithInvalidUrlPath() {
        String invalidPath = "/invalid/path";
        String invalidURL = generateURLString(FeedHandlerTest.MOCK_SERVLET_PROTOCOL, FeedHandlerTest.MOCK_SERVLET_HOSTNAME, FeedHandlerTest.MOCK_SERVLET_PORT, invalidPath);
        initializeFeedHandler(invalidURL);
        waitForAssert(() -> {
            assertThat(feedThing.getStatus(), is(equalTo(OFFLINE)));
            assertThat(feedThing.getStatusInfo().getStatusDetail(), is(equalTo(ThingStatusDetail.COMMUNICATION_ERROR)));
        });
    }
}

