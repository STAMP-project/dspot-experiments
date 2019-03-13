/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.NotifyBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * CAMEL-5848
 */
public class FileConsumeDoneFileIssueTest extends ContextTestSupport {
    @Test
    public void testFileConsumeDoneFileIssue() throws Exception {
        NotifyBuilder notify = whenDone(5).create();
        template.sendBodyAndHeader("file:target/data/done", "A", FILE_NAME, "foo-a.txt");
        template.sendBodyAndHeader("file:target/data/done", "B", FILE_NAME, "foo-b.txt");
        template.sendBodyAndHeader("file:target/data/done", "C", FILE_NAME, "foo-c.txt");
        template.sendBodyAndHeader("file:target/data/done", "D", FILE_NAME, "foo-d.txt");
        template.sendBodyAndHeader("file:target/data/done", "E", FILE_NAME, "foo-e.txt");
        template.sendBodyAndHeader("file:target/data/done", "E", FILE_NAME, "foo.done");
        Assert.assertTrue("Done file should exists", new File("target/data/done/foo.done").exists());
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("A", "B", "C", "D", "E");
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(notify.matchesMockWaitTime());
        Thread.sleep(50);
        // the done file should be deleted
        Assert.assertFalse("Done file should be deleted", new File("target/data/done/foo.done").exists());
    }

    @Test
    public void testFileConsumeDynamicDoneFileName() throws Exception {
        NotifyBuilder notify = whenDone(3).create();
        template.sendBodyAndHeader("file:target/data/done2", "A", FILE_NAME, "a.txt");
        template.sendBodyAndHeader("file:target/data/done2", "B", FILE_NAME, "b.txt");
        template.sendBodyAndHeader("file:target/data/done2", "C", FILE_NAME, "c.txt");
        template.sendBodyAndHeader("file:target/data/done2", "a", FILE_NAME, "a.txt.done");
        template.sendBodyAndHeader("file:target/data/done2", "b", FILE_NAME, "b.txt.done");
        template.sendBodyAndHeader("file:target/data/done2", "c", FILE_NAME, "c.txt.done");
        Assert.assertTrue("Done file should exists", new File("target/data/done2/a.txt.done").exists());
        Assert.assertTrue("Done file should exists", new File("target/data/done2/b.txt.done").exists());
        Assert.assertTrue("Done file should exists", new File("target/data/done2/c.txt.done").exists());
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("A", "B", "C");
        context.getRouteController().startRoute("bar");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(notify.matchesMockWaitTime());
        Thread.sleep(50);
        // the done file should be deleted
        Assert.assertFalse("Done file should be deleted", new File("target/data/done2/a.txt.done").exists());
        Assert.assertFalse("Done file should be deleted", new File("target/data/done2/b.txt.done").exists());
        Assert.assertFalse("Done file should be deleted", new File("target/data/done2/c.txt.done").exists());
    }

    @Test
    public void testFileDoneFileNameContainingDollarSign() throws Exception {
        NotifyBuilder notify = whenDone(3).create();
        template.sendBodyAndHeader("file:target/data/done2", "A", FILE_NAME, "$a$.txt");
        template.sendBodyAndHeader("file:target/data/done2", "B", FILE_NAME, "$b.txt");
        template.sendBodyAndHeader("file:target/data/done2", "C", FILE_NAME, "c$.txt");
        template.sendBodyAndHeader("file:target/data/done2", "a", FILE_NAME, "$a$.txt.done");
        template.sendBodyAndHeader("file:target/data/done2", "b", FILE_NAME, "$b.txt.done");
        template.sendBodyAndHeader("file:target/data/done2", "c", FILE_NAME, "c$.txt.done");
        Assert.assertTrue("Done file should exists", new File("target/data/done2/$a$.txt.done").exists());
        Assert.assertTrue("Done file should exists", new File("target/data/done2/$b.txt.done").exists());
        Assert.assertTrue("Done file should exists", new File("target/data/done2/c$.txt.done").exists());
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("A", "B", "C");
        context.getRouteController().startRoute("bar");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(notify.matchesMockWaitTime());
        Thread.sleep(50);
        // the done file should be deleted
        Assert.assertFalse("Done file should be deleted", new File("target/data/done2/$a$.txt.done").exists());
        Assert.assertFalse("Done file should be deleted", new File("target/data/done2/$b.txt.done").exists());
        Assert.assertFalse("Done file should be deleted", new File("target/data/done2/c$.txt.done").exists());
    }
}

