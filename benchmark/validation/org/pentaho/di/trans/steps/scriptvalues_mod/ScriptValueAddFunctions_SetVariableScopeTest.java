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
package org.pentaho.di.trans.steps.scriptvalues_mod;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;


public class ScriptValueAddFunctions_SetVariableScopeTest {
    private static final String VARIABLE_NAME = "variable-name";

    private static final String VARIABLE_VALUE = "variable-value";

    @Test
    public void setParentScopeVariable_ParentIsTrans() {
        Trans parent = createTrans();
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setParentScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setParentScopeVariable_ParentIsJob() {
        Job parent = createJob();
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setParentScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setParentScopeVariable_NoParent() {
        Trans trans = createTrans();
        ScriptValuesAddedFunctions.setParentScopeVariable(trans, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(trans).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setGrandParentScopeVariable_TwoLevelHierarchy() {
        Trans parent = createTrans();
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setGrandParentScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setGrandParentScopeVariable_ThreeLevelHierarchy() {
        Job grandParent = createJob();
        Trans parent = createTrans(grandParent);
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setGrandParentScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(grandParent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setGrandParentScopeVariable_FourLevelHierarchy() {
        Job grandGrandParent = createJob();
        Trans grandParent = createTrans(grandGrandParent);
        Trans parent = createTrans(grandParent);
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setGrandParentScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(grandParent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(grandGrandParent, Mockito.never()).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setGrandParentScopeVariable_NoParent() {
        Trans trans = createTrans();
        ScriptValuesAddedFunctions.setGrandParentScopeVariable(trans, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(trans).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setRootScopeVariable_TwoLevelHierarchy() {
        Trans parent = createTrans();
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setRootScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setRootScopeVariable_FourLevelHierarchy() {
        Job grandGrandParent = createJob();
        Trans grandParent = createTrans(grandGrandParent);
        Trans parent = createTrans(grandParent);
        Trans child = createTrans(parent);
        ScriptValuesAddedFunctions.setRootScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(grandParent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        Mockito.verify(grandGrandParent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setRootScopeVariable_NoParent() {
        Trans trans = createTrans();
        ScriptValuesAddedFunctions.setRootScopeVariable(trans, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
        Mockito.verify(trans).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
    }

    @Test
    public void setSystemScopeVariable_NoParent() {
        Trans trans = createTrans();
        Assert.assertNull(System.getProperty(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME));
        try {
            ScriptValuesAddedFunctions.setSystemScopeVariable(trans, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
            Assert.assertEquals(System.getProperty(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
            Mockito.verify(trans).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        } finally {
            System.clearProperty(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME);
        }
    }

    @Test
    public void setSystemScopeVariable_FourLevelHierarchy() {
        Job grandGrandParent = createJob();
        Trans grandParent = createTrans(grandGrandParent);
        Trans parent = createTrans(grandParent);
        Trans child = createTrans(parent);
        Assert.assertNull(System.getProperty(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME));
        try {
            ScriptValuesAddedFunctions.setSystemScopeVariable(child, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME, ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
            Assert.assertEquals(System.getProperty(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE);
            Mockito.verify(child).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
            Mockito.verify(parent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
            Mockito.verify(grandParent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
            Mockito.verify(grandGrandParent).setVariable(ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME), ArgumentMatchers.eq(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_VALUE));
        } finally {
            System.clearProperty(ScriptValueAddFunctions_SetVariableScopeTest.VARIABLE_NAME);
        }
    }
}

