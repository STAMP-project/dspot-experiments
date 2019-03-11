/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.batch.core.jsr;


import JsrJobParametersConverter.JOB_RUN_ID;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;


public class JsrJobParametersConverterTests {
    private JsrJobParametersConverter converter;

    private static DataSource dataSource;

    @Test
    public void testNullJobParameters() {
        Properties props = converter.getProperties(((JobParameters) (null)));
        Assert.assertNotNull(props);
        Set<Map.Entry<Object, Object>> properties = props.entrySet();
        Assert.assertEquals(1, properties.size());
        Assert.assertTrue(props.containsKey(JOB_RUN_ID));
    }

    @Test
    public void testStringJobParameters() {
        JobParameters parameters = new JobParametersBuilder().addString("key", "value", false).toJobParameters();
        Properties props = converter.getProperties(parameters);
        Assert.assertNotNull(props);
        Set<Map.Entry<Object, Object>> properties = props.entrySet();
        Assert.assertEquals(2, properties.size());
        Assert.assertTrue(props.containsKey(JOB_RUN_ID));
        Assert.assertEquals("value", props.getProperty("key"));
    }

    @Test
    public void testNonStringJobParameters() {
        JobParameters parameters = new JobParametersBuilder().addLong("key", 5L, false).toJobParameters();
        Properties props = converter.getProperties(parameters);
        Assert.assertNotNull(props);
        Set<Map.Entry<Object, Object>> properties = props.entrySet();
        Assert.assertEquals(2, properties.size());
        Assert.assertTrue(props.containsKey(JOB_RUN_ID));
        Assert.assertEquals("5", props.getProperty("key"));
    }

    @Test
    public void testJobParametersWithRunId() {
        JobParameters parameters = new JobParametersBuilder().addLong("key", 5L, false).addLong(JOB_RUN_ID, 2L).toJobParameters();
        Properties props = converter.getProperties(parameters);
        Assert.assertNotNull(props);
        Set<Map.Entry<Object, Object>> properties = props.entrySet();
        Assert.assertEquals(2, properties.size());
        Assert.assertEquals("2", props.getProperty(JOB_RUN_ID));
        Assert.assertEquals("5", props.getProperty("key"));
    }

    @Test
    public void testNullProperties() {
        JobParameters parameters = converter.getJobParameters(((Properties) (null)));
        Assert.assertNotNull(parameters);
        Assert.assertEquals(1, parameters.getParameters().size());
        Assert.assertTrue(parameters.getParameters().containsKey(JOB_RUN_ID));
    }

    @Test
    public void testProperties() {
        Properties properties = new Properties();
        properties.put("key", "value");
        JobParameters parameters = converter.getJobParameters(properties);
        Assert.assertEquals(2, parameters.getParameters().size());
        Assert.assertEquals("value", parameters.getString("key"));
        Assert.assertTrue(parameters.getParameters().containsKey(JOB_RUN_ID));
    }

    @Test
    public void testPropertiesWithRunId() {
        Properties properties = new Properties();
        properties.put("key", "value");
        properties.put(JOB_RUN_ID, "3");
        JobParameters parameters = converter.getJobParameters(properties);
        Assert.assertEquals(2, parameters.getParameters().size());
        Assert.assertEquals("value", parameters.getString("key"));
        Assert.assertEquals(Long.valueOf(3L), parameters.getLong(JOB_RUN_ID));
        Assert.assertTrue(parameters.getParameters().get(JOB_RUN_ID).isIdentifying());
    }
}

