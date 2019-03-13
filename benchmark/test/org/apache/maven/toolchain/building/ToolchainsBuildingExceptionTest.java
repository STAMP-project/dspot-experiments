/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.toolchain.building;


import Problem.Severity.ERROR;
import java.util.Collections;
import org.apache.maven.building.Problem;
import org.apache.maven.building.ProblemCollector;
import org.apache.maven.building.ProblemCollectorFactory;
import org.junit.Assert;
import org.junit.Test;


public class ToolchainsBuildingExceptionTest {
    private static final String LS = System.getProperty("line.separator");

    @Test
    public void testNoProblems() {
        ToolchainsBuildingException e = new ToolchainsBuildingException(Collections.<Problem>emptyList());
        Assert.assertEquals(("0 problems were encountered while building the effective toolchains" + (ToolchainsBuildingExceptionTest.LS)), e.getMessage());
    }

    @Test
    public void testOneProblem() {
        ProblemCollector problemCollector = ProblemCollectorFactory.newInstance(null);
        problemCollector.add(ERROR, "MESSAGE", 3, 5, new Exception());
        ToolchainsBuildingException e = new ToolchainsBuildingException(problemCollector.getProblems());
        Assert.assertEquals(((("1 problem was encountered while building the effective toolchains" + (ToolchainsBuildingExceptionTest.LS)) + "[ERROR] MESSAGE @ line 3, column 5") + (ToolchainsBuildingExceptionTest.LS)), e.getMessage());
    }

    @Test
    public void testUnknownPositionAndSource() {
        ProblemCollector problemCollector = ProblemCollectorFactory.newInstance(null);
        problemCollector.add(ERROR, "MESSAGE", (-1), (-1), new Exception());
        ToolchainsBuildingException e = new ToolchainsBuildingException(problemCollector.getProblems());
        Assert.assertEquals(((("1 problem was encountered while building the effective toolchains" + (ToolchainsBuildingExceptionTest.LS)) + "[ERROR] MESSAGE") + (ToolchainsBuildingExceptionTest.LS)), e.getMessage());
    }

    @Test
    public void testUnknownPosition() {
        ProblemCollector problemCollector = ProblemCollectorFactory.newInstance(null);
        problemCollector.setSource("SOURCE");
        problemCollector.add(ERROR, "MESSAGE", (-1), (-1), new Exception());
        ToolchainsBuildingException e = new ToolchainsBuildingException(problemCollector.getProblems());
        Assert.assertEquals(((("1 problem was encountered while building the effective toolchains" + (ToolchainsBuildingExceptionTest.LS)) + "[ERROR] MESSAGE @ SOURCE") + (ToolchainsBuildingExceptionTest.LS)), e.getMessage());
    }
}

