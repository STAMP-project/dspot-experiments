/**
 * Copyright 2006-2014 the original author or authors.
 *
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
 */
package org.springframework.batch.core;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationUtils;


/**
 *
 *
 * @author dsyer
 */
public class JobInstanceTests {
    private JobInstance instance = new JobInstance(new Long(11), "job");

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobInstance#getJobName()}.
     */
    @Test
    public void testGetName() {
        instance = new JobInstance(new Long(1), "foo");
        Assert.assertEquals("foo", instance.getJobName());
    }

    @Test
    public void testGetJob() {
        Assert.assertEquals("job", instance.getJobName());
    }

    @Test
    public void testCreateWithNulls() {
        try {
            new JobInstance(null, null);
            Assert.fail("job instance can't exist without job specified");
        } catch (IllegalArgumentException e) {
            // expected
        }
        instance = new JobInstance(null, "testJob");
        Assert.assertEquals("testJob", instance.getJobName());
    }

    @Test
    public void testSerialization() {
        instance = new JobInstance(new Long(1), "jobName");
        byte[] serialized = SerializationUtils.serialize(instance);
        Assert.assertEquals(instance, SerializationUtils.deserialize(serialized));
    }

    @Test
    public void testGetInstanceId() {
        Assert.assertEquals(11, instance.getInstanceId());
    }
}

