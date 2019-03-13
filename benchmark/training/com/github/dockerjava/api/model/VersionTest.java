package com.github.dockerjava.api.model;


import RemoteApiVersion.VERSION_1_22;
import RemoteApiVersion.VERSION_1_38;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.test.serdes.JSONSamples;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class VersionTest {
    @Test
    public void testSerDer1() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Version.class);
        final Version version = JSONSamples.testRoundTrip(VERSION_1_22, "/version/1.json", type);
        MatcherAssert.assertThat(version, Matchers.notNullValue());
        MatcherAssert.assertThat(version.getVersion(), CoreMatchers.is("1.10.1"));
        MatcherAssert.assertThat(version.getApiVersion(), CoreMatchers.is("1.22"));
        MatcherAssert.assertThat(version.getGitCommit(), CoreMatchers.is("9e83765"));
        MatcherAssert.assertThat(version.getGoVersion(), CoreMatchers.is("go1.5.3"));
        MatcherAssert.assertThat(version.getOperatingSystem(), CoreMatchers.is("linux"));
        MatcherAssert.assertThat(version.getArch(), CoreMatchers.is("amd64"));
        MatcherAssert.assertThat(version.getKernelVersion(), CoreMatchers.is("4.1.17-boot2docker"));
        MatcherAssert.assertThat(version.getBuildTime(), CoreMatchers.is("2016-02-11T20:39:58.688092588+00:00"));
    }

    @Test
    public void version_1_38() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(Version.class);
        final Version version = JSONSamples.testRoundTrip(VERSION_1_38, "/version/lcow.json", type);
        MatcherAssert.assertThat(version, Matchers.notNullValue());
        MatcherAssert.assertThat(version.getApiVersion(), CoreMatchers.is("1.38"));
        MatcherAssert.assertThat(version.getArch(), CoreMatchers.is("amd64"));
        MatcherAssert.assertThat(version.getBuildTime(), CoreMatchers.is("2018-08-21T17:36:40.000000000+00:00"));
        Map<String, String> details = new LinkedHashMap<>();
        details.put("ApiVersion", "1.38");
        details.put("Arch", "amd64");
        details.put("BuildTime", "2018-08-21T17:36:40.000000000+00:00");
        details.put("Experimental", "true");
        details.put("GitCommit", "e68fc7a");
        details.put("GoVersion", "go1.10.3");
        details.put("KernelVersion", "10.0 17134 (17134.1.amd64fre.rs4_release.180410-1804)");
        details.put("MinAPIVersion", "1.24");
        details.put("Os", "windows");
        List<VersionComponent> components = Collections.singletonList(new VersionComponent().withDetails(details).withName("Engine").withVersion("18.06.1-ce"));
        MatcherAssert.assertThat(version.getComponents(), Matchers.equalTo(components));
        MatcherAssert.assertThat(version.getExperimental(), CoreMatchers.is(true));
        MatcherAssert.assertThat(version.getGitCommit(), CoreMatchers.is("e68fc7a"));
        MatcherAssert.assertThat(version.getGoVersion(), CoreMatchers.is("go1.10.3"));
        MatcherAssert.assertThat(version.getKernelVersion(), CoreMatchers.is("10.0 17134 (17134.1.amd64fre.rs4_release.180410-1804)"));
        MatcherAssert.assertThat(version.getMinAPIVersion(), CoreMatchers.is("1.24"));
        MatcherAssert.assertThat(version.getOperatingSystem(), CoreMatchers.is("windows"));
        MatcherAssert.assertThat(version.getPlatform(), Matchers.equalTo(new VersionPlatform().withName("")));
        MatcherAssert.assertThat(version.getVersion(), CoreMatchers.is("18.06.1-ce"));
    }
}

