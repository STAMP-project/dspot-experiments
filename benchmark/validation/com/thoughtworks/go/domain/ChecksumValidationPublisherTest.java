/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.domain;


import HttpServletResponse.SC_OK;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ChecksumValidationPublisherTest {
    private ChecksumValidationPublisher checksumValidationPublisher;

    private StubGoPublisher goPublisher;

    private File artifact;

    @Test
    public void testMessagesPublished_WhenMD5PropertyFileIsNotFoundOnServer() throws Exception {
        checksumValidationPublisher.md5ChecksumFileNotFound();
        checksumValidationPublisher.publish(SC_OK, artifact, goPublisher);
        Assert.assertThat(goPublisher.getMessage(), Matchers.not(Matchers.containsString(String.format("[WARN] The md5checksum value of the artifact [%s] was not found on the server. Hence, Go could not verify the integrity of its contents.", artifact))));
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("Saved artifact to [%s] without verifying the integrity of its contents.", artifact)));
    }

    @Test
    public void shouldThrowExceptionWhenMd5ValuesMismatch() {
        checksumValidationPublisher.md5Mismatch(artifact.getPath());
        try {
            checksumValidationPublisher.publish(SC_OK, artifact, goPublisher);
            Assert.fail("Should throw exception when checksums do not match.");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is(String.format("Artifact download failed for [%s]", artifact)));
            Assert.assertThat(goPublisher.getMessage(), Matchers.containsString(String.format("[ERROR] Verification of the integrity of the artifact [%s] failed. The artifact file on the server may have changed since its original upload.", artifact)));
        }
    }
}

