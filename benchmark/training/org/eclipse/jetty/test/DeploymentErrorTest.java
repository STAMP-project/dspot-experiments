/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.test;


import AppLifeCycle.FAILED;
import AppLifeCycle.STARTED;
import AppLifeCycle.STARTING;
import HttpStatus.NOT_FOUND_404;
import HttpStatus.SERVICE_UNAVAILABLE_503;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.AppLifeCycle;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


@ExtendWith(WorkDirExtension.class)
public class DeploymentErrorTest {
    public WorkDir workDir;

    private StacklessLogging stacklessLogging;

    private Server server;

    private DeploymentManager deploymentManager;

    private ContextHandlerCollection contexts;

    /**
     * Test of a server startup, where a DeploymentManager has a WebAppProvider pointing
     * to a directory that already has a webapp that will deploy with an error.
     * The webapp is a WebAppContext with {@code throwUnavailableOnStartupException=true;}.
     */
    @Test
    public void testInitial_BadApp_UnavailableTrue() {
        Assertions.assertThrows(NoClassDefFoundError.class, () -> {
            startServer(( docroots) -> copyBadApp("badapp.xml", docroots));
        });
        // The above should have prevented the server from starting.
        MatcherAssert.assertThat("server.isRunning", server.isRunning(), Is.is(false));
    }

    /**
     * Test of a server startup, where a DeploymentManager has a WebAppProvider pointing
     * to a directory that already has a webapp that will deploy with an error.
     * The webapp is a WebAppContext with {@code throwUnavailableOnStartupException=false;}.
     */
    @Test
    public void testInitial_BadApp_UnavailableFalse() throws Exception {
        startServer(( docroots) -> copyBadApp("badapp-unavailable-false.xml", docroots));
        List<App> apps = new ArrayList<>();
        apps.addAll(deploymentManager.getApps());
        MatcherAssert.assertThat("Apps tracked", apps.size(), Is.is(1));
        String contextPath = "/badapp-uaf";
        App app = findApp(contextPath, apps);
        ContextHandler context = app.getContextHandler();
        MatcherAssert.assertThat("ContextHandler.isStarted", context.isStarted(), Is.is(true));
        MatcherAssert.assertThat("ContextHandler.isFailed", context.isFailed(), Is.is(false));
        MatcherAssert.assertThat("ContextHandler.isAvailable", context.isAvailable(), Is.is(false));
        WebAppContext webapp = ((WebAppContext) (context));
        DeploymentErrorTest.TrackedConfiguration trackedConfiguration = null;
        for (Configuration webappConfig : webapp.getConfigurations()) {
            if (webappConfig instanceof DeploymentErrorTest.TrackedConfiguration)
                trackedConfiguration = ((DeploymentErrorTest.TrackedConfiguration) (webappConfig));

        }
        MatcherAssert.assertThat("webapp TrackedConfiguration exists", trackedConfiguration, Matchers.notNullValue());
        MatcherAssert.assertThat("trackedConfig.preConfigureCount", trackedConfiguration.preConfigureCounts.get(contextPath), Is.is(1));
        MatcherAssert.assertThat("trackedConfig.configureCount", trackedConfiguration.configureCounts.get(contextPath), Is.is(1));
        // NOTE: Failure occurs during configure, so postConfigure never runs.
        MatcherAssert.assertThat("trackedConfig.postConfigureCount", trackedConfiguration.postConfigureCounts.get(contextPath), Matchers.nullValue());
        assertHttpState(contextPath, SERVICE_UNAVAILABLE_503);
    }

    /**
     * Test of a server startup, where a DeploymentManager has a WebAppProvider pointing
     * to a directory that already has no initial webapps that will deploy.
     * A webapp is added (by filesystem copies) into the monitored docroot.
     * The webapp will have a deployment error.
     * The webapp is a WebAppContext with {@code throwUnavailableOnStartupException=true;}.
     */
    @Test
    public void testDelayedAdd_BadApp_UnavailableTrue() throws Exception {
        Path docroots = startServer(null);
        String contextPath = "/badapp";
        DeploymentErrorTest.AppLifeCycleTrackingBinding startTracking = new DeploymentErrorTest.AppLifeCycleTrackingBinding(contextPath);
        DeploymentManager deploymentManager = server.getBean(DeploymentManager.class);
        deploymentManager.addLifeCycleBinding(startTracking);
        copyBadApp("badapp.xml", docroots);
        // Wait for deployment manager to do its thing
        MatcherAssert.assertThat("AppLifeCycle.FAILED event occurred", startTracking.failedLatch.await(3, TimeUnit.SECONDS), Is.is(true));
        List<App> apps = new ArrayList<>();
        apps.addAll(deploymentManager.getApps());
        MatcherAssert.assertThat("Apps tracked", apps.size(), Is.is(1));
        App app = findApp(contextPath, apps);
        ContextHandler context = app.getContextHandler();
        MatcherAssert.assertThat("ContextHandler.isStarted", context.isStarted(), Is.is(false));
        MatcherAssert.assertThat("ContextHandler.isFailed", context.isFailed(), Is.is(true));
        MatcherAssert.assertThat("ContextHandler.isAvailable", context.isAvailable(), Is.is(false));
        WebAppContext webapp = ((WebAppContext) (context));
        DeploymentErrorTest.TrackedConfiguration trackedConfiguration = null;
        for (Configuration webappConfig : webapp.getConfigurations()) {
            if (webappConfig instanceof DeploymentErrorTest.TrackedConfiguration)
                trackedConfiguration = ((DeploymentErrorTest.TrackedConfiguration) (webappConfig));

        }
        MatcherAssert.assertThat("webapp TrackedConfiguration exists", trackedConfiguration, Matchers.notNullValue());
        MatcherAssert.assertThat("trackedConfig.preConfigureCount", trackedConfiguration.preConfigureCounts.get(contextPath), Is.is(1));
        MatcherAssert.assertThat("trackedConfig.configureCount", trackedConfiguration.configureCounts.get(contextPath), Is.is(1));
        // NOTE: Failure occurs during configure, so postConfigure never runs.
        MatcherAssert.assertThat("trackedConfig.postConfigureCount", trackedConfiguration.postConfigureCounts.get(contextPath), Matchers.nullValue());
        assertHttpState(contextPath, NOT_FOUND_404);
    }

