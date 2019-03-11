/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.transform.schema;


import ColumnType.Double;
import ColumnType.Long;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 04/09/2016.
 */
public class TestSchemaMethods {
    @Test
    public void testNumberedColumnAdding() {
        Schema schema = new Schema.Builder().addColumnsDouble("doubleCol_%d", 0, 2).addColumnsLong("longCol_%d", 3, 5).addColumnsString("stringCol_%d", 6, 8).build();
        Assert.assertEquals(9, schema.numColumns());
        for (int i = 0; i < 9; i++) {
            if (i <= 2) {
                Assert.assertEquals(("doubleCol_" + i), schema.getName(i));
                Assert.assertEquals(Double, schema.getType(i));
            } else
                if (i <= 5) {
                    Assert.assertEquals(("longCol_" + i), schema.getName(i));
                    Assert.assertEquals(Long, schema.getType(i));
                } else {
                    Assert.assertEquals(("stringCol_" + i), schema.getName(i));
                    Assert.assertEquals(ColumnType.String, schema.getType(i));
                }

        }
    }
}

