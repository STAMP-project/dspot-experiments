/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.building;


import Severity.ERROR;
import Severity.FATAL;
import Severity.WARNING;
import org.apache.maven.building.Problem.Severity;
import org.junit.Assert;
import org.junit.Test;


public class DefaultProblemTest {
    @Test
    public void testGetSeverity() {
        DefaultProblem problem = new DefaultProblem(null, null, null, (-1), (-1), null);
        Assert.assertEquals(ERROR, problem.getSeverity());
        problem = new DefaultProblem(null, Severity.FATAL, null, (-1), (-1), null);
        Assert.assertEquals(FATAL, problem.getSeverity());
        problem = new DefaultProblem(null, Severity.ERROR, null, (-1), (-1), null);
        Assert.assertEquals(ERROR, problem.getSeverity());
        problem = new DefaultProblem(null, Severity.WARNING, null, (-1), (-1), null);
        Assert.assertEquals(WARNING, problem.getSeverity());
    }

    @Test
    public void testGetLineNumber() {
        DefaultProblem problem = new DefaultProblem(null, null, null, (-1), (-1), null);
        Assert.assertEquals((-1), problem.getLineNumber());
        problem = new DefaultProblem(null, null, null, 42, (-1), null);
        Assert.assertEquals(42, problem.getLineNumber());
        problem = new DefaultProblem(null, null, null, Integer.MAX_VALUE, (-1), null);
        Assert.assertEquals(Integer.MAX_VALUE, problem.getLineNumber());
        // this case is not specified, might also return -1
        problem = new DefaultProblem(null, null, null, Integer.MIN_VALUE, (-1), null);
        Assert.assertEquals(Integer.MIN_VALUE, problem.getLineNumber());
    }

    @Test
    public void testGetColumnNumber() {
        DefaultProblem problem = new DefaultProblem(null, null, null, (-1), (-1), null);
        Assert.assertEquals((-1), problem.getColumnNumber());
        problem = new DefaultProblem(null, null, null, (-1), 42, null);
        Assert.assertEquals(42, problem.getColumnNumber());
        problem = new DefaultProblem(null, null, null, (-1), Integer.MAX_VALUE, null);
        Assert.assertEquals(Integer.MAX_VALUE, problem.getColumnNumber());
        // this case is not specified, might also return -1
        problem = new DefaultProblem(null, null, null, (-1), Integer.MIN_VALUE, null);
        Assert.assertEquals(Integer.MIN_VALUE, problem.getColumnNumber());
    }

    @Test
    public void testGetException() {
        DefaultProblem problem = new DefaultProblem(null, null, null, (-1), (-1), null);
        Assert.assertEquals(null, problem.getException());
        Exception e = new Exception();
        problem = new DefaultProblem(null, null, null, (-1), (-1), e);
        Assert.assertSame(e, problem.getException());
    }

    @Test
    public void testGetSource() {
        DefaultProblem problem = new DefaultProblem(null, null, null, (-1), (-1), null);
        Assert.assertEquals("", problem.getSource());
        problem = new DefaultProblem(null, null, "", (-1), (-1), null);
        Assert.assertEquals("", problem.getSource());
        problem = new DefaultProblem(null, null, "SOURCE", (-1), (-1), null);
        Assert.assertEquals("SOURCE", problem.getSource());
    }

    @Test
    public void testGetLocation() {
        DefaultProblem problem = new DefaultProblem(null, null, null, (-1), (-1), null);
        Assert.assertEquals("", problem.getLocation());
        problem = new DefaultProblem(null, null, "SOURCE", (-1), (-1), null);
        Assert.assertEquals("SOURCE", problem.getLocation());
        problem = new DefaultProblem(null, null, null, 42, (-1), null);
        Assert.assertEquals("line 42", problem.getLocation());
        problem = new DefaultProblem(null, null, null, (-1), 127, null);
        Assert.assertEquals("column 127", problem.getLocation());
        problem = new DefaultProblem(null, null, "SOURCE", 42, 127, null);
        Assert.assertEquals("SOURCE, line 42, column 127", problem.getLocation());
    }

    @Test
    public void testGetMessage() {
        DefaultProblem problem = new DefaultProblem("MESSAGE", null, null, (-1), (-1), null);
        Assert.assertEquals("MESSAGE", problem.getMessage());
        problem = new DefaultProblem(null, null, null, (-1), (-1), new Exception());
        Assert.assertEquals("", problem.getMessage());
        problem = new DefaultProblem(null, null, null, (-1), (-1), new Exception("EXCEPTION MESSAGE"));
        Assert.assertEquals("EXCEPTION MESSAGE", problem.getMessage());
    }
}

