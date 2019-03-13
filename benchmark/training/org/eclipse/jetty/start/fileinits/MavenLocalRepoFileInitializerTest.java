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
package org.eclipse.jetty.start.fileinits;


import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jetty.start.BaseHome;
import org.eclipse.jetty.start.fileinits.MavenLocalRepoFileInitializer.Coordinates;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


@ExtendWith(WorkDirExtension.class)
public class MavenLocalRepoFileInitializerTest {
    public WorkDir testdir;

    private BaseHome baseHome;

    @Test
    public void testGetCoordinate_NotMaven() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome);
        String ref = "http://www.eclipse.org/jetty";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coords", coords, Matchers.nullValue());
    }

    @Test
    public void testGetCoordinate_InvalidMaven() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome);
        String ref = "maven://www.eclipse.org/jetty";
        RuntimeException x = Assertions.assertThrows(RuntimeException.class, () -> repo.getCoordinates(URI.create(ref)));
        MatcherAssert.assertThat(x.getMessage(), Matchers.containsString("Not a valid maven:// uri"));
    }

    @Test
    public void testGetCoordinate_Normal() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome);
        String ref = "maven://org.eclipse.jetty/jetty-start/9.3.x";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coordinates", coords, Matchers.notNullValue());
        MatcherAssert.assertThat("coords.groupId", coords.groupId, Matchers.is("org.eclipse.jetty"));
        MatcherAssert.assertThat("coords.artifactId", coords.artifactId, Matchers.is("jetty-start"));
        MatcherAssert.assertThat("coords.version", coords.version, Matchers.is("9.3.x"));
        MatcherAssert.assertThat("coords.type", coords.type, Matchers.is("jar"));
        MatcherAssert.assertThat("coords.classifier", coords.classifier, Matchers.nullValue());
        MatcherAssert.assertThat("coords.toCentralURI", coords.toCentralURI().toASCIIString(), Matchers.is("https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-start/9.3.x/jetty-start-9.3.x.jar"));
    }

    @Test
    public void testGetCoordinate_Zip() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome);
        String ref = "maven://org.eclipse.jetty/jetty-distribution/9.3.x/zip";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coordinates", coords, Matchers.notNullValue());
        MatcherAssert.assertThat("coords.groupId", coords.groupId, Matchers.is("org.eclipse.jetty"));
        MatcherAssert.assertThat("coords.artifactId", coords.artifactId, Matchers.is("jetty-distribution"));
        MatcherAssert.assertThat("coords.version", coords.version, Matchers.is("9.3.x"));
        MatcherAssert.assertThat("coords.type", coords.type, Matchers.is("zip"));
        MatcherAssert.assertThat("coords.classifier", coords.classifier, Matchers.nullValue());
        MatcherAssert.assertThat("coords.toCentralURI", coords.toCentralURI().toASCIIString(), Matchers.is("https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.3.x/jetty-distribution-9.3.x.zip"));
    }

    @Test
    public void testGetCoordinate_TestJar() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome);
        String ref = "maven://org.eclipse.jetty/jetty-http/9.3.x/jar/tests";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coordinates", coords, Matchers.notNullValue());
        MatcherAssert.assertThat("coords.groupId", coords.groupId, Matchers.is("org.eclipse.jetty"));
        MatcherAssert.assertThat("coords.artifactId", coords.artifactId, Matchers.is("jetty-http"));
        MatcherAssert.assertThat("coords.version", coords.version, Matchers.is("9.3.x"));
        MatcherAssert.assertThat("coords.type", coords.type, Matchers.is("jar"));
        MatcherAssert.assertThat("coords.classifier", coords.classifier, Matchers.is("tests"));
        MatcherAssert.assertThat("coords.toCentralURI", coords.toCentralURI().toASCIIString(), Matchers.is("https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-http/9.3.x/jetty-http-9.3.x-tests.jar"));
    }

    @Test
    public void testGetCoordinate_Test_UnspecifiedType() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome);
        String ref = "maven://org.eclipse.jetty/jetty-http/9.3.x//tests";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coordinates", coords, Matchers.notNullValue());
        MatcherAssert.assertThat("coords.groupId", coords.groupId, Matchers.is("org.eclipse.jetty"));
        MatcherAssert.assertThat("coords.artifactId", coords.artifactId, Matchers.is("jetty-http"));
        MatcherAssert.assertThat("coords.version", coords.version, Matchers.is("9.3.x"));
        MatcherAssert.assertThat("coords.type", coords.type, Matchers.is("jar"));
        MatcherAssert.assertThat("coords.classifier", coords.classifier, Matchers.is("tests"));
        MatcherAssert.assertThat("coords.toCentralURI", coords.toCentralURI().toASCIIString(), Matchers.is("https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-http/9.3.x/jetty-http-9.3.x-tests.jar"));
    }

    @Test
    public void testGetCoordinate_TestMavenBaseUri() {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome, null, false, "https://repo1.maven.org/maven2/");
        String ref = "maven://org.eclipse.jetty/jetty-http/9.3.x/jar/tests";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coordinates", coords, Matchers.notNullValue());
        MatcherAssert.assertThat("coords.groupId", coords.groupId, Matchers.is("org.eclipse.jetty"));
        MatcherAssert.assertThat("coords.artifactId", coords.artifactId, Matchers.is("jetty-http"));
        MatcherAssert.assertThat("coords.version", coords.version, Matchers.is("9.3.x"));
        MatcherAssert.assertThat("coords.type", coords.type, Matchers.is("jar"));
        MatcherAssert.assertThat("coords.classifier", coords.classifier, Matchers.is("tests"));
        MatcherAssert.assertThat("coords.toCentralURI", coords.toCentralURI().toASCIIString(), Matchers.is("https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-http/9.3.x/jetty-http-9.3.x-tests.jar"));
    }

    @Test
    public void testDownload_default_repo() throws Exception {
        MavenLocalRepoFileInitializer repo = new MavenLocalRepoFileInitializer(baseHome, null, false);
        String ref = "maven://org.eclipse.jetty/jetty-http/9.4.10.v20180503/jar/tests";
        Coordinates coords = repo.getCoordinates(URI.create(ref));
        MatcherAssert.assertThat("Coordinates", coords, Matchers.notNullValue());
        MatcherAssert.assertThat("coords.groupId", coords.groupId, Matchers.is("org.eclipse.jetty"));
        MatcherAssert.assertThat("coords.artifactId", coords.artifactId, Matchers.is("jetty-http"));
        MatcherAssert.assertThat("coords.version", coords.version, Matchers.is("9.4.10.v20180503"));
        MatcherAssert.assertThat("coords.type", coords.type, Matchers.is("jar"));
        MatcherAssert.assertThat("coords.classifier", coords.classifier, Matchers.is("tests"));
        MatcherAssert.assertThat("coords.toCentralURI", coords.toCentralURI().toASCIIString(), Matchers.is("https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-http/9.4.10.v20180503/jetty-http-9.4.10.v20180503-tests.jar"));
        Path destination = Paths.get(System.getProperty("java.io.tmpdir"), "jetty-http-9.4.10.v20180503-tests.jar");
        Files.deleteIfExists(destination);
        repo.download(coords.toCentralURI(), destination);
        MatcherAssert.assertThat(Files.exists(destination), Matchers.is(true));
        MatcherAssert.assertThat(destination.toFile().length(), Matchers.is(962621L));
    }
}

