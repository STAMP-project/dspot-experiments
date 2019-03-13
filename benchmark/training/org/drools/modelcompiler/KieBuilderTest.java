/**
 * Copyright 2005 JBoss Inc
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
package org.drools.modelcompiler;


import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class KieBuilderTest {
    @Test
    public void testDrlBuild() throws Exception {
        KieSession ksession = checkKieSession(DrlProject.class);
        Assert.assertTrue(((getAlphaConstraint(ksession)) instanceof MvelConstraint));
    }

    @Test
    public void testFlowModelBuild() throws Exception {
        KieSession ksession = checkKieSession(ExecutableModelFlowProject.class);
        Assert.assertTrue(((getAlphaConstraint(ksession)) instanceof LambdaConstraint));
    }

    @Test
    public void testPatternModelBuild() throws Exception {
        KieSession ksession = checkKieSession(ExecutableModelProject.class);
        Assert.assertTrue(((getAlphaConstraint(ksession)) instanceof LambdaConstraint));
    }
}

