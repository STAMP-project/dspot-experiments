/**
 * Copyright 2006-2018 the original author or authors.
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


import ExitStatus.COMPLETED;
import ExitStatus.EXECUTING;
import ExitStatus.FAILED;
import ExitStatus.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationUtils;

import static ExitStatus.COMPLETED;
import static ExitStatus.EXECUTING;


/**
 *
 *
 * @author Dave Syer
 * @author Mahmoud Ben Hassine
 */
public class ExitStatusTests {
    @Test
    public void testExitStatusNullDescription() {
        ExitStatus status = new ExitStatus("10", null);
        Assert.assertEquals("", status.getExitDescription());
    }

    @Test
    public void testExitStatusBooleanInt() {
        ExitStatus status = new ExitStatus("10");
        Assert.assertEquals("10", status.getExitCode());
    }

    @Test
    public void testExitStatusConstantsContinuable() {
        ExitStatus status = EXECUTING;
        Assert.assertEquals("EXECUTING", status.getExitCode());
    }

    @Test
    public void testExitStatusConstantsFinished() {
        ExitStatus status = COMPLETED;
        Assert.assertEquals("COMPLETED", status.getExitCode());
    }

    /**
     * Test equality of exit statuses.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEqualsWithSameProperties() throws Exception {
        Assert.assertEquals(EXECUTING, new ExitStatus("EXECUTING"));
    }

    @Test
    public void testEqualsSelf() {
        ExitStatus status = new ExitStatus("test");
        Assert.assertEquals(status, status);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new ExitStatus("test"), new ExitStatus("test"));
    }

    /**
     * Test equality of exit statuses.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEqualsWithNull() throws Exception {
        Assert.assertFalse(EXECUTING.equals(null));
    }

    /**
     * Test equality of exit statuses.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHashcode() throws Exception {
        Assert.assertEquals(EXECUTING.toString().hashCode(), EXECUTING.hashCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusStillExecutable() {
        Assert.assertEquals(EXECUTING.getExitCode(), EXECUTING.and(EXECUTING).getExitCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusWhenFinishedAddedToContinuable() {
        Assert.assertEquals(COMPLETED.getExitCode(), EXECUTING.and(COMPLETED).getExitCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusWhenContinuableAddedToFinished() {
        Assert.assertEquals(COMPLETED.getExitCode(), COMPLETED.and(EXECUTING).getExitCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusWhenCustomContinuableAddedToContinuable() {
        Assert.assertEquals("CUSTOM", EXECUTING.and(EXECUTING.replaceExitCode("CUSTOM")).getExitCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusWhenCustomCompletedAddedToCompleted() {
        Assert.assertEquals("COMPLETED_CUSTOM", COMPLETED.and(EXECUTING.replaceExitCode("COMPLETED_CUSTOM")).getExitCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusFailedPlusFinished() {
        Assert.assertEquals("FAILED", COMPLETED.and(FAILED).getExitCode());
        Assert.assertEquals("FAILED", FAILED.and(COMPLETED).getExitCode());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.ExitStatus#and(org.springframework.batch.core.ExitStatus)}
     * .
     */
    @Test
    public void testAndExitStatusWhenCustomContinuableAddedToFinished() {
        Assert.assertEquals("CUSTOM", COMPLETED.and(EXECUTING.replaceExitCode("CUSTOM")).getExitCode());
    }

    @Test
    public void testAddExitCode() throws Exception {
        ExitStatus status = EXECUTING.replaceExitCode("FOO");
        Assert.assertTrue(((EXECUTING) != status));
        Assert.assertEquals("FOO", status.getExitCode());
    }

    @Test
    public void testAddExitCodeToExistingStatus() throws Exception {
        ExitStatus status = EXECUTING.replaceExitCode("FOO").replaceExitCode("BAR");
        Assert.assertTrue(((EXECUTING) != status));
        Assert.assertEquals("BAR", status.getExitCode());
    }

    @Test
    public void testAddExitCodeToSameStatus() throws Exception {
        ExitStatus status = EXECUTING.replaceExitCode(EXECUTING.getExitCode());
        Assert.assertTrue(((EXECUTING) != status));
        Assert.assertEquals(EXECUTING.getExitCode(), status.getExitCode());
    }

    @Test
    public void testAddExitDescription() throws Exception {
        ExitStatus status = EXECUTING.addExitDescription("Foo");
        Assert.assertTrue(((EXECUTING) != status));
        Assert.assertEquals("Foo", status.getExitDescription());
    }

    @Test
    public void testAddExitDescriptionWIthStacktrace() throws Exception {
        ExitStatus status = EXECUTING.addExitDescription(new RuntimeException("Foo"));
        Assert.assertTrue(((EXECUTING) != status));
        String description = status.getExitDescription();
        Assert.assertTrue(("Wrong description: " + description), description.contains("Foo"));
        Assert.assertTrue(("Wrong description: " + description), description.contains("RuntimeException"));
    }

    @Test
    public void testAddExitDescriptionToSameStatus() throws Exception {
        ExitStatus status = EXECUTING.addExitDescription("Foo").addExitDescription("Foo");
        Assert.assertTrue(((EXECUTING) != status));
        Assert.assertEquals("Foo", status.getExitDescription());
    }

    @Test
    public void testAddEmptyExitDescription() throws Exception {
        ExitStatus status = EXECUTING.addExitDescription("Foo").addExitDescription(((String) (null)));
        Assert.assertEquals("Foo", status.getExitDescription());
    }

    @Test
    public void testAddExitCodeWithDescription() throws Exception {
        ExitStatus status = new ExitStatus("BAR", "Bar").replaceExitCode("FOO");
        Assert.assertEquals("FOO", status.getExitCode());
        Assert.assertEquals("Bar", status.getExitDescription());
    }

    @Test
    public void testUnknownIsRunning() throws Exception {
        Assert.assertTrue(UNKNOWN.isRunning());
    }

    @Test
    public void testSerializable() throws Exception {
        ExitStatus status = EXECUTING.replaceExitCode("FOO");
        byte[] bytes = SerializationUtils.serialize(status);
        Object object = SerializationUtils.deserialize(bytes);
        Assert.assertTrue((object instanceof ExitStatus));
        ExitStatus restored = ((ExitStatus) (object));
        Assert.assertEquals(status.getExitCode(), restored.getExitCode());
    }
}

