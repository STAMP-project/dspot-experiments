/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RuleModelTest {
    private RuleModel model;

    @Test
    public void checkGetAllLHSVariables() {
        final List<String> allLHSVariables = model.getAllLHSVariables();
        Assert.assertEquals(5, allLHSVariables.size());
        Assert.assertTrue(allLHSVariables.contains("$p1"));
        Assert.assertTrue(allLHSVariables.contains("$p2"));
        Assert.assertTrue(allLHSVariables.contains("$sfc1"));
        Assert.assertTrue(allLHSVariables.contains("$sfc2"));
        Assert.assertTrue(allLHSVariables.contains("$sfc3"));
    }

    @Test
    public void checkGetLHSPatternVariables() {
        final List<String> allLHSVariables = model.getLHSPatternVariables();
        Assert.assertEquals(2, allLHSVariables.size());
        Assert.assertTrue(allLHSVariables.contains("$p1"));
        Assert.assertTrue(allLHSVariables.contains("$p2"));
    }

    @Test
    public void checkGetLHSFieldVariables() {
        final List<String> allLHSVariables = model.getLHSVariables(false, true);
        Assert.assertEquals(3, allLHSVariables.size());
        Assert.assertTrue(allLHSVariables.contains("$sfc1"));
        Assert.assertTrue(allLHSVariables.contains("$sfc2"));
        Assert.assertTrue(allLHSVariables.contains("$sfc3"));
    }
}

