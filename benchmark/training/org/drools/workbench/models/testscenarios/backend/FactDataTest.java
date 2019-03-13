/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.testscenarios.backend;


import java.util.ArrayList;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.junit.Assert;
import org.junit.Test;


public class FactDataTest {
    @Test
    public void testAdd() {
        FactData fd = new FactData("x", "y", new ArrayList(), false);
        Assert.assertEquals(0, fd.getFieldData().size());
        fd.getFieldData().add(new FieldData("x", "y"));
        Assert.assertEquals(1, fd.getFieldData().size());
        fd.getFieldData().add(new FieldData("q", "x"));
        Assert.assertEquals(2, fd.getFieldData().size());
    }
}

