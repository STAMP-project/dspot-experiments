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
package com.spotify.helios.common;


import Job.Builder;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.spotify.helios.common.descriptors.HealthCheck;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.PortMapping;
import com.spotify.helios.common.descriptors.ServiceEndpoint;
import com.spotify.helios.common.descriptors.ServicePortParameters;
import com.spotify.helios.common.descriptors.ServicePorts;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class JobValidatorTest {
    private static final HealthCheck HEALTH_CHECK = HealthCheck.newHttpHealthCheck().setPath("/").setPort("1").build();

    private static final Job VALID_JOB = Job.newBuilder().setName("foo").setVersion("1").setImage("bar").setHostname("baz").setEnv(ImmutableMap.of("FOO", "BAR")).setPorts(ImmutableMap.of("1", PortMapping.of(1, 1), "2", PortMapping.of(2, 2))).setHealthCheck(JobValidatorTest.HEALTH_CHECK).build();

    private final JobValidator validator = new JobValidator(true, true);

    @Test
    public void testValidJobPasses() {
        Assert.assertThat(validator.validate(JobValidatorTest.VALID_JOB), Is.is(Matchers.empty()));
    }

    @Test
    public void testValidNamesPass() {
        final Job.Builder b = Job.newBuilder().setVersion("1").setImage("bar");
        Assert.assertThat(validator.validate(b.setName("foo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setName("17").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setName("foo17.bar-baz_quux").build()), Is.is(Matchers.empty()));
    }

    @Test
    public void testValidVersionsPass() {
        final Job.Builder b = Job.newBuilder().setName("foo").setImage("bar");
        Assert.assertThat(validator.validate(b.setVersion("foo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setVersion("17").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setVersion("foo17.bar-baz_quux").build()), Is.is(Matchers.empty()));
    }

    @Test
    public void testValidImagePasses() {
        final Job.Builder b = Job.newBuilder().setName("foo").setVersion("1");
        Assert.assertThat(validator.validate(b.setImage("repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("namespace/repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("namespace/repo:tag").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("namespace/repo:1.2").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("reg.istry:4711/repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("reg.istry.:4711/repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("reg.istry:4711/namespace/repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("reg.istry.:4711/namespace/repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("1.2.3.4:4711/namespace/repo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("registry.test.net:80/fooo/bar").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("registry.test.net.:80/fooo/bar").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("namespace/foo@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("foo.net/bar@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage("foo@tarsum.v1+sha256:6c3c624b58dbbcd3c0dd82b4c53f04194d1247c6eebdaab7c610cf7d66709b3b").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage(("baz.io/foo/foo-bar:1.2.3-SNAPSHOT-20170830T143321-decafbad" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setImage(("baz.io:4711/foo/foo-bar:1.2.3-SNAPSHOT-20170830T143321-decafbad" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()), Is.is(Matchers.empty()));
    }

    @Test
    public void testValidHostnamesPass() {
        final Job.Builder b = Job.newBuilder().setName("foo").setVersion("1").setImage("bar");
        Assert.assertThat(validator.validate(b.setHostname("foo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setHostname("17").build()), Is.is(Matchers.empty()));
        // 63 chars
        Assert.assertThat(validator.validate(b.setHostname(((Strings.repeat("hostname", 7)) + "hostnam")).build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setHostname("a").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(b.setHostname("foo17bar-baz-quux").build()), Is.is(Matchers.empty()));
    }

    @Test
    public void testValidVolumesPass() {
        final Job j = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").build();
        Assert.assertThat(validator.validate(j.toBuilder().addVolume("/foo", "bar").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(j.toBuilder().addVolume("/foo").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(j.toBuilder().addVolume("/foo", "/").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(j.toBuilder().addVolume("/foo:ro", "/").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(j.toBuilder().addVolume("/foo", "/bar").build()), Is.is(Matchers.empty()));
        Assert.assertThat(validator.validate(j.toBuilder().addVolume("/foo:ro", "/bar").build()), Is.is(Matchers.empty()));
    }

    @Test
    public void testValidPortTagsPass() {
        final Job j = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").build();
        final Job.Builder builder = j.toBuilder();
        final Map<String, PortMapping> ports = ImmutableMap.of("add_ports1", PortMapping.of(1234), "add_ports2", PortMapping.of(2345));
        final ImmutableMap.Builder<String, ServicePortParameters> servicePortsBuilder = ImmutableMap.builder();
        servicePortsBuilder.put("add_ports1", new ServicePortParameters(ImmutableList.of("tag1", "tag2")));
        servicePortsBuilder.put("add_ports2", new ServicePortParameters(ImmutableList.of("tag3", "tag4")));
        final ServicePorts servicePorts = new ServicePorts(servicePortsBuilder.build());
        final Map<ServiceEndpoint, ServicePorts> addRegistration = ImmutableMap.of(ServiceEndpoint.of("add_service", "add_proto"), servicePorts);
        builder.setPorts(ports).setRegistration(addRegistration);
        Assert.assertThat(validator.validate(builder.build()), Is.is(Matchers.empty()));
    }

    @Test
    public void testPortMappingCollisionFails() throws Exception {
        final Job job = Job.newBuilder().setName("foo").setVersion("1").setImage("bar").setPorts(ImmutableMap.of("1", PortMapping.of(1, 1), "2", PortMapping.of(2, 1))).build();
        Assert.assertEquals(ImmutableSet.of("Duplicate external port mapping: 1"), validator.validate(job));
    }

    @Test
    public void testIdMismatchFails() throws Exception {
        final Job job = new Job(JobId.fromString("foo:bar:badf00d"), "bar", Job.EMPTY_HOSTNAME, Job.EMPTY_CREATED, Job.EMPTY_COMMAND, Job.EMPTY_ENV, Job.EMPTY_RESOURCES, Job.EMPTY_PORTS, Job.EMPTY_REGISTRATION, Job.EMPTY_GRACE_PERIOD, Job.EMPTY_VOLUMES, Job.EMPTY_EXPIRES, Job.EMPTY_REGISTRATION_DOMAIN, Job.EMPTY_CREATING_USER, Job.EMPTY_TOKEN, Job.EMPTY_HEALTH_CHECK, Job.EMPTY_SECURITY_OPT, Job.DEFAULT_NETWORK_MODE, Job.EMPTY_METADATA, Job.EMPTY_CAPS, Job.EMPTY_CAPS, Job.EMPTY_LABELS, Job.EMPTY_SECONDS_TO_WAIT, Job.EMPTY_RAMDISKS, Job.EMPTY_ROLLOUT_OPTIONS, Job.EMPTY_RUNTIME);
        final JobId recomputedId = job.toBuilder().build().getId();
        Assert.assertEquals(ImmutableSet.of(((("Id hash mismatch: " + (job.getId().getHash())) + " != ") + (recomputedId.getHash()))), validator.validate(job));
    }

    @Test
    public void testInvalidNamesFail() throws Exception {
        final Job.Builder b = Job.newBuilder().setVersion("1").setImage("foo");
        Assert.assertEquals(Sets.newHashSet("Job name was not specified.", "Job hash was not specified in job id [null:1]."), validator.validate(b.build()));
        Assert.assertThat(validator.validate(b.setName("foo@bar").build()), Matchers.contains(Matchers.equalTo("Job name may only contain [0-9a-zA-Z-_.] in job name [foo@bar].")));
        Assert.assertThat(validator.validate(b.setName("foo&bar").build()), Matchers.contains(Matchers.equalTo("Job name may only contain [0-9a-zA-Z-_.] in job name [foo&bar].")));
    }

    @Test
    public void testInvalidVersionsFail() throws Exception {
        final Job.Builder b = Job.newBuilder().setName("foo").setImage("foo");
        Assert.assertEquals(Sets.newHashSet("Job version was not specified in job id [foo:null].", "Job hash was not specified in job id [foo:null]."), validator.validate(b.build()));
        Assert.assertThat(validator.validate(b.setVersion("17@bar").build()), Matchers.contains(Matchers.equalTo(("Job version may only contain [0-9a-zA-Z-_.] " + "in job version [17@bar]."))));
        Assert.assertThat(validator.validate(b.setVersion("17&bar").build()), Matchers.contains(Matchers.equalTo(("Job version may only contain [0-9a-zA-Z-_.] " + "in job version [17&bar]."))));
    }

    @Test
    public void testInvalidImagesFail() throws Exception {
        final Job.Builder b = Job.newBuilder().setName("foo").setVersion("1");
        Assert.assertEquals(Sets.newHashSet("Tag cannot be empty"), validator.validate(b.setImage("repo:").build()));
        Assert.assertEquals(Sets.newHashSet("Tag cannot be empty"), validator.validate(b.setImage("repo:").build()));
        Assert.assertEquals(Sets.newHashSet("Tag cannot be empty"), validator.validate(b.setImage(("repo:" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet("Digest cannot be empty"), validator.validate(b.setImage("foo@").build()));
        Assert.assertEquals(Sets.newHashSet("Illegal digest: \":123\""), validator.validate(b.setImage("foo@:123").build()));
        Assert.assertEquals(Sets.newHashSet("Illegal digest: \":123\""), validator.validate(b.setImage("foo:bar@:123").build()));
        Assert.assertEquals(Sets.newHashSet("Illegal digest: \"sha256:\""), validator.validate(b.setImage("foo@sha256:").build()));
        Assert.assertEquals(Sets.newHashSet("Illegal digest: \"sha256:\""), validator.validate(b.setImage("foo:bar@sha256:").build()));
        Assert.assertEquals(Sets.newHashSet("Illegal digest: \"sha256:\""), validator.validate(b.setImage("foo:4711/baz:bar@sha256:").build()));
        Assert.assertFalse(validator.validate(b.setImage("repo:/").build()).isEmpty());
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"1.2.3.4.\""), validator.validate(b.setImage("1.2.3.4.:4711/namespace/repo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \" reg.istry\""), validator.validate(b.setImage(" reg.istry:4711/repo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg .istry\""), validator.validate(b.setImage("reg .istry:4711/repo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg.istry \""), validator.validate(b.setImage("reg.istry :4711/repo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"1.2.3.4.\""), validator.validate(b.setImage(("1.2.3.4.:4711/namespace/repo" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"reg.istry: 4711\""), validator.validate(b.setImage("reg.istry: 4711/repo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"reg.istry:4711 \""), validator.validate(b.setImage("reg.istry:4711 /repo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"reg.istry:4711 \""), validator.validate(b.setImage(("reg.istry:4711 /repo" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid image name (reg.istry:4711/ repo), only ^([a-z0-9._-]+)$ is " + ("allowed for each slash-separated name component " + "(failed on \" repo\")"))), validator.validate(b.setImage("reg.istry:4711/ repo").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid image name (reg.istry:4711/namespace /repo), only " + ("^([a-z0-9._-]+)$ is allowed for each slash-separated name component " + "(failed on \"namespace \")"))), validator.validate(b.setImage("reg.istry:4711/namespace /repo").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid image name (reg.istry:4711/namespace/ repo), only " + ("^([a-z0-9._-]+)$ is allowed for each slash-separated name component " + "(failed on \" repo\")"))), validator.validate(b.setImage("reg.istry:4711/namespace/ repo").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid image name (reg.istry:4711/namespace/repo ), only " + ("^([a-z0-9._-]+)$ is allowed for each slash-separated name component " + "(failed on \"repo \")"))), validator.validate(b.setImage("reg.istry:4711/namespace/repo ").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid image name (reg.istry:4711/namespace/ repo" + (("@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae), only " + "^([a-z0-9._-]+)$ is allowed for each slash-separated name component ") + "(failed on \" repo\")"))), validator.validate(b.setImage(("reg.istry:4711/namespace/ repo" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"foo-.ba|z\""), validator.validate(b.setImage("foo-.ba|z/namespace/baz").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg..istry\""), validator.validate(b.setImage("reg..istry/namespace/baz").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg..istry\""), validator.validate(b.setImage("reg..istry/namespace/baz").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg..istry\""), validator.validate(b.setImage("reg..istry/namespace/baz:foo").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg..istry\""), validator.validate(b.setImage(("reg..istry/namespace/baz:foo" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet("Invalid domain name: \"reg..istry\""), validator.validate(b.setImage(("reg..istry/namespace/baz" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"foo:345345345\""), validator.validate(b.setImage("foo:345345345/namespace/baz").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"foo:345345345\""), validator.validate(b.setImage("foo:345345345/namespace/baz:bar").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"foo:345345345\""), validator.validate(b.setImage(("foo:345345345/namespace/baz:bar" + "@sha256:2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae")).build()));
        Assert.assertEquals(Sets.newHashSet("Invalid port in endpoint: \"foo:-17\""), validator.validate(b.setImage("foo:-17/namespace/baz").build()));
        final String foos = Strings.repeat("foo", 100);
        final String image = foos + "/bar";
        Assert.assertEquals(Sets.newHashSet(((("Invalid image name (" + image) + "), repository name cannot be larger") + " than 255 characters")), validator.validate(b.setImage(image).build()));
    }

    @Test
    public void testInValidHostnamesFail() {
        final Job.Builder b = Job.newBuilder().setName("foo").setVersion("1").setImage("bar");
        // 64 chars
        final String toolonghostname = Strings.repeat("hostname", 8);
        Assert.assertEquals(Sets.newHashSet(((("Invalid hostname (" + toolonghostname) + "), ") + "only [a-z0-9][a-z0-9-] are allowed, size between 1 and 63")), validator.validate(b.setHostname(toolonghostname).build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid hostname (%/ RJU&%(=N/U), " + "only [a-z0-9][a-z0-9-] are allowed, size between 1 and 63")), validator.validate(b.setHostname("%/ RJU&%(=N/U").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid hostname (-), " + "only [a-z0-9][a-z0-9-] are allowed, size between 1 and 63")), validator.validate(b.setHostname("-").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid hostname (foo17.bar-baz_quux), " + "only [a-z0-9][a-z0-9-] are allowed, size between 1 and 63")), validator.validate(b.setHostname("foo17.bar-baz_quux").build()));
        Assert.assertEquals(Sets.newHashSet(("Invalid hostname (D34DB33F), " + "only [a-z0-9][a-z0-9-] are allowed, size between 1 and 63")), validator.validate(b.setHostname("D34DB33F").build()));
    }

    @Test
    public void testInvalidVolumesFail() {
        final Job j = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").build();
        Assert.assertEquals(Sets.newHashSet("Invalid volume path: /"), validator.validate(j.toBuilder().addVolume("/").build()));
        Assert.assertEquals(Sets.newHashSet("Invalid volume path: /foo:"), validator.validate(j.toBuilder().addVolume("/foo:", "/bar").build()));
        Assert.assertEquals(Sets.newHashSet("Volume path is not absolute: foo"), validator.validate(j.toBuilder().addVolume("foo").build()));
        Assert.assertEquals(Sets.newHashSet("Volume path is not absolute: foo"), validator.validate(j.toBuilder().addVolume("foo", "/bar").build()));
        Assert.assertEquals(Sets.newHashSet("Volume source is not absolute: bar/baz"), validator.validate(j.toBuilder().addVolume("/foo", "bar/baz").build()));
    }

    @Test
    public void testInvalidRamdiskMountPointFail() {
        final Job j = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").build();
        Assert.assertEquals(Sets.newHashSet("Ramdisk mount point is not absolute: relative-path"), validator.validate(j.toBuilder().addRamdisk("relative-path", "derp").build()));
    }

    @Test
    public void testConflictingVolumeAndRamdiskFail() {
        final Job job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").addRamdisk("/herp", "rw,size=1").addRamdisk("/a", "rw,size=1").addRamdisk("/derp", "rw,size=1").addVolume("/herp").addVolume("/a:ro", "/tmp").addVolume("/derp:rw").build();
        Assert.assertEquals(Sets.newHashSet("Ramdisk mount point used by volume: /herp", "Ramdisk mount point used by volume: /a", "Ramdisk mount point used by volume: /derp"), validator.validate(job));
    }

    @Test
    public void testInvalidHealthCheckFail() {
        final Job jobWithNoPorts = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setHealthCheck(JobValidatorTest.HEALTH_CHECK).build();
        Assert.assertEquals(1, validator.validate(jobWithNoPorts).size());
        final Job jobWithWrongPort = jobWithNoPorts.toBuilder().addPort("a", PortMapping.of(1, 1)).build();
        Assert.assertEquals(1, validator.validate(jobWithWrongPort).size());
    }

    @Test
    public void testValidNetworkModesPass() {
        Job job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setNetworkMode("bridge").build();
        Assert.assertEquals(0, validator.validate(job).size());
        job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setNetworkMode("host").build();
        Assert.assertEquals(0, validator.validate(job).size());
        job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setNetworkMode("container:foo").build();
        Assert.assertEquals(0, validator.validate(job).size());
    }

    @Test
    public void testInvalidNetworkModeFail() {
        final Job job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setNetworkMode("chocolate").build();
        Assert.assertEquals(1, validator.validate(job).size());
    }

    @Test
    public void testExpiry() {
        // make a date that's 24 hours behind
        final Date d = new Date(((System.currentTimeMillis()) - (86400 * 1000)));
        final Job j = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setExpires(d).build();
        Assert.assertThat(validator.validate(j), Matchers.contains(Matchers.startsWith("Job expires in the past")));
    }

    @Test
    public void testWhitelistedCapabilities_noWhitelist() throws Exception {
        final Job job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setAddCapabilities(ImmutableSet.of("cap1", "cap2")).build();
        Assert.assertEquals(1, validator.validate(job).size());
    }

    @Test
    public void testWhitelistedCapabilities_withWhitelist() throws Exception {
        final JobValidator validator = new JobValidator(true, true, ImmutableSet.of("cap1"));
        final Job job = Job.newBuilder().setName("foo").setVersion("1").setImage("foobar").setAddCapabilities(ImmutableSet.of("cap1", "cap2")).build();
        final Set<String> errors = validator.validate(job);
        Assert.assertEquals(1, errors.size());
        Assert.assertThat(errors.iterator().next(), Matchers.equalTo(("The following Linux capabilities aren't allowed by the Helios master: 'cap2'. " + "The allowed capabilities are: 'cap1'.")));
        final JobValidator validator2 = new JobValidator(true, true, ImmutableSet.of("cap1", "cap2"));
        final Set<String> errors2 = validator2.validate(job);
        Assert.assertEquals(0, errors2.size());
    }

    @Test
    public void testImageNamespaceWithHyphens() {
        final Job job = JobValidatorTest.VALID_JOB.toBuilder().setImage("b.gcr.io/cloudsql-docker/gce-proxy:1.05").build();
        Assert.assertThat(validator.validate(job), Is.is(Matchers.empty()));
    }

    @Test
    public void testImageNameWithManyNameComponents() {
        final Job job = JobValidatorTest.VALID_JOB.toBuilder().setImage("b.gcr.io/cloudsql-docker/and/more/components/gce-proxy:1.05").build();
        Assert.assertThat(validator.validate(job), Is.is(Matchers.empty()));
    }

    @Test
    public void testImageNameInvalidTag() {
        final Job job = JobValidatorTest.VALID_JOB.toBuilder().setImage("foo/bar:a b c").build();
        Assert.assertThat(validator.validate(job), Matchers.contains(Matchers.containsString("Illegal tag: \"a b c\"")));
    }

    /**
     * Tests that Jobs deserialized from JSON representation that happen to have malformed
     * "registration" sections are properly handled. This mimics a real life test case where the
     * "volumes" entry was accidentally indented wrong and included within the "registration"
     * section.
     */
    @Test
    public void testJobFromJsonWithInvalidRegistration() throws Exception {
        final URL resource = getClass().getResource("job-with-bad-registration.json");
        final byte[] bytes = Resources.toByteArray(resource);
        final Job job = Json.read(bytes, Job.class);
        Assert.assertThat(validator.validate(job), Matchers.contains("registration for 'volumes' is malformed: does not have a port mapping"));
    }

    /**
     * Tests that Jobs cannot have both grace period and external ports.
     */
    @Test
    public void testValidateGracePeriodAndExternalPorts() throws Exception {
        final Job noPorts = Json.read(stringFromResource("job-with-no-ports.json"), Job.class);
        Assert.assertThat(validator.validate(noPorts), Matchers.<String>empty());
        final Job noExternalPorts = Json.read(stringFromResource("job-with-no-external-ports.json"), Job.class);
        Assert.assertThat(validator.validate(noExternalPorts), Matchers.<String>empty());
        final Job externalPorts = Json.read(stringFromResource("job-with-external-ports.json"), Job.class);
        Assert.assertThat(validator.validate(externalPorts), Matchers.<String>empty());
        final Job gracePeriod = Json.read(stringFromResource("job-with-grace-period.json"), Job.class);
        Assert.assertThat(validator.validate(gracePeriod), Matchers.<String>empty());
        final Job externalPortsAndGracePeriod = Json.read(stringFromResource("job-with-external-ports-and-grace-period.json"), Job.class);
        Assert.assertThat(validator.validate(externalPortsAndGracePeriod), Matchers.hasItem(("Job has both grace period and static/external ports. External ports will " + ("prevent new containers from deploying during the gracePeriod because of port " + "conflicts. Please choose one or the other."))));
    }
}

