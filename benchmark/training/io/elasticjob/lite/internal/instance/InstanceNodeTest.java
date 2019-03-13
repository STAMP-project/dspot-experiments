/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.internal.instance;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class InstanceNodeTest {
    private static InstanceNode instanceNode;

    @Test
    public void assertGetInstanceFullPath() {
        Assert.assertThat(InstanceNodeTest.instanceNode.getInstanceFullPath(), CoreMatchers.is("/test_job/instances"));
    }

    @Test
    public void assertIsInstancePath() {
        Assert.assertTrue(InstanceNodeTest.instanceNode.isInstancePath("/test_job/instances/127.0.0.1@-@0"));
    }

    @Test
    public void assertIsNotInstancePath() {
        Assert.assertFalse(InstanceNodeTest.instanceNode.isInstancePath("/test_job/other/127.0.0.1@-@0"));
    }

    @Test
    public void assertIsLocalInstancePath() {
        Assert.assertTrue(InstanceNodeTest.instanceNode.isLocalInstancePath("/test_job/instances/127.0.0.1@-@0"));
    }

    @Test
    public void assertIsNotLocalInstancePath() {
        Assert.assertFalse(InstanceNodeTest.instanceNode.isLocalInstancePath("/test_job/instances/127.0.0.2@-@0"));
    }

    @Test
    public void assertGetLocalInstancePath() {
        Assert.assertThat(InstanceNodeTest.instanceNode.getLocalInstanceNode(), CoreMatchers.is("instances/127.0.0.1@-@0"));
    }
}

