/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.variables;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaString;


/**
 * Variables tests.
 *
 * @author Yury Bakhmutski
 * @see Variables
 */
public class VariablesTest {
    private Variables variables = new Variables();

    /**
     * Test for PDI-12893 issue.  Checks if an ConcurrentModificationException while iterating over the System properties
     * is occurred.
     */
    @Test
    public void testinItializeVariablesFrom() {
        final Variables variablesMock = Mockito.mock(Variables.class);
        Mockito.doCallRealMethod().when(variablesMock).initializeVariablesFrom(ArgumentMatchers.any(VariableSpace.class));
        @SuppressWarnings("unchecked")
        final Map<String, String> propertiesMock = Mockito.mock(Map.class);
        Mockito.when(variablesMock.getProperties()).thenReturn(propertiesMock);
        Mockito.doAnswer(new Answer<Map<String, String>>() {
            final String keyStub = "key";

            @Override
            public Map<String, String> answer(InvocationOnMock invocation) throws Throwable {
                if ((System.getProperty(keyStub)) == null) {
                    modifySystemproperties();
                }
                if ((invocation.getArguments()[1]) != null) {
                    propertiesMock.put(((String) (invocation.getArguments()[0])), System.getProperties().getProperty(((String) (invocation.getArguments()[1]))));
                }
                return propertiesMock;
            }
        }).when(propertiesMock).put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        variablesMock.initializeVariablesFrom(null);
    }

    /**
     * Spawns 20 threads that modify variables to test concurrent modification error fix.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConcurrentModification() throws Exception {
        int threads = 20;
        List<Callable<Boolean>> callables = new ArrayList<Callable<Boolean>>();
        for (int i = 0; i < threads; i++) {
            callables.add(newCallable());
        }
        // Assert threads ran successfully.
        for (Future<Boolean> result : Executors.newFixedThreadPool(5).invokeAll(callables)) {
            Assert.assertTrue(result.get());
        }
    }

    @Test
    public void testFieldSubstitution() throws KettleValueException {
        Object[] rowData = new Object[]{ "DataOne", "DataTwo" };
        RowMeta rm = new RowMeta();
        rm.addValueMeta(new ValueMetaString("FieldOne"));
        rm.addValueMeta(new ValueMetaString("FieldTwo"));
        Variables vars = new Variables();
        Assert.assertNull(vars.fieldSubstitute(null, rm, rowData));
        Assert.assertEquals("", vars.fieldSubstitute("", rm, rowData));
        Assert.assertEquals("DataOne", vars.fieldSubstitute("?{FieldOne}", rm, rowData));
        Assert.assertEquals("TheDataOne", vars.fieldSubstitute("The?{FieldOne}", rm, rowData));
    }

    @Test
    public void testEnvironmentSubstitute() {
        Variables vars = new Variables();
        vars.setVariable("VarOne", "DataOne");
        vars.setVariable("VarTwo", "DataTwo");
        Assert.assertNull(vars.environmentSubstitute(((String) (null))));
        Assert.assertEquals("", vars.environmentSubstitute(""));
        Assert.assertEquals("DataTwo", vars.environmentSubstitute("${VarTwo}"));
        Assert.assertEquals("DataTwoEnd", vars.environmentSubstitute("${VarTwo}End"));
        Assert.assertEquals(0, vars.environmentSubstitute(new String[0]).length);
        Assert.assertArrayEquals(new String[]{ "DataOne", "TheDataOne" }, vars.environmentSubstitute(new String[]{ "${VarOne}", "The${VarOne}" }));
    }
}

