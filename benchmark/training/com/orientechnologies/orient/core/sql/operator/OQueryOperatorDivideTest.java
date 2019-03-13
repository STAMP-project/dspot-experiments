/**
 * *  Copyright 2016 OrientDB LTD (info(at)orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientdb.com
 */
package com.orientechnologies.orient.core.sql.operator;


import ORecordSerializerBinary.INSTANCE;
import com.orientechnologies.orient.core.sql.operator.math.OQueryOperatorDivide;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by luigidellaquila on 29/03/16.
 */
public class OQueryOperatorDivideTest {
    @Test
    public void test() {
        OQueryOperator operator = new OQueryOperatorDivide();
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10, 3, null, INSTANCE.getCurrentSerializer()), (10 / 3));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10L, 3L, null, INSTANCE.getCurrentSerializer()), (10L / 3L));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10.1, 3, null, INSTANCE.getCurrentSerializer()), (10.1 / 3));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10, 3.1, null, INSTANCE.getCurrentSerializer()), (10 / 3.1));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10.1, 3, null, INSTANCE.getCurrentSerializer()), (10.1 / 3));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10, 3.1, null, INSTANCE.getCurrentSerializer()), (10 / 3.1));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, new BigDecimal(10), 4, null, INSTANCE.getCurrentSerializer()), new BigDecimal(10).divide(new BigDecimal(4)));
        Assert.assertEquals(operator.evaluateRecord(null, null, null, 10, new BigDecimal(4), null, INSTANCE.getCurrentSerializer()), new BigDecimal(10).divide(new BigDecimal(4)));
    }
}

