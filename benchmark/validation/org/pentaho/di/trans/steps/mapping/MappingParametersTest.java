/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.mapping;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.StepWithMappingMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;


public class MappingParametersTest {
    private Mapping step;

    private Trans trans;

    private TransMeta transMeta;

    /**
     * PDI-3064 Test parent transformation overrides parameters for child transformation.
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testOverrideMappingParameters() throws KettleException {
        MappingParameters param = Mockito.mock(MappingParameters.class);
        Mockito.when(param.getVariable()).thenReturn(new String[]{ "a", "b" });
        Mockito.when(param.getInputField()).thenReturn(new String[]{ "11", "12" });
        Mockito.when(transMeta.listParameters()).thenReturn(new String[]{ "a" });
        StepWithMappingMeta.activateParams(trans, trans, step, transMeta.listParameters(), param.getVariable(), param.getInputField());
        // parameters was overridden 2 times
        // new call of setParameterValue added in StepWithMappingMeta - wantedNumberOfInvocations is now to 2
        Mockito.verify(trans, Mockito.times(2)).setParameterValue(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(trans, Mockito.times(1)).setVariable(Mockito.anyString(), Mockito.anyString());
    }

    /**
     * Regression of PDI-3064 : keep correct 'inherit all variables' settings. This is a case for 'do not override'
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testDoNotOverrideMappingParametes() throws KettleException {
        prepareMappingParametesActions(false);
        Mockito.verify(transMeta, Mockito.never()).copyVariablesFrom(Mockito.any(VariableSpace.class));
    }
}

