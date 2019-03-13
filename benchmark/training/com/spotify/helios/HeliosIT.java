/**
 * -
 * -\-\-
 * Helios Integration Tests
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
package com.spotify.helios;


import CreateJobResponse.Status.OK;
import com.spotify.helios.common.protocol.CreateJobResponse;
import com.spotify.helios.common.protocol.JobDeleteResponse;
import com.spotify.helios.common.protocol.JobDeployResponse;
import com.spotify.helios.common.protocol.JobUndeployResponse;
import com.spotify.helios.testing.HeliosDeploymentResource;
import com.spotify.helios.testing.HeliosSoloDeployment;
import com.spotify.helios.testing.TemporaryJobs;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings("AbbreviationAsWordInName")
public class HeliosIT {
    @ClassRule
    public static HeliosDeploymentResource solo = new HeliosDeploymentResource(HeliosSoloDeployment.fromEnv().heliosSoloImage(Utils.soloImage()).checkForNewImages(false).removeHeliosSoloOnExit(false).logStreamProvider(null).env("REGISTRAR_HOST_FORMAT", "_${service}._${protocol}.test.${domain}").build());

    @Rule
    public TemporaryJobs temporaryJobs = TemporaryJobs.builder().client(HeliosIT.solo.client()).build();

    private static final String TEST_USER = "HeliosIT";

    private static final String TEST_HOST = "test-host";

    private String masterEndpoint;

    @Test
    public void test() throws Exception {
        final CreateJobResponse create = cli(CreateJobResponse.class, "create", "test:1", "spotify/busybox:latest");
        MatcherAssert.assertThat(create.getStatus(), Matchers.equalTo(OK));
        final JobDeployResponse deploy = cli(JobDeployResponse.class, "deploy", "test:1", HeliosIT.TEST_HOST);
        MatcherAssert.assertThat(deploy.getStatus(), Matchers.equalTo(JobDeployResponse.Status.OK));
        final JobUndeployResponse undeploy = cli(JobUndeployResponse.class, "undeploy", "--yes", "test:1", "-a");
        MatcherAssert.assertThat(undeploy.getStatus(), Matchers.equalTo(JobUndeployResponse.Status.OK));
        final JobDeleteResponse delete = cli(JobDeleteResponse.class, "remove", "--yes", "test:1");
        MatcherAssert.assertThat(delete.getStatus(), Matchers.equalTo(JobDeleteResponse.Status.OK));
    }
}

