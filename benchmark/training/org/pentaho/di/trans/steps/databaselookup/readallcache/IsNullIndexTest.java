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
package org.pentaho.di.trans.steps.databaselookup.readallcache;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
@RunWith(Parameterized.class)
public class IsNullIndexTest {
    private final Long[][] rows;

    private final int amountOfNulls;

    private IsNullIndex matchingNulls;

    private IsNullIndex matchingNonNulls;

    private SearchingContext context;

    public IsNullIndexTest(Long[][] rows) {
        this.rows = rows;
        int cnt = 0;
        for (Long[] row : rows) {
            for (Long value : row) {
                if (value == null) {
                    cnt++;
                }
            }
        }
        this.amountOfNulls = cnt;
    }

    @Test
    public void lookupFor_Null() {
        testFindsCorrectly(matchingNulls, true);
    }

    @Test
    public void lookupFor_One() {
        testFindsCorrectly(matchingNonNulls, false);
    }
}

