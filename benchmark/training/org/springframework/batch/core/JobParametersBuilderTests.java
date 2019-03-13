/**
 * Copyright 2008-2018 the original author or authors.
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


import BatchStatus.FAILED;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.support.RunIdIncrementer;


/**
 *
 *
 * @author Lucas Ward
 * @author Michael Minella
 * @author Glenn Renfro
 * @author Mahmoud Ben Hassine
 */
public class JobParametersBuilderTests {
    private JobParametersBuilder parametersBuilder;

    private SimpleJob job;

    private JobExplorer jobExplorer;

    private List<JobInstance> jobInstanceList;

    private List<JobExecution> jobExecutionList;

    private Date date = new Date(System.currentTimeMillis());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAddingExistingJobParameters() {
        JobParameters params1 = new JobParametersBuilder().addString("foo", "bar").addString("bar", "baz").toJobParameters();
        JobParameters params2 = new JobParametersBuilder().addString("foo", "baz").toJobParameters();
        JobParameters finalParams = new JobParametersBuilder().addString("baz", "quix").addJobParameters(params1).addJobParameters(params2).toJobParameters();
        Assert.assertEquals(finalParams.getString("foo"), "baz");
        Assert.assertEquals(finalParams.getString("bar"), "baz");
        Assert.assertEquals(finalParams.getString("baz"), "quix");
    }

    @Test
    public void testNonIdentifyingParameters() {
        this.parametersBuilder.addDate("SCHEDULE_DATE", date, false);
        this.parametersBuilder.addLong("LONG", new Long(1), false);
        this.parametersBuilder.addString("STRING", "string value", false);
        this.parametersBuilder.addDouble("DOUBLE", new Double(1), false);
        JobParameters parameters = this.parametersBuilder.toJobParameters();
        Assert.assertEquals(date, parameters.getDate("SCHEDULE_DATE"));
        Assert.assertEquals(1L, parameters.getLong("LONG").longValue());
        Assert.assertEquals("string value", parameters.getString("STRING"));
        Assert.assertEquals(1, parameters.getDouble("DOUBLE").doubleValue(), 1.0E-15);
        Assert.assertFalse(parameters.getParameters().get("SCHEDULE_DATE").isIdentifying());
        Assert.assertFalse(parameters.getParameters().get("LONG").isIdentifying());
        Assert.assertFalse(parameters.getParameters().get("STRING").isIdentifying());
        Assert.assertFalse(parameters.getParameters().get("DOUBLE").isIdentifying());
    }

    @Test
    public void testToJobRuntimeParameters() {
        this.parametersBuilder.addDate("SCHEDULE_DATE", date);
        this.parametersBuilder.addLong("LONG", new Long(1));
        this.parametersBuilder.addString("STRING", "string value");
        this.parametersBuilder.addDouble("DOUBLE", new Double(1));
        JobParameters parameters = this.parametersBuilder.toJobParameters();
        Assert.assertEquals(date, parameters.getDate("SCHEDULE_DATE"));
        Assert.assertEquals(1L, parameters.getLong("LONG").longValue());
        Assert.assertEquals(1, parameters.getDouble("DOUBLE").doubleValue(), 1.0E-15);
        Assert.assertEquals("string value", parameters.getString("STRING"));
    }

    @Test
    public void testNullRuntimeParameters() {
        this.parametersBuilder.addDate("SCHEDULE_DATE", null);
        this.parametersBuilder.addLong("LONG", null);
        this.parametersBuilder.addString("STRING", null);
        this.parametersBuilder.addDouble("DOUBLE", null);
        JobParameters parameters = this.parametersBuilder.toJobParameters();
        Assert.assertNull(parameters.getDate("SCHEDULE_DATE"));
        Assert.assertNull(parameters.getLong("LONG"));
        Assert.assertNull(parameters.getString("STRING"));
        Assert.assertNull(parameters.getLong("DOUBLE"));
    }

    @Test
    public void testCopy() {
        this.parametersBuilder.addString("STRING", "string value");
        this.parametersBuilder = new JobParametersBuilder(this.parametersBuilder.toJobParameters());
        Iterator<String> parameters = this.parametersBuilder.toJobParameters().getParameters().keySet().iterator();
        Assert.assertEquals("STRING", parameters.next());
    }

    @Test
    public void testOrderedTypes() {
        this.parametersBuilder.addDate("SCHEDULE_DATE", date);
        this.parametersBuilder.addLong("LONG", new Long(1));
        this.parametersBuilder.addString("STRING", "string value");
        Iterator<String> parameters = this.parametersBuilder.toJobParameters().getParameters().keySet().iterator();
        Assert.assertEquals("SCHEDULE_DATE", parameters.next());
        Assert.assertEquals("LONG", parameters.next());
        Assert.assertEquals("STRING", parameters.next());
    }

