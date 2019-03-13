/**
 * ! ******************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.engine.model;


import ActingPrincipal.ANONYMOUS;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static ActingPrincipal.ANONYMOUS;


public class ActingPrincipalTest {
    ActingPrincipal principal1;

    ActingPrincipal principal2;

    @Test
    public void equals() throws Exception {
        principal1 = new ActingPrincipal("suzy");
        principal2 = new ActingPrincipal("joe");
        TestCase.assertFalse(principal1.equals(principal2));
        TestCase.assertFalse(principal1.equals(ANONYMOUS));
        principal2 = new ActingPrincipal("suzy");
        Assert.assertTrue(principal1.equals(principal2));
        principal2 = ANONYMOUS;
        Assert.assertTrue(principal2.equals(ANONYMOUS));
    }

    @Test
    public void isAnonymous() throws Exception {
        Assert.assertTrue(ANONYMOUS.isAnonymous());
        TestCase.assertFalse(new ActingPrincipal("harold").isAnonymous());
        TestCase.assertFalse(new ActingPrincipal("").isAnonymous());
    }
}