    /**
     * Test of a server startup, where a DeploymentManager has a WebAppProvider pointing
     * to a directory that already has no initial webapps that will deploy.
     * A webapp is added (by filesystem copies) into the monitored docroot.
     * The webapp will have a deployment error.
     * The webapp is a WebAppContext with {@code throwUnavailableOnStartupException=false;}.
     */
    @Test
    public void testDelayedAdd_BadApp_UnavailableFalse() throws Exception {
        Path docroots = startServer(null);
        String contextPath = "/badapp-uaf";
        DeploymentErrorTest.AppLifeCycleTrackingBinding startTracking = new DeploymentErrorTest.AppLifeCycleTrackingBinding(contextPath);
        DeploymentManager deploymentManager = server.getBean(DeploymentManager.class);
        deploymentManager.addLifeCycleBinding(startTracking);
        copyBadApp("badapp-unavailable-false.xml", docroots);
        // Wait for deployment manager to do its thing
        startTracking.startedLatch.await(3, TimeUnit.SECONDS);
        List<App> apps = new ArrayList<>();
        apps.addAll(deploymentManager.getApps());
        MatcherAssert.assertThat("Apps tracked", apps.size(), Is.is(1));
        App app = findApp(contextPath, apps);
        ContextHandler context = app.getContextHandler();
        MatcherAssert.assertThat("ContextHandler.isStarted", context.isStarted(), Is.is(true));
        MatcherAssert.assertThat("ContextHandler.isFailed", context.isFailed(), Is.is(false));
        MatcherAssert.assertThat("ContextHandler.isAvailable", context.isAvailable(), Is.is(false));
        WebAppContext webapp = ((WebAppContext) (context));
        DeploymentErrorTest.TrackedConfiguration trackedConfiguration = null;
        for (Configuration webappConfig : webapp.getConfigurations()) {
            if (webappConfig instanceof DeploymentErrorTest.TrackedConfiguration)
                trackedConfiguration = ((DeploymentErrorTest.TrackedConfiguration) (webappConfig));

        }
        MatcherAssert.assertThat("webapp TrackedConfiguration exists", trackedConfiguration, Matchers.notNullValue());
        MatcherAssert.assertThat("trackedConfig.preConfigureCount", trackedConfiguration.preConfigureCounts.get(contextPath), Is.is(1));
        MatcherAssert.assertThat("trackedConfig.configureCount", trackedConfiguration.configureCounts.get(contextPath), Is.is(1));
        // NOTE: Failure occurs during configure, so postConfigure never runs.
        MatcherAssert.assertThat("trackedConfig.postConfigureCount", trackedConfiguration.postConfigureCounts.get(contextPath), Matchers.nullValue());
        assertHttpState(contextPath, SERVICE_UNAVAILABLE_503);
    }

    public static class TrackedConfiguration extends AbstractConfiguration {
        public Map<String, Integer> preConfigureCounts = new HashMap<>();

        public Map<String, Integer> configureCounts = new HashMap<>();

        public Map<String, Integer> postConfigureCounts = new HashMap<>();

        private void incrementCount(WebAppContext context, Map<String, Integer> contextCounts) {
            Integer count = contextCounts.get(context.getContextPath());
            if (count == null) {
                count = new Integer(0);
            }
            count++;
            contextCounts.put(context.getContextPath(), count);
        }

        @Override
        public void preConfigure(WebAppContext context) throws Exception {
            incrementCount(context, preConfigureCounts);
        }

        @Override
        public void configure(WebAppContext context) throws Exception {
            incrementCount(context, configureCounts);
        }

        @Override
        public void postConfigure(WebAppContext context) throws Exception {
            incrementCount(context, postConfigureCounts);
        }
    }

    public static class AppLifeCycleTrackingBinding implements AppLifeCycle.Binding {
        public final CountDownLatch startingLatch = new CountDownLatch(1);

        public final CountDownLatch startedLatch = new CountDownLatch(1);

        public final CountDownLatch failedLatch = new CountDownLatch(1);

        private final String expectedContextPath;

        public AppLifeCycleTrackingBinding(String expectedContextPath) {
            this.expectedContextPath = expectedContextPath;
        }

        @Override
        public String[] getBindingTargets() {
            return new String[]{ AppLifeCycle.STARTING, AppLifeCycle.STARTED, AppLifeCycle.FAILED };
        }

        @Override
        public void processBinding(Node node, App app) {
            if (app.getContextPath().equalsIgnoreCase(expectedContextPath)) {
                if (node.getName().equalsIgnoreCase(STARTING)) {
                    startingLatch.countDown();
                } else
                    if (node.getName().equalsIgnoreCase(STARTED)) {
                        startedLatch.countDown();
                    } else
                        if (node.getName().equalsIgnoreCase(FAILED)) {
                            failedLatch.countDown();
                        }


            }
        }
    }
}

