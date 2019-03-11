/**
 * Copyright 2006-2009 the original author or authors.
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
package org.springframework.batch.core.configuration.xml;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Dan Garrette
 * @author Dave Syer
 * @since 2.0
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class JobParserParentAttributeTests {
    @Autowired
    @Qualifier("listenerClearingJob")
    private Job listenerClearingJob;

    @Autowired
    @Qualifier("defaultRepoJob")
    private Job defaultRepoJob;

    @Autowired
    @Qualifier("specifiedRepoJob")
    private Job specifiedRepoJob;

    @Autowired
    @Qualifier("inheritSpecifiedRepoJob")
    private Job inheritSpecifiedRepoJob;

    @Autowired
    @Qualifier("overrideInheritedRepoJob")
    private Job overrideInheritedRepoJob;

    @Autowired
    @Qualifier("job3")
    private Job job3;

    @Autowired
    @Qualifier("job2")
    private Job job2;

    @Autowired
    @Qualifier("job1")
    private Job job1;

    @Test
    public void testInheritListeners() throws Exception {
        List<?> job1Listeners = getListeners(job1);
        Assert.assertEquals(2, job1Listeners.size());
        boolean a = false;
        boolean b = false;
        for (Object l : job1Listeners) {
            if (l instanceof DummyAnnotationJobExecutionListener) {
                a = true;
            } else
                if (l instanceof JobExecutionListenerSupport) {
                    b = true;
                }

        }
        Assert.assertTrue(a);
        Assert.assertTrue(b);
    }

    @Test
    public void testInheritListeners_NoMerge() throws Exception {
        List<?> job2Listeners = getListeners(job2);
        Assert.assertEquals(1, job2Listeners.size());
        boolean c = false;
        for (Object l : job2Listeners) {
            if (l instanceof JobExecutionListenerSupport) {
                c = true;
            }
        }
        Assert.assertTrue(c);
    }

    @Test
    public void testStandaloneListener() throws Exception {
        List<?> jobListeners = getListeners(job3);
        Assert.assertEquals(2, jobListeners.size());
        boolean a = false;
        boolean b = false;
        for (Object l : jobListeners) {
            if (l instanceof DummyAnnotationJobExecutionListener) {
                a = true;
            } else
                if (l instanceof JobExecutionListenerSupport) {
                    b = true;
                }

        }
        Assert.assertTrue(a);
        Assert.assertTrue(b);
    }

    @Test
    public void testJobRepositoryDefaults() throws Exception {
        Assert.assertTrue(((getJobRepository(defaultRepoJob)) instanceof SimpleJobRepository));
        Assert.assertTrue(((getJobRepository(specifiedRepoJob)) instanceof DummyJobRepository));
        Assert.assertTrue(((getJobRepository(inheritSpecifiedRepoJob)) instanceof DummyJobRepository));
        Assert.assertTrue(((getJobRepository(overrideInheritedRepoJob)) instanceof SimpleJobRepository));
    }

    @Test
    public void testListenerClearingJob() throws Exception {
        Assert.assertEquals(0, getListeners(listenerClearingJob).size());
    }
}

