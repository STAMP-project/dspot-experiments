/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.core.row.value;


import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import ValueMetaInterface.STORAGE_TYPE_INDEXED;
import ValueMetaInterface.STORAGE_TYPE_NORMAL;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class ValueMetaBaseSerializationTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void restoresMetaData_storageTypeNormal() throws Exception {
        ValueMetaBase vmb = ValueMetaBaseSerializationTest.createTestObject(STORAGE_TYPE_NORMAL);
        ValueMetaBaseSerializationTest.checkRestoring(vmb);
    }

    @Test
    public void restoresMetaData_storageTypeIndexed() throws Exception {
        ValueMetaBase vmb = ValueMetaBaseSerializationTest.createTestObject(STORAGE_TYPE_INDEXED);
        vmb.setIndex(new Object[]{ "qwerty", "asdfg" });
        ValueMetaBaseSerializationTest.checkRestoring(vmb);
    }

    @Test
    public void restoresMetaData_storageTypeBinaryString() throws Exception {
        ValueMetaBase vmb = ValueMetaBaseSerializationTest.createTestObject(STORAGE_TYPE_BINARY_STRING);
        vmb.setStorageMetadata(new ValueMetaBase("storageMetadataInstance", ValueMetaInterface.TYPE_STRING));
        ValueMetaBaseSerializationTest.checkRestoring(vmb);
    }
}

