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
package org.pentaho.di.trans.steps.rules;


import org.junit.Assert;
import org.junit.Test;


public class RulesExecutorDataTest {
    private RulesExecutorData data;

    @Test
    public void testLoadRow() throws Exception {
        // test
        data.loadRow(new Object[]{ "1", "2" });
        data.execute();
        data.loadRow(new Object[]{ "3", "4" });
        // verify
        Assert.assertEquals(null, data.fetchResult("c1"));
        Assert.assertEquals(null, data.fetchResult("c2"));
    }
}

