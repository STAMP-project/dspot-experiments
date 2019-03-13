/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.maven.lifecycle.mapping;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author atanasenko
 */
public class LifecyclePhaseTest {
    @Test
    public void testToString() {
        LifecyclePhase phase = new LifecyclePhase();
        Assert.assertEquals("", phase.toString());
        LifecycleMojo mojo1 = new LifecycleMojo();
        mojo1.setGoal("jar:jar");
        phase.setMojos(Arrays.asList(mojo1));
        Assert.assertEquals("jar:jar", phase.toString());
        LifecycleMojo mojo2 = new LifecycleMojo();
        mojo2.setGoal("war:war");
        phase.setMojos(Arrays.asList(mojo1, mojo2));
        Assert.assertEquals("jar:jar,war:war", phase.toString());
    }

    @Test
    public void testSet() {
        LifecyclePhase phase = new LifecyclePhase();
        Assert.assertNull(phase.getMojos());
        phase.set("");
        Assert.assertNotNull(phase.getMojos());
        Assert.assertEquals(0, phase.getMojos().size());
        phase.set("jar:jar, war:war");
        List<LifecycleMojo> mojos = phase.getMojos();
        Assert.assertNotNull(mojos);
        Assert.assertEquals(2, mojos.size());
        LifecycleMojo mojo1 = mojos.get(0);
        Assert.assertNotNull(mojo1);
        Assert.assertEquals("jar:jar", mojo1.getGoal());
        LifecycleMojo mojo2 = mojos.get(1);
        Assert.assertNotNull(mojo2);
        Assert.assertEquals("war:war", mojo2.getGoal());
    }
}

