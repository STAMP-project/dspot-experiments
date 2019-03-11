/**
 * -
 * -\-\-
 * Dockerfile Maven Plugin
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.it.frontend;


import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;


public class MainIT {
    private static final Logger log = LoggerFactory.getLogger(MainIT.class);

    private static final int BACKEND_PORT = 1337;

    private static final int FRONTEND_PORT = 1338;

    /**
     * Create a Network that both containers are attached to. When the containers are setup, they have
     * .withNetworkAliases("foo") set, which allows the other container to refer to http://foo/ when
     * one container needs to talk to another.
     *
     * This is needed because container.getContainerIpAddress() is only for use for a test
     * to communicate with a container, not container-to-container communication -
     * getContainerIpAddress() will return "localhost" typically
     */
    @Rule
    public final Network network = Network.newNetwork();

    @Rule
    public final GenericContainer backendJob = createBackend(network);

    @Rule
    public final GenericContainer frontendJob = createFrontend(network);

    private URI frontend;

    private URI backend;

    @Test
    public void testVersion() throws Exception {
        String version = requestString(backend.resolve("/api/version"));
        String homepage = requestString(frontend.resolve("/"));
        MatcherAssert.assertThat(homepage, Matchers.containsString(("Backend version: " + version)));
    }

    @Test
    public void testLowercase() throws Exception {
        String homepage;
        homepage = requestString(frontend.resolve("/"));
        Pattern pattern = Pattern.compile("Lower case of ([^ <]+) is according to backend ([^ <]+)");
        Matcher matcher = pattern.matcher(homepage);
        MatcherAssert.assertThat(matcher.find(), Matchers.describedAs("the pattern was found", Matchers.is(true)));
        MatcherAssert.assertThat(matcher.group(2), Matchers.is(matcher.group(1).toLowerCase()));
    }
}

