/**
 * Copyright 2008-2013 the original author or authors.
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


import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lucas Ward
 */
public class JobParameterTests {
    JobParameter jobParameter;

    @Test
    public void testStringParameter() {
        jobParameter = new JobParameter("test", true);
        Assert.assertEquals("test", jobParameter.getValue());
    }

    @Test
    public void testNullStringParameter() {
        jobParameter = new JobParameter(((String) (null)), true);
        Assert.assertEquals(null, jobParameter.getValue());
    }

    @Test
    public void testLongParameter() {
        jobParameter = new JobParameter(1L, true);
        Assert.assertEquals(1L, jobParameter.getValue());
    }

    @Test
    public void testDoubleParameter() {
        jobParameter = new JobParameter(1.1, true);
        Assert.assertEquals(1.1, jobParameter.getValue());
    }

    @Test
    public void testDateParameter() {
        Date epoch = new Date(0L);
        jobParameter = new JobParameter(epoch, true);
        Assert.assertEquals(new Date(0L), jobParameter.getValue());
    }

    @Test
    public void testNullDateParameter() {
        jobParameter = new JobParameter(((Date) (null)), true);
        Assert.assertEquals(null, jobParameter.getValue());
    }

    @Test
    public void testDateParameterToString() {
        Date epoch = new Date(0L);
        jobParameter = new JobParameter(epoch, true);
        Assert.assertEquals("0", jobParameter.toString());
    }

    @Test
    public void testEquals() {
        jobParameter = new JobParameter("test", true);
        JobParameter testParameter = new JobParameter("test", true);
        Assert.assertTrue(jobParameter.equals(testParameter));
    }

    @Test
    public void testHashcode() {
        jobParameter = new JobParameter("test", true);
        JobParameter testParameter = new JobParameter("test", true);
        Assert.assertEquals(testParameter.hashCode(), jobParameter.hashCode());
    }

    @Test
    public void testEqualsWithNull() {
        jobParameter = new JobParameter(((String) (null)), true);
        JobParameter testParameter = new JobParameter(((String) (null)), true);
        Assert.assertTrue(jobParameter.equals(testParameter));
    }

    @Test
    public void testEqualsWithNullAndDifferentType() {
        jobParameter = new JobParameter(((String) (null)), true);
        JobParameter testParameter = new JobParameter(((Date) (null)), true);
        Assert.assertFalse(jobParameter.equals(testParameter));
    }

    @Test
    public void testHashcodeWithNull() {
        jobParameter = new JobParameter(((String) (null)), true);
        JobParameter testParameter = new JobParameter(((String) (null)), true);
        Assert.assertEquals(testParameter.hashCode(), jobParameter.hashCode());
    }
}

