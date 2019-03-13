/**
 * Copyright 2013 the original author or authors.
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
package org.springframework.batch.core.jsr.configuration.xml;


import ExitStatus.COMPLETED;
import java.io.Serializable;
import java.util.List;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.api.chunk.ItemReader;
import javax.batch.api.chunk.ItemWriter;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * <p>
 * Test cases for JSR-352 job property substitution.
 * </p>
 *
 * TODO: enhance test cases with more complex substitutions
 *
 * @author Chris Schaefer
 * @since 3.0
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class JobPropertySubstitutionTests {
    @Autowired
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @Test
    public void testPropertySubstitutionSimple() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder().addString("testParam", "testParamValue").addString("file.name.junit", "myfile2").toJobParameters());
        Assert.assertEquals(COMPLETED, jobExecution.getExitStatus());
    }

    public static final class TestItemReader implements ItemReader {
        private int cnt;

        @Inject
        @BatchProperty
        String readerPropertyName1;

        @Override
        public void open(Serializable serializable) throws Exception {
            Assert.assertEquals(System.getProperty("file.separator"), readerPropertyName1);
        }

        @Override
        public void close() throws Exception {
        }

        @Override
        public Object readItem() throws Exception {
            if ((cnt) == 0) {
                (cnt)++;
                return "blah";
            }
            return null;
        }

        @Override
        public Serializable checkpointInfo() throws Exception {
            return null;
        }
    }

    public static final class TestItemWriter implements ItemWriter {
        @Inject
        @BatchProperty
        String writerPropertyName1;

        @Override
        public void open(Serializable serializable) throws Exception {
            Assert.assertEquals("jobPropertyValue1", writerPropertyName1);
        }

        @Override
        public void close() throws Exception {
        }

        @Override
        public void writeItems(List<Object> objects) throws Exception {
            System.out.println(objects);
        }

        @Override
        public Serializable checkpointInfo() throws Exception {
            return null;
        }
    }

    public static final class TestItemProcessor implements ItemProcessor {
        @Inject
        @BatchProperty
        String processorProperty1;

        @Inject
        @BatchProperty
        String processorProperty2;

        @Inject
        @BatchProperty
        String processorProperty3;

        @Override
        public Object processItem(Object item) throws Exception {
            Assert.assertEquals("testParamValue", processorProperty1);
            Assert.assertEquals("myfile1.txt", processorProperty2);
            Assert.assertEquals(((System.getProperty("file.separator")) + "myfile2.txt"), processorProperty3);
            return item;
        }
    }
}

