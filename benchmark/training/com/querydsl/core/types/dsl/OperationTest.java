/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.types.dsl;


import Ops.DateTimeOps;
import ToStringVisitor.DEFAULT;
import com.google.common.collect.ImmutableList;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static Ops.ADD;
import static Ops.ALIAS;
import static Ops.CONCAT;
import static Ops.EQ;
import static Ops.SUBSTR_1ARG;
import static Ops.TRIM;


public class OperationTest {
    enum ExampleEnum {

        A,
        B;}

    @SuppressWarnings("unchecked")
    @Test
    public void various() {
        Expression[] args = new Expression[]{ new StringPath("x"), new StringPath("y") };
        List<Operation<?>> operations = new ArrayList<Operation<?>>();
        // paths.add(new ArrayOperation(String[].class, "p"));
        operations.add(new BooleanOperation(EQ, args));
        operations.add(new ComparableOperation(String.class, SUBSTR_1ARG, args));
        operations.add(new DateOperation(Date.class, DateTimeOps.CURRENT_DATE, args));
        operations.add(new DateTimeOperation(Date.class, DateTimeOps.CURRENT_TIMESTAMP, args));
        operations.add(new EnumOperation(OperationTest.ExampleEnum.class, ALIAS, args));
        operations.add(new NumberOperation(Integer.class, ADD, args));
        operations.add(new SimpleOperation(String.class, TRIM, args));
        operations.add(new StringOperation(CONCAT, args));
        operations.add(new TimeOperation(Time.class, DateTimeOps.CURRENT_TIME, args));
        for (Operation<?> operation : operations) {
            Operation<?> other = ExpressionUtils.operation(operation.getType(), operation.getOperator(), ImmutableList.copyOf(operation.getArgs()));
            Assert.assertEquals(operation.toString(), operation.accept(DEFAULT, Templates.DEFAULT));
            Assert.assertEquals(operation.hashCode(), other.hashCode());
            Assert.assertEquals(operation, other);
            Assert.assertNotNull(operation.getOperator());
            Assert.assertNotNull(operation.getArgs());
            Assert.assertNotNull(operation.getType());
        }
    }
}

