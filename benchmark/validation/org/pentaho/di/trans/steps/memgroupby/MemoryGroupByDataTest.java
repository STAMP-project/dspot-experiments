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
package org.pentaho.di.trans.steps.memgroupby;


import MemoryGroupByData.HashEntry;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;


/**
 * Created by bmorrise on 2/11/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MemoryGroupByDataTest {
    private MemoryGroupByData data = new MemoryGroupByData();

    @Mock
    private RowMetaInterface groupMeta;

    @Mock
    private ValueMetaInterface valueMeta;

    @Test
    public void hashEntryTest() {
        HashMap<MemoryGroupByData.HashEntry, String> map = new HashMap<>();
        byte[] byteValue1 = "key".getBytes();
        Object[] groupData1 = new Object[1];
        groupData1[0] = byteValue1;
        MemoryGroupByData.HashEntry hashEntry1 = data.getHashEntry(groupData1);
        map.put(hashEntry1, "value");
        byte[] byteValue2 = "key".getBytes();
        Object[] groupData2 = new Object[1];
        groupData2[0] = byteValue2;
        MemoryGroupByData.HashEntry hashEntry2 = data.getHashEntry(groupData2);
        String value = map.get(hashEntry2);
        Assert.assertEquals("value", value);
    }
}

