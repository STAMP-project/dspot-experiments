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
package org.springframework.batch.core.scope;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class JobScopeDestructionCallbackIntegrationTests {
    @Autowired
    @Qualifier("proxied")
    private Job proxied;

    @Autowired
    @Qualifier("nested")
    private Job nested;

    @Autowired
    @Qualifier("ref")
    private Job ref;

    @Autowired
    @Qualifier("foo")
    private Collaborator foo;

    @Test
    public void testDisposableScopedProxy() throws Exception {
        Assert.assertNotNull(proxied);
        proxied.execute(new JobExecution(1L));
        Assert.assertEquals(1, StringUtils.countOccurrencesOf(TestDisposableCollaborator.message, "destroyed"));
    }

    @Test
    public void testDisposableInnerScopedProxy() throws Exception {
        Assert.assertNotNull(nested);
        nested.execute(new JobExecution(1L));
        Assert.assertEquals(1, StringUtils.countOccurrencesOf(TestDisposableCollaborator.message, "destroyed"));
    }

    @Test
    public void testProxiedScopedProxy() throws Exception {
        Assert.assertNotNull(nested);
        nested.execute(new JobExecution(1L));
        Assert.assertEquals(4, TestAdvice.names.size());
        Assert.assertEquals("bar", TestAdvice.names.get(0));
        Assert.assertEquals(1, StringUtils.countOccurrencesOf(TestDisposableCollaborator.message, "destroyed"));
    }

    @Test
    public void testRefScopedProxy() throws Exception {
        Assert.assertNotNull(ref);
        ref.execute(new JobExecution(1L));
        Assert.assertEquals(4, TestAdvice.names.size());
        Assert.assertEquals("spam", TestAdvice.names.get(0));
        Assert.assertEquals(2, StringUtils.countOccurrencesOf(TestDisposableCollaborator.message, "destroyed"));
        Assert.assertEquals(1, StringUtils.countOccurrencesOf(TestDisposableCollaborator.message, "bar:destroyed"));
        Assert.assertEquals(1, StringUtils.countOccurrencesOf(TestDisposableCollaborator.message, "spam:destroyed"));
    }

    @Test
    public void testProxiedNormalBean() throws Exception {
        Assert.assertNotNull(nested);
        String name = foo.getName();
        Assert.assertEquals(1, TestAdvice.names.size());
        Assert.assertEquals(name, TestAdvice.names.get(0));
    }
}

