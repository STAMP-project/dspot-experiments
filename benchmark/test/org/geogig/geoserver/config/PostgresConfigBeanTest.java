/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.config;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


public class PostgresConfigBeanTest {
    private static final String UTF8 = StandardCharsets.UTF_8.name();

    @Test
    public void testBuildUriForRepo() throws UnsupportedEncodingException, URISyntaxException {
        // set some typical values
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "testPassword";
        final String expectedRepoId = "testRepoId";
        // test it
        test(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
    }

    @Test
    public void testBuildUriForRepo_specialCharacters_hash() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "pass#word";
        final String expectedRepoId = "testRepoId";
        // test it
        test(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
    }

    @Test
    public void testBuildUriForRepo_specialCharacters_ampersand() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "pass&word";
        final String expectedRepoId = "testRepoId";
        // test it
        test(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
    }

    @Test
    public void testBuildUriForRepo_specialCharacters_multi() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "!@#$%^&*()";
        final String expectedRepoId = "testRepoId";
        // test it
        test(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
    }

    @Test
    public void testBeanFromURI() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "password";
        final String expectedRepoId = "testRepoId";
        // build a URI
        URI uri = buildURIFromBean(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
        // get a bean
        PostgresConfigBean bean = PostgresConfigBean.from(uri);
        verifyBean(bean, expectedHost, expectedPort, expectedDb, expectedSchema, expectedUser, expectedPassword);
    }

    @Test
    public void testBeanFromURI_specialCharacters_hash() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "pass#word";
        final String expectedRepoId = "testRepoId";
        // build a URI
        URI uri = buildURIFromBean(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
        // get a bean
        PostgresConfigBean bean = PostgresConfigBean.from(uri);
        verifyBean(bean, expectedHost, expectedPort, expectedDb, expectedSchema, expectedUser, expectedPassword);
    }

    @Test
    public void testBeanFromURI_specialCharacters_ampersand() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "pass&word";
        final String expectedRepoId = "testRepoId";
        // build a URI
        URI uri = buildURIFromBean(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
        // get a bean
        PostgresConfigBean bean = PostgresConfigBean.from(uri);
        verifyBean(bean, expectedHost, expectedPort, expectedDb, expectedSchema, expectedUser, expectedPassword);
    }

    @Test
    public void testBeanFromURI_specialCharacters_multi() throws UnsupportedEncodingException, URISyntaxException {
        final String expectedHost = "testHost";
        final Integer expectedPort = Integer.valueOf(5432);
        final String expectedDb = "testDb";
        final String expectedSchema = "testSchema";
        final String expectedUser = "testUser";
        final String expectedPassword = "!@#$%^&*()";
        final String expectedRepoId = "testRepoId";
        // build a URI
        URI uri = buildURIFromBean(expectedHost, expectedPort, expectedDb, expectedSchema, expectedRepoId, expectedUser, expectedPassword);
        // get a bean
        PostgresConfigBean bean = PostgresConfigBean.from(uri);
        verifyBean(bean, expectedHost, expectedPort, expectedDb, expectedSchema, expectedUser, expectedPassword);
    }
}

