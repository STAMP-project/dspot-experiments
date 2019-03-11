/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.test;


import java.util.Iterator;
import java.util.List;
import org.apache.drill.common.exceptions.ExecutionSetupException;
import org.apache.drill.exec.physical.base.AbstractBase;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.base.PhysicalVisitor;
import org.junit.ComparisonFailure;
import org.junit.Test;


public class OperatorTestBuilderTest extends PhysicalOpUnitTestBase {
    public static final String FIRST_NAME_COL = "firstname";

    public static final String LAST_NAME_COL = "lastname";

    @Test
    public void noCombineUnorderedTestPass() throws Exception {
        executeTest(buildInputData(), true, false);
    }

    @Test(expected = AssertionError.class)
    public void noCombineUnorderedTestFail() throws Exception {
        executeTest(buildIncorrectData(), true, false);
    }

    @Test
    public void noCombineOrderedTestPass() throws Exception {
        executeTest(buildInputData(), false, false);
    }

    @Test(expected = ComparisonFailure.class)
    public void noCombineOrderedTestFail() throws Exception {
        executeTest(buildIncorrectData(), false, false);
    }

    @Test
    public void combineUnorderedTestPass() throws Exception {
        executeTest(buildInputData(), true, true);
    }

    @Test(expected = AssertionError.class)
    public void combineUnorderedTestFail() throws Exception {
        executeTest(buildIncorrectData(), true, true);
    }

    @Test
    public void combineOrderedTestPass() throws Exception {
        executeTest(buildInputData(), false, true);
    }

    @Test(expected = ComparisonFailure.class)
    public void combineOrderedTestFail() throws Exception {
        executeTest(buildIncorrectData(), false, true);
    }

    public static class MockPhysicalOperator extends AbstractBase {
        @Override
        public <T, X, E extends Throwable> T accept(PhysicalVisitor<T, X, E> physicalVisitor, X value) throws E {
            return null;
        }

        @Override
        public PhysicalOperator getNewWithChildren(List<PhysicalOperator> children) throws ExecutionSetupException {
            return null;
        }

        @Override
        public int getOperatorType() {
            return 0;
        }

        @Override
        public Iterator<PhysicalOperator> iterator() {
            return null;
        }
    }
}

