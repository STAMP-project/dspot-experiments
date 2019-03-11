/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.operations;


import java.io.IOException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class OperationsToolTest {
    @Test
    public void testHelp() throws IOException {
        Assert.assertThat(OperationsToolTest.run("--help"), Is.is(1));
    }

    @Test
    public void testList() throws IOException {
        Assert.assertThat(OperationsToolTest.run("--cluster http://example.com:1234/watman list"), Is.is(0));
    }

    @Test
    public void testListWithoutCluster() throws IOException {
        Assert.assertThat(OperationsToolTest.run("list"), Is.is(1));
    }

    @Test
    public void testCreate() {
        Assert.assertThat(OperationsToolTest.run("create --config file.xml"), Is.is(0));
    }

    @Test
    public void testDryRunCreate() {
        Assert.assertThat(OperationsToolTest.run("--dry-run create --config file.xml"), Is.is(0));
    }

    @Test
    public void testOverridenCreate() {
        Assert.assertThat(OperationsToolTest.run("--cluster http://example.com:1234/watman create --config file.xml"), Is.is(0));
    }

    @Test
    public void testCreateWithMissingFile() {
        Assert.assertThat(OperationsToolTest.run("create"), Is.is(1));
    }

    @Test
    public void testCreateWithIllegalURI() {
        Assert.assertThat(OperationsToolTest.run("--cluster ### create --config file.xml"), Is.is(1));
    }

    @Test
    public void testDestroy() {
        Assert.assertThat(OperationsToolTest.run("destroy --config file.xml"), Is.is(0));
    }

    @Test
    public void testDryRunDestroy() {
        Assert.assertThat(OperationsToolTest.run("--dry-run destroy --config file.xml"), Is.is(0));
    }

    @Test
    public void testOverridenDestroy() {
        Assert.assertThat(OperationsToolTest.run("--cluster http://example.com:1234/watman destroy --config file.xml"), Is.is(0));
    }

    @Test
    public void testNonMatchingDestroy() {
        Assert.assertThat(OperationsToolTest.run("destroy --config file.xml --match false"), Is.is(0));
    }

    @Test
    public void testDestroyWithMissingFile() {
        Assert.assertThat(OperationsToolTest.run("destroy"), Is.is(1));
    }

    @Test
    public void testDestroyWithIllegalURI() {
        Assert.assertThat(OperationsToolTest.run("--cluster ### destroy --config file.xml"), Is.is(1));
    }

    @Test
    public void testUpdate() {
        Assert.assertThat(OperationsToolTest.run("update --config file.xml"), Is.is(0));
    }

    @Test
    public void testDryRunUpdate() {
        Assert.assertThat(OperationsToolTest.run("--dry-run update --config file.xml"), Is.is(0));
    }

    @Test
    public void testOverridenUpdate() {
        Assert.assertThat(OperationsToolTest.run("--cluster http://example.com:1234/watman update --config file.xml"), Is.is(0));
    }

    @Test
    public void testUpdateWithDeletions() {
        Assert.assertThat(OperationsToolTest.run("update --config file.xml --allow-destroy"), Is.is(0));
    }

    @Test
    public void testUpdateWithMutations() {
        Assert.assertThat(OperationsToolTest.run("update --config file.xml --allow-mutation"), Is.is(0));
    }

    @Test
    public void testUpdateWithMissingFile() {
        Assert.assertThat(OperationsToolTest.run("update"), Is.is(1));
    }

    @Test
    public void testUpdateWithIllegalURI() {
        Assert.assertThat(OperationsToolTest.run("--cluster ### update --config file.xml"), Is.is(1));
    }
}

