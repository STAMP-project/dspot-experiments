/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.lookup;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class LookUpStrategyImmutableTest {
    private final Object internalObject;

    private final Object externalObject;

    private LookUpManager lookUpManager;

    public LookUpStrategyImmutableTest(Object internalObject, Object externalObject) {
        this.internalObject = internalObject;
        this.externalObject = externalObject;
    }

    @Test
    public void addImmutable() {
        lookUpManager.addWorkingObject(internalObject);
    }

    @Test
    public void removeImmutable() {
        lookUpManager.removeWorkingObject(internalObject);
    }

    @Test
    public void lookUpImmutable() {
        // make sure we are working with different instances
        Assert.assertNotSame(internalObject, externalObject);
        // since they are immutable we don't care about which instance is looked up
        Assert.assertEquals(internalObject, lookUpManager.lookUpWorkingObject(externalObject));
    }
}

