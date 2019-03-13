/**
 * -
 * -\-\-
 * Helios Client
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
package com.spotify.helios.common.descriptors;


import Job.Builder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.spotify.helios.common.Hash;
import com.spotify.helios.common.Json;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JobTest {
    @Test
    public void testNormalizedExcludesEmptyStrings() throws Exception {
        final Job j = Job.newBuilder().setName("x").setImage("x").setVersion("x").setRegistrationDomain("").build();
        Assert.assertFalse(Json.asNormalizedString(j).contains("registrationDomain"));
    }

    @Test
    public void verifyBuilder() throws Exception {
        final Job.Builder builder = Job.newBuilder();
        // Input to setXXX
        final String setName = "set_name";
        final String setVersion = "set_version";
        final String setImage = "set_image";
        final String setHostname = "set_hostname";
        final List<String> setCommand = Arrays.asList("set", "command");
        final Map<String, String> setEnv = ImmutableMap.of("set", "env");
        final Map<String, PortMapping> setPorts = ImmutableMap.of("set_ports", PortMapping.of(1234));
        final ImmutableMap.Builder<String, ServicePortParameters> setServicePortsBuilder = ImmutableMap.builder();
        setServicePortsBuilder.put("set_ports1", new ServicePortParameters(ImmutableList.of("tag1", "tag2")));
        setServicePortsBuilder.put("set_ports2", new ServicePortParameters(ImmutableList.of("tag3", "tag4")));
        final ServicePorts setServicePorts = new ServicePorts(setServicePortsBuilder.build());
        final Map<ServiceEndpoint, ServicePorts> setRegistration = ImmutableMap.of(ServiceEndpoint.of("set_service", "set_proto"), setServicePorts);
        final Integer setGracePeriod = 120;
        final Map<String, String> setVolumes = ImmutableMap.of("/set", "/volume");
        final Date setExpires = new Date();
        final String setRegistrationDomain = "my.domain";
        final String setCreatingUser = "username";
        final Resources setResources = new Resources(10485760L, 10485761L, 4L, "1");
        final HealthCheck setHealthCheck = HealthCheck.newHttpHealthCheck().setPath("/healthcheck").setPort("set_ports").build();
        final List<String> setSecurityOpt = Lists.newArrayList("label:user:dxia", "apparmor:foo");
        final String setNetworkMode = "host";
        final Map<String, String> setMetadata = ImmutableMap.of("set_metadata_key", "set_metadata_val");
        final Set<String> setAddCapabilities = ImmutableSet.of("set_cap_add1", "set_cap_add2");
        final Set<String> setDropCapabilities = ImmutableSet.of("set_cap_drop1", "set_cap_drop2");
        final Map<String, String> setLabels = ImmutableMap.of("set_label_key", "set_label_val");
        // Input to addXXX
        final Map<String, String> addEnv = ImmutableMap.of("add", "env");
        final Map<String, PortMapping> addPorts = ImmutableMap.of("add_ports", PortMapping.of(4711));
        final ImmutableMap.Builder<String, ServicePortParameters> addServicePortsBuilder = ImmutableMap.builder();
        addServicePortsBuilder.put("add_ports1", new ServicePortParameters(ImmutableList.of("tag1", "tag2")));
        addServicePortsBuilder.put("add_ports2", new ServicePortParameters(ImmutableList.of("tag3", "tag4")));
        final ServicePorts addServicePorts = new ServicePorts(addServicePortsBuilder.build());
        final Map<ServiceEndpoint, ServicePorts> addRegistration = ImmutableMap.of(ServiceEndpoint.of("add_service", "add_proto"), addServicePorts);
        final Map<String, String> addVolumes = ImmutableMap.of("/add", "/volume");
        final Map<String, String> addMetadata = ImmutableMap.of("add_metadata_key", "add_metadata_val");
        final Map<String, String> addLabels = ImmutableMap.of("add_labels_key", "add_labels_val");
        // Expected output from getXXX
        final String expectedName = setName;
        final String expectedVersion = setVersion;
        final String expectedImage = setImage;
        final String expectedHostname = setHostname;
        final List<String> expectedCommand = setCommand;
        final Map<String, String> expectedEnv = concat(setEnv, addEnv);
        final Map<String, PortMapping> expectedPorts = concat(setPorts, addPorts);
        final Map<ServiceEndpoint, ServicePorts> expectedRegistration = concat(setRegistration, addRegistration);
        final Integer expectedGracePeriod = setGracePeriod;
        final Map<String, String> expectedVolumes = concat(setVolumes, addVolumes);
        final Date expectedExpires = setExpires;
        final String expectedRegistrationDomain = setRegistrationDomain;
        final String expectedCreatingUser = setCreatingUser;
        final Resources expectedResources = setResources;
        final HealthCheck expectedHealthCheck = setHealthCheck;
        final List<String> expectedSecurityOpt = setSecurityOpt;
        final String expectedNetworkMode = setNetworkMode;
        final Map<String, String> expectedMetadata = concat(setMetadata, addMetadata);
        final Set<String> expectedAddCapabilities = setAddCapabilities;
        final Set<String> expectedDropCapabilities = setDropCapabilities;
        final Map<String, String> expectedLabels = concat(setLabels, addLabels);
        // Check setXXX methods
        builder.setName(setName);
        builder.setVersion(setVersion);
        builder.setImage(setImage);
        builder.setHostname(setHostname);
        builder.setCommand(setCommand);
        builder.setEnv(setEnv);
        builder.setPorts(setPorts);
        builder.setRegistration(setRegistration);
        builder.setGracePeriod(setGracePeriod);
        builder.setVolumes(setVolumes);
        builder.setExpires(setExpires);
        builder.setRegistrationDomain(setRegistrationDomain);
        builder.setCreatingUser(setCreatingUser);
        builder.setResources(setResources);
        builder.setHealthCheck(setHealthCheck);
        builder.setSecurityOpt(setSecurityOpt);
        builder.setNetworkMode(setNetworkMode);
        builder.setMetadata(setMetadata);
        builder.setAddCapabilities(setAddCapabilities);
        builder.setDropCapabilities(setDropCapabilities);
        builder.setLabels(setLabels);
        // Check addXXX methods
        for (final Map.Entry<String, String> entry : addEnv.entrySet()) {
            builder.addEnv(entry.getKey(), entry.getValue());
        }
        for (final Map.Entry<String, PortMapping> entry : addPorts.entrySet()) {
            builder.addPort(entry.getKey(), entry.getValue());
        }
        for (final Map.Entry<ServiceEndpoint, ServicePorts> entry : addRegistration.entrySet()) {
            builder.addRegistration(entry.getKey(), entry.getValue());
        }
        for (final Map.Entry<String, String> entry : addVolumes.entrySet()) {
            builder.addVolume(entry.getKey(), entry.getValue());
        }
        for (final Map.Entry<String, String> entry : addMetadata.entrySet()) {
            builder.addMetadata(entry.getKey(), entry.getValue());
        }
        for (final Map.Entry<String, String> entry : addLabels.entrySet()) {
            builder.addLabels(entry.getKey(), entry.getValue());
        }
        Assert.assertEquals("name", expectedName, builder.getName());
        Assert.assertEquals("version", expectedVersion, builder.getVersion());
        Assert.assertEquals("image", expectedImage, builder.getImage());
        Assert.assertEquals("hostname", expectedHostname, builder.getHostname());
        Assert.assertEquals("command", expectedCommand, builder.getCommand());
        Assert.assertEquals("env", expectedEnv, builder.getEnv());
        Assert.assertEquals("ports", expectedPorts, builder.getPorts());
        Assert.assertEquals("registration", expectedRegistration, builder.getRegistration());
        Assert.assertEquals("gracePeriod", expectedGracePeriod, builder.getGracePeriod());
        Assert.assertEquals("volumes", expectedVolumes, builder.getVolumes());
        Assert.assertEquals("expires", expectedExpires, builder.getExpires());
        Assert.assertEquals("registrationDomain", expectedRegistrationDomain, builder.getRegistrationDomain());
        Assert.assertEquals("creatingUser", expectedCreatingUser, builder.getCreatingUser());
        Assert.assertEquals("resources", expectedResources, builder.getResources());
        Assert.assertEquals("healthCheck", expectedHealthCheck, builder.getHealthCheck());
        Assert.assertEquals("securityOpt", expectedSecurityOpt, builder.getSecurityOpt());
        Assert.assertEquals("networkMode", expectedNetworkMode, builder.getNetworkMode());
        Assert.assertEquals("metadata", expectedMetadata, builder.getMetadata());
        Assert.assertEquals("addCapabilities", expectedAddCapabilities, builder.getAddCapabilities());
        Assert.assertEquals("dropCapabilities", expectedDropCapabilities, builder.getDropCapabilities());
        Assert.assertEquals("labels", expectedLabels, builder.getLabels());
        // Check final output
        final Job job = builder.build();
        Assert.assertEquals("name", expectedName, job.getId().getName());
        Assert.assertEquals("version", expectedVersion, job.getId().getVersion());
        Assert.assertEquals("image", expectedImage, job.getImage());
        Assert.assertEquals("hostname", expectedHostname, job.getHostname());
        Assert.assertEquals("command", expectedCommand, job.getCommand());
        Assert.assertEquals("env", expectedEnv, job.getEnv());
        Assert.assertEquals("ports", expectedPorts, job.getPorts());
        Assert.assertEquals("registration", expectedRegistration, job.getRegistration());
        Assert.assertEquals("gracePeriod", expectedGracePeriod, job.getGracePeriod());
        Assert.assertEquals("volumes", expectedVolumes, job.getVolumes());
        Assert.assertEquals("expires", expectedExpires, job.getExpires());
        Assert.assertEquals("registrationDomain", expectedRegistrationDomain, job.getRegistrationDomain());
        Assert.assertEquals("creatingUser", expectedCreatingUser, job.getCreatingUser());
        Assert.assertEquals("resources", expectedResources, job.getResources());
        Assert.assertEquals("healthCheck", expectedHealthCheck, job.getHealthCheck());
        Assert.assertEquals("securityOpt", expectedSecurityOpt, job.getSecurityOpt());
        Assert.assertEquals("networkMode", expectedNetworkMode, job.getNetworkMode());
        Assert.assertEquals("metadata", expectedMetadata, job.getMetadata());
        Assert.assertEquals("addCapabilities", expectedAddCapabilities, job.getAddCapabilities());
        Assert.assertEquals("dropCapabilities", expectedDropCapabilities, job.getDropCapabilities());
        Assert.assertEquals("labels", expectedLabels, job.getLabels());
        // Check toBuilder
        final Job.Builder rebuilder = job.toBuilder();
        Assert.assertEquals("name", expectedName, rebuilder.getName());
        Assert.assertEquals("version", expectedVersion, rebuilder.getVersion());
        Assert.assertEquals("image", expectedImage, rebuilder.getImage());
        Assert.assertEquals("hostname", expectedHostname, rebuilder.getHostname());
        Assert.assertEquals("command", expectedCommand, rebuilder.getCommand());
        Assert.assertEquals("env", expectedEnv, rebuilder.getEnv());
        Assert.assertEquals("ports", expectedPorts, rebuilder.getPorts());
        Assert.assertEquals("registration", expectedRegistration, rebuilder.getRegistration());
        Assert.assertEquals("gracePeriod", expectedGracePeriod, rebuilder.getGracePeriod());
        Assert.assertEquals("volumes", expectedVolumes, rebuilder.getVolumes());
        Assert.assertEquals("expires", expectedExpires, rebuilder.getExpires());
        Assert.assertEquals("registrationDomain", expectedRegistrationDomain, rebuilder.getRegistrationDomain());
        Assert.assertEquals("creatingUser", expectedCreatingUser, rebuilder.getCreatingUser());
        Assert.assertEquals("resources", expectedResources, rebuilder.getResources());
        Assert.assertEquals("healthCheck", expectedHealthCheck, rebuilder.getHealthCheck());
        Assert.assertEquals("securityOpt", expectedSecurityOpt, rebuilder.getSecurityOpt());
        Assert.assertEquals("networkMode", expectedNetworkMode, rebuilder.getNetworkMode());
        Assert.assertEquals("metadata", expectedMetadata, rebuilder.getMetadata());
        Assert.assertEquals("addCapabilities", expectedAddCapabilities, rebuilder.getAddCapabilities());
        Assert.assertEquals("dropCapabilities", expectedDropCapabilities, rebuilder.getDropCapabilities());
        Assert.assertEquals("labels", expectedLabels, rebuilder.getLabels());
        // Check clone
        final Job.Builder cloned = builder.clone();
        Assert.assertEquals("name", expectedName, cloned.getName());
        Assert.assertEquals("version", expectedVersion, cloned.getVersion());
        Assert.assertEquals("image", expectedImage, cloned.getImage());
        Assert.assertEquals("hostname", expectedHostname, cloned.getHostname());
        Assert.assertEquals("command", expectedCommand, cloned.getCommand());
        Assert.assertEquals("env", expectedEnv, cloned.getEnv());
        Assert.assertEquals("ports", expectedPorts, cloned.getPorts());
        Assert.assertEquals("registration", expectedRegistration, cloned.getRegistration());
        Assert.assertEquals("gracePeriod", expectedGracePeriod, cloned.getGracePeriod());
        Assert.assertEquals("volumes", expectedVolumes, cloned.getVolumes());
        Assert.assertEquals("expires", expectedExpires, cloned.getExpires());
        Assert.assertEquals("registrationDomain", expectedRegistrationDomain, cloned.getRegistrationDomain());
        Assert.assertEquals("creatingUser", expectedCreatingUser, cloned.getCreatingUser());
        Assert.assertEquals("resources", expectedResources, cloned.getResources());
        Assert.assertEquals("healthCheck", expectedHealthCheck, cloned.getHealthCheck());
        Assert.assertEquals("securityOpt", expectedSecurityOpt, cloned.getSecurityOpt());
        Assert.assertEquals("networkMode", expectedNetworkMode, cloned.getNetworkMode());
        Assert.assertEquals("metadata", expectedMetadata, cloned.getMetadata());
        Assert.assertEquals("addCapabilities", expectedAddCapabilities, cloned.getAddCapabilities());
        Assert.assertEquals("dropCapabilities", expectedDropCapabilities, cloned.getDropCapabilities());
        Assert.assertEquals("labels", expectedLabels, cloned.getLabels());
        final Job clonedJob = cloned.build();
        Assert.assertEquals("name", expectedName, clonedJob.getId().getName());
        Assert.assertEquals("version", expectedVersion, clonedJob.getId().getVersion());
        Assert.assertEquals("image", expectedImage, clonedJob.getImage());
        Assert.assertEquals("hostname", expectedHostname, clonedJob.getHostname());
        Assert.assertEquals("command", expectedCommand, clonedJob.getCommand());
        Assert.assertEquals("env", expectedEnv, clonedJob.getEnv());
        Assert.assertEquals("ports", expectedPorts, clonedJob.getPorts());
        Assert.assertEquals("registration", expectedRegistration, clonedJob.getRegistration());
        Assert.assertEquals("gracePeriod", expectedGracePeriod, clonedJob.getGracePeriod());
        Assert.assertEquals("volumes", expectedVolumes, clonedJob.getVolumes());
        Assert.assertEquals("expires", expectedExpires, clonedJob.getExpires());
        Assert.assertEquals("registrationDomain", expectedRegistrationDomain, clonedJob.getRegistrationDomain());
        Assert.assertEquals("creatingUser", expectedCreatingUser, clonedJob.getCreatingUser());
        Assert.assertEquals("resources", expectedResources, clonedJob.getResources());
        Assert.assertEquals("healthCheck", expectedHealthCheck, clonedJob.getHealthCheck());
        Assert.assertEquals("securityOpt", expectedSecurityOpt, clonedJob.getSecurityOpt());
        Assert.assertEquals("networkMode", expectedNetworkMode, clonedJob.getNetworkMode());
        Assert.assertEquals("metadata", expectedMetadata, clonedJob.getMetadata());
        Assert.assertEquals("addCapabilities", expectedAddCapabilities, clonedJob.getAddCapabilities());
        Assert.assertEquals("dropCapabilities", expectedDropCapabilities, clonedJob.getDropCapabilities());
        Assert.assertEquals("labels", expectedLabels, clonedJob.getLabels());
    }

    /**
     * Verify the Builder allows calling addFoo() before setFoo() for collection types.
     */
    @Test
    public void testBuilderAddBeforeSet() throws Exception {
        final Job job = Job.newBuilder().addEnv("env", "var").addMetadata("meta", "data").addPort("http", PortMapping.of(80, 8000)).addRegistration(ServiceEndpoint.of("foo", "http"), ServicePorts.of("http")).addVolume("/foo", "/bar").build();
        Assert.assertThat(job.getEnv(), Matchers.hasEntry("env", "var"));
        Assert.assertThat(job.getMetadata(), Matchers.hasEntry("meta", "data"));
        Assert.assertThat(job.getPorts(), Matchers.hasEntry("http", PortMapping.of(80, 8000)));
        Assert.assertThat(job.getRegistration(), Matchers.hasEntry(ServiceEndpoint.of("foo", "http"), ServicePorts.of("http")));
        Assert.assertThat(job.getVolumes(), Matchers.hasEntry("/foo", "/bar"));
    }

    @Test
    public void verifySha1Id() throws IOException {
        final Map<String, Object> expectedConfig = map("command", Arrays.asList("foo", "bar"), "image", "foobar:4711", "name", "foozbarz", "version", "17");
        final String expectedInput = "foozbarz:17:" + (hex(Json.sha1digest(expectedConfig)));
        final String expectedDigest = hex(Hash.sha1digest(expectedInput.getBytes(Charsets.UTF_8)));
        final JobId expectedId = JobId.fromString(("foozbarz:17:" + expectedDigest));
        final Job job = Job.newBuilder().setCommand(Arrays.asList("foo", "bar")).setImage("foobar:4711").setName("foozbarz").setVersion("17").build();
        Assert.assertEquals(expectedId, job.getId());
    }

    @Test
    public void verifySha1IdWithEnv() throws IOException {
        final Map<String, String> env = ImmutableMap.of("FOO", "BAR");
        final Map<String, Object> expectedConfig = map("command", Arrays.asList("foo", "bar"), "image", "foobar:4711", "name", "foozbarz", "version", "17", "env", env);
        final String expectedInput = "foozbarz:17:" + (hex(Json.sha1digest(expectedConfig)));
        final String expectedDigest = hex(Hash.sha1digest(expectedInput.getBytes(Charsets.UTF_8)));
        final JobId expectedId = JobId.fromString(("foozbarz:17:" + expectedDigest));
        final Job job = Job.newBuilder().setCommand(Arrays.asList("foo", "bar")).setImage("foobar:4711").setName("foozbarz").setVersion("17").setEnv(env).build();
        Assert.assertEquals(expectedId, job.getId());
    }

    @Test
    public void verifyCanParseJobWithUnknownFields() throws Exception {
        final Job job = Job.newBuilder().setCommand(Arrays.asList("foo", "bar")).setImage("foobar:4711").setName("foozbarz").setVersion("17").build();
        final String jobJson = job.toJsonString();
        final ObjectMapper objectMapper = new ObjectMapper();
        final Map<String, Object> fields = objectMapper.readValue(jobJson, new TypeReference<Map<String, Object>>() {});
        fields.put("UNKNOWN_FIELD", "FOOBAR");
        final String modifiedJobJson = objectMapper.writeValueAsString(fields);
        final Job parsedJob = Descriptor.parse(modifiedJobJson, Job.class);
        Assert.assertEquals(job, parsedJob);
    }

    @Test
    public void verifyCanParseJobWithMissingEnv() throws Exception {
        final Job job = Job.newBuilder().setCommand(Arrays.asList("foo", "bar")).setImage("foobar:4711").setName("foozbarz").setVersion("17").build();
        JobTest.removeFieldAndParse(job, "env");
    }

    @Test
    public void verifyCanParseJobWithMissingMetadata() throws Exception {
        final Job job = Job.newBuilder().setCommand(Arrays.asList("foo", "bar")).setImage("foobar:4711").setName("foozbarz").setVersion("17").build();
        JobTest.removeFieldAndParse(job, "metadata");
    }

    @Test
    public void verifyJobIsImmutable() {
        final List<String> expectedCommand = ImmutableList.of("foo");
        final Map<String, String> expectedEnv = ImmutableMap.of("e1", "1");
        final Map<String, String> expectedMetadata = ImmutableMap.of("foo", "bar");
        final Map<String, PortMapping> expectedPorts = ImmutableMap.of("p1", PortMapping.of(1, 2));
        final Map<ServiceEndpoint, ServicePorts> expectedRegistration = ImmutableMap.of(ServiceEndpoint.of("foo", "tcp"), ServicePorts.of("p1"));
        final Integer expectedGracePeriod = 240;
        final List<String> mutableCommand = Lists.newArrayList(expectedCommand);
        final Map<String, String> mutableEnv = Maps.newHashMap(expectedEnv);
        final Map<String, String> mutableMetadata = Maps.newHashMap(expectedMetadata);
        final Map<String, PortMapping> mutablePorts = Maps.newHashMap(expectedPorts);
        final Map<ServiceEndpoint, ServicePorts> mutableRegistration = Maps.newHashMap(expectedRegistration);
        final Job.Builder builder = Job.newBuilder().setCommand(mutableCommand).setEnv(mutableEnv).setMetadata(mutableMetadata).setPorts(mutablePorts).setImage("foobar:4711").setName("foozbarz").setVersion("17").setRegistration(mutableRegistration).setGracePeriod(expectedGracePeriod);
        final Job job = builder.build();
        mutableCommand.add("bar");
        mutableEnv.put("e2", "2");
        mutableMetadata.put("some", "thing");
        mutablePorts.put("p2", PortMapping.of(3, 4));
        mutableRegistration.put(ServiceEndpoint.of("bar", "udp"), ServicePorts.of("p2"));
        builder.addEnv("added_env", "FOO");
        builder.addMetadata("added", "data");
        builder.addPort("added_port", PortMapping.of(4711));
        builder.addRegistration(ServiceEndpoint.of("added_reg", "added_proto"), ServicePorts.of("added_port"));
        builder.setGracePeriod(480);
        Assert.assertEquals(expectedCommand, job.getCommand());
        Assert.assertEquals(expectedEnv, job.getEnv());
        Assert.assertEquals(expectedMetadata, job.getMetadata());
        Assert.assertEquals(expectedPorts, job.getPorts());
        Assert.assertEquals(expectedRegistration, job.getRegistration());
        Assert.assertEquals(expectedGracePeriod, job.getGracePeriod());
    }

    @Test
    public void testChangingPortTagsChangesJobHash() {
        final Job j = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").build();
        final Job.Builder builder = j.toBuilder();
        final Map<String, PortMapping> ports = ImmutableMap.of("add_ports1", PortMapping.of(1234), "add_ports2", PortMapping.of(2345));
        final ImmutableMap.Builder<String, ServicePortParameters> servicePortsBuilder = ImmutableMap.builder();
        servicePortsBuilder.put("add_ports1", new ServicePortParameters(ImmutableList.of("tag1", "tag2")));
        servicePortsBuilder.put("add_ports2", new ServicePortParameters(ImmutableList.of("tag3", "tag4")));
        final ServicePorts servicePorts = new ServicePorts(servicePortsBuilder.build());
        final Map<ServiceEndpoint, ServicePorts> oldRegistration = ImmutableMap.of(ServiceEndpoint.of("add_service", "add_proto"), servicePorts);
        final Job job = builder.setPorts(ports).setRegistration(oldRegistration).build();
        final ImmutableMap.Builder<String, ServicePortParameters> newServicePortsBuilder = ImmutableMap.builder();
        newServicePortsBuilder.put("add_ports1", new ServicePortParameters(ImmutableList.of("tag1", "newtag")));
        newServicePortsBuilder.put("add_ports2", new ServicePortParameters(ImmutableList.of("tag3", "tag4")));
        final ServicePorts newServicePorts = new ServicePorts(newServicePortsBuilder.build());
        final Map<ServiceEndpoint, ServicePorts> newRegistration = ImmutableMap.of(ServiceEndpoint.of("add_service", "add_proto"), newServicePorts);
        final Job newJob = builder.setRegistration(newRegistration).build();
        Assert.assertNotEquals(job.getId().getHash(), newJob.getId().getHash());
    }

    @Test
    public void testBuildWithoutHash() {
        final Job.Builder builder = Job.newBuilder().setCommand(Arrays.asList("foo", "bar")).setImage("foobar:4711").setName("foozbarz").setVersion("17");
        Assert.assertNull(builder.buildWithoutHash().getId().getHash());
        Assert.assertNotNull(builder.build().getId().getHash());
    }

    @Test
    public void testHasExternalPorts() {
        final Job.Builder builder = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar");
        final Job noPorts = builder.build();
        Assert.assertThat(noPorts.hasExternalPorts(), Matchers.equalTo(false));
        final Job noExternalPorts = builder.setPorts(ImmutableMap.of("add_ports1", PortMapping.of(1234), "add_ports2", PortMapping.of(2345))).build();
        Assert.assertThat(noExternalPorts.hasExternalPorts(), Matchers.equalTo(false));
        final Job externalPorts = builder.setPorts(ImmutableMap.of("add_ports1", PortMapping.of(1234), "add_ports2", PortMapping.of(2345, 9090))).build();
        Assert.assertThat(externalPorts.hasExternalPorts(), Matchers.equalTo(true));
    }
}

