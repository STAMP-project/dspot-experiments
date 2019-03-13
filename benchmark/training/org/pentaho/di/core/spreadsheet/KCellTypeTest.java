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
package org.pentaho.di.core.spreadsheet;


import KCellType.BOOLEAN;
import KCellType.BOOLEAN_FORMULA;
import KCellType.DATE;
import KCellType.DATE_FORMULA;
import KCellType.EMPTY;
import KCellType.LABEL;
import KCellType.NUMBER;
import KCellType.NUMBER_FORMULA;
import KCellType.STRING_FORMULA;
import org.junit.Assert;
import org.junit.Test;


public class KCellTypeTest {
    @Test
    public void testEnums() {
        Assert.assertEquals("Empty", EMPTY.getDescription());
        Assert.assertEquals("Boolean", BOOLEAN.getDescription());
        Assert.assertEquals("Boolean formula", BOOLEAN_FORMULA.getDescription());
        Assert.assertEquals("Date", DATE.getDescription());
        Assert.assertEquals("Date formula", DATE_FORMULA.getDescription());
        Assert.assertEquals("Label", LABEL.getDescription());
        Assert.assertEquals("String formula", STRING_FORMULA.getDescription());
        Assert.assertEquals("Number", NUMBER.getDescription());
        Assert.assertEquals("Number formula", NUMBER_FORMULA.getDescription());
    }
}

