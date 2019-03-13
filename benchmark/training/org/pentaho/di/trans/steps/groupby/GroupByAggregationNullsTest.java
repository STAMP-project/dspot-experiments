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
package org.pentaho.di.trans.steps.groupby;


import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * PDI-10250 - "Group by" step - Minimum aggregation doesn't work
 */
public class GroupByAggregationNullsTest {
    static StepMockHelper<GroupByMeta, GroupByData> mockHelper;

    GroupBy step;

    GroupByData data;

    int def = -113;

    /**
     * PDI-10250 - "Group by" step - Minimum aggregation doesn't work
     *
     * KETTLE_AGGREGATION_MIN_NULL_IS_VALUED
     *
     * Set this variable to Y to set the minimum to NULL if NULL is within an aggregate. Otherwise by default NULL is
     * ignored by the MIN aggregate and MIN is set to the minimum value that is not NULL. See also the variable
     * KETTLE_AGGREGATION_ALL_NULLS_ARE_ZERO.
     *
     * @throws KettleValueException
     * 		
     */
    @Test
    public void calcAggregateResultTestMin_1_Test() throws KettleValueException {
        step.setMinNullIsValued(true);
        step.calcAggregate(new Object[]{ null });
        Assert.assertNull("Value is set", data.agg[0]);
    }

    @Test
    public void calcAggregateResultTestMin_2_Test() throws KettleValueException {
        step.setMinNullIsValued(true);
        step.calcAggregate(new Object[]{ null });
        Assert.assertNull("Value is set", data.agg[0]);
    }

    @Test
    public void calcAggregateResultTestMin_5_Test() throws KettleValueException {
        step.calcAggregate(new Object[]{ null });
        Assert.assertEquals("Value is NOT set", def, data.agg[0]);
    }

    @Test
    public void calcAggregateResultTestMin_3_Test() throws KettleValueException {
        step.setMinNullIsValued(false);
        step.calcAggregate(new Object[]{ null });
        Assert.assertEquals("Value is NOT set", def, data.agg[0]);
    }

    // PDI-15648 - Minimum aggregation doesn't work when null value in first row
    @Test
    public void getMinAggregateResultFirstValIsNullTest() throws KettleValueException {
        data.agg[0] = null;
        step.setMinNullIsValued(false);
        step.calcAggregate(new Object[]{ null });
        step.calcAggregate(new Object[]{ 2 });
        Assert.assertEquals("Min aggregation doesn't properly work if the first value is null", 2, data.agg[0]);
    }

    /**
     * Set this variable to Y to return 0 when all values within an aggregate are NULL. Otherwise by default a NULL is
     * returned when all values are NULL.
     *
     * @throws KettleValueException
     * 		
     */
    @Test
    public void getAggregateResultTestMin_0_Test() throws KettleValueException {
        // data.agg[0] is not null - this is the default behaviour
        step.setAllNullsAreZero(true);
        Object[] row = step.getAggregateResult();
        Assert.assertEquals("Default value is not corrupted", def, row[0]);
    }

    @Test
    public void getAggregateResultTestMin_1_Test() throws KettleValueException {
        data.agg[0] = null;
        step.setAllNullsAreZero(true);
        Object[] row = step.getAggregateResult();
        Assert.assertEquals("Returns 0 if aggregation is null", new Long(0), row[0]);
    }

    @Test
    public void getAggregateResultTestMin_3_Test() throws KettleValueException {
        data.agg[0] = null;
        step.setAllNullsAreZero(false);
        Object[] row = step.getAggregateResult();
        Assert.assertNull("Returns null if aggregation is null", row[0]);
    }
}

