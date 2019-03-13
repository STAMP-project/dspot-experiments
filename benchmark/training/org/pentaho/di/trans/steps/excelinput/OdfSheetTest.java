/**
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
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
 */
package org.pentaho.di.trans.steps.excelinput;


import org.junit.Test;
import org.pentaho.di.core.spreadsheet.KWorkbook;
import org.pentaho.di.trans.steps.excelinput.ods.OdfSheet;


public class OdfSheetTest {
    private KWorkbook ods341;

    private KWorkbook ods24;

    @Test
    public void testRowColumnsCount() {
        String sameRowWidthSheet = "SameRowWidth";
        String diffRowWidthSheet = "DifferentRowWidth";
        checkRowCount(((OdfSheet) (ods341.getSheet(sameRowWidthSheet))), 3, "Row count mismatch for ODF v3.4.1");
        checkRowCount(((OdfSheet) (ods24.getSheet(sameRowWidthSheet))), 2, "Row count mismatch for ODF v2.4");
        checkRowCount(((OdfSheet) (ods341.getSheet(diffRowWidthSheet))), 3, "Row count mismatch for ODF v3.4.1");
        checkRowCount(((OdfSheet) (ods24.getSheet(diffRowWidthSheet))), 2, "Row count mismatch for ODF v2.4");
        checkCellCount(((OdfSheet) (ods341.getSheet(sameRowWidthSheet))), 15, "Cell count mismatch for ODF v3.4.1");
        checkCellCount(((OdfSheet) (ods24.getSheet(sameRowWidthSheet))), 1, "Cell count mismatch for ODF v2.4");
        checkCellCount(((OdfSheet) (ods341.getSheet(diffRowWidthSheet))), new int[]{ 15, 15, 12 }, "Cell count mismatch for ODF v3.4.1");
        checkCellCount(((OdfSheet) (ods24.getSheet(diffRowWidthSheet))), new int[]{ 3, 2 }, "Cell count mismatch for ODF v2.4");
    }
}