    @Test
    public void testOrderedStrings() {
        this.parametersBuilder.addString("foo", "value foo");
        this.parametersBuilder.addString("bar", "value bar");
        this.parametersBuilder.addString("spam", "value spam");
        Iterator<String> parameters = this.parametersBuilder.toJobParameters().getParameters().keySet().iterator();
        Assert.assertEquals("foo", parameters.next());
        Assert.assertEquals("bar", parameters.next());
        Assert.assertEquals("spam", parameters.next());
    }

    @Test
    public void testAddJobParameter() {
        JobParameter jobParameter = new JobParameter("bar");
        this.parametersBuilder.addParameter("foo", jobParameter);
        Map<String, JobParameter> parameters = this.parametersBuilder.toJobParameters().getParameters();
        Assert.assertEquals(1, parameters.size());
        Assert.assertEquals("bar", parameters.get("foo").getValue());
    }

    @Test
    public void testProperties() {
        Properties props = new Properties();
        props.setProperty("SCHEDULE_DATE", "A DATE");
        props.setProperty("LONG", "1");
        props.setProperty("STRING", "string value");
        this.parametersBuilder = new JobParametersBuilder(props);
        JobParameters parameters = this.parametersBuilder.toJobParameters();
        Assert.assertEquals("A DATE", parameters.getString("SCHEDULE_DATE"));
        Assert.assertEquals("1", parameters.getString("LONG"));
        Assert.assertEquals("string value", parameters.getString("STRING"));
        Assert.assertFalse(parameters.getParameters().get("SCHEDULE_DATE").isIdentifying());
        Assert.assertFalse(parameters.getParameters().get("LONG").isIdentifying());
        Assert.assertFalse(parameters.getParameters().get("STRING").isIdentifying());
    }

    @Test
    public void testGetNextJobParametersFirstRun() {
        job.setJobParametersIncrementer(new RunIdIncrementer());
        initializeForNextJobParameters();
        this.parametersBuilder.getNextJobParameters(this.job);
        defaultNextJobParametersVerify(this.parametersBuilder.toJobParameters(), 4);
    }

    @Test
    public void testGetNextJobParametersNoIncrementer() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No job parameters incrementer found for job=simpleJob");
        initializeForNextJobParameters();
        this.parametersBuilder.getNextJobParameters(this.job);
    }

    @Test
    public void testGetNextJobParameters() {
        this.job.setJobParametersIncrementer(new RunIdIncrementer());
        this.jobInstanceList.add(new JobInstance(1L, "simpleJobInstance"));
        this.jobExecutionList.add(getJobExecution(this.jobInstanceList.get(0), null));
        Mockito.when(this.jobExplorer.getJobInstances("simpleJob", 0, 1)).thenReturn(this.jobInstanceList);
        Mockito.when(this.jobExplorer.getJobExecutions(ArgumentMatchers.any())).thenReturn(this.jobExecutionList);
        initializeForNextJobParameters();
        this.parametersBuilder.getNextJobParameters(this.job);
        defaultNextJobParametersVerify(this.parametersBuilder.toJobParameters(), 4);
    }

    @Test
    public void testGetNextJobParametersRestartable() {
        this.job.setRestartable(true);
        this.job.setJobParametersIncrementer(new RunIdIncrementer());
        this.jobInstanceList.add(new JobInstance(1L, "simpleJobInstance"));
        this.jobExecutionList.add(getJobExecution(this.jobInstanceList.get(0), FAILED));
        Mockito.when(this.jobExplorer.getJobInstances("simpleJob", 0, 1)).thenReturn(this.jobInstanceList);
        Mockito.when(this.jobExplorer.getJobExecutions(ArgumentMatchers.any())).thenReturn(this.jobExecutionList);
        initializeForNextJobParameters();
        this.parametersBuilder.addLong("NON_IDENTIFYING_LONG", new Long(1), false);
        this.parametersBuilder.getNextJobParameters(this.job);
        baseJobParametersVerify(this.parametersBuilder.toJobParameters(), 5);
    }

    @Test
    public void testGetNextJobParametersNoPreviousExecution() {
        this.job.setJobParametersIncrementer(new RunIdIncrementer());
        this.jobInstanceList.add(new JobInstance(1L, "simpleJobInstance"));
        Mockito.when(this.jobExplorer.getJobInstances("simpleJob", 0, 1)).thenReturn(this.jobInstanceList);
        Mockito.when(this.jobExplorer.getJobExecutions(ArgumentMatchers.any())).thenReturn(this.jobExecutionList);
        initializeForNextJobParameters();
        this.parametersBuilder.getNextJobParameters(this.job);
        baseJobParametersVerify(this.parametersBuilder.toJobParameters(), 4);
    }

    @Test(expected = IllegalStateException.class)
    public void testMissingJobExplorer() {
        this.parametersBuilder = new JobParametersBuilder();
        this.parametersBuilder.getNextJobParameters(this.job);
    }
}

