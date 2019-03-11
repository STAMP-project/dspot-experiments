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
package com.querydsl.core;


import Ops.ALIAS;
import Ops.ARRAY_SIZE;
import Ops.AggOps.AVG_AGG;
import Ops.AggOps.COUNT_AGG;
import Ops.AggOps.COUNT_ALL_AGG;
import Ops.AggOps.MAX_AGG;
import Ops.AggOps.MIN_AGG;
import Ops.AggOps.SUM_AGG;
import Ops.CASE_ELSE;
import Ops.CASE_EQ_ELSE;
import Ops.CASE_EQ_WHEN;
import Ops.CASE_WHEN;
import Ops.COALESCE;
import Ops.EXISTS;
import Ops.INSTANCE_OF;
import Ops.LIST;
import Ops.MATCHES_IC;
import Ops.MOD;
import Ops.ORDER;
import Ops.ORDINAL;
import Ops.SET;
import Ops.SINGLETON;
import Ops.STRING_CAST;
import Ops.WRAPPED;
import Ops.XNOR;
import Ops.XOR;
import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Class CoverageTest.
 */
public class CoverageTest {
    private MatchingFiltersFactory matchers = new MatchingFiltersFactory(Module.COLLECTIONS, Target.MEM);

    private ProjectionsFactory projections = new ProjectionsFactory(Module.COLLECTIONS, Target.MEM);

    private FilterFactory filters = new FilterFactory(projections, Module.COLLECTIONS, Target.MEM);

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws IllegalAccessException, IllegalArgumentException {
        // make sure all Operators are covered in expression factory methods
        Set<Operator> usedOperators = new HashSet<Operator>();
        List<Expression<?>> exprs = new ArrayList<Expression<?>>();
        Entity entity = Alias.alias(Entity.class, "entity");
        // numeric
        exprs.addAll(projections.numeric(Alias.$(entity.getNum()), Alias.$(entity.getNum()), 1, false));
        exprs.addAll(matchers.numeric(Alias.$(entity.getNum()), Alias.$(entity.getNum()), 1));
        exprs.addAll(filters.numeric(Alias.$(entity.getNum()), Alias.$(entity.getNum()), 1));
        exprs.addAll(projections.numericCasts(Alias.$(entity.getNum()), Alias.$(entity.getNum()), 1));
        // string
        exprs.addAll(projections.string(Alias.$(entity.getStr()), Alias.$(entity.getStr()), "abc"));
        exprs.addAll(matchers.string(Alias.$(entity.getStr()), Alias.$(entity.getStr()), "abc"));
        exprs.addAll(filters.string(Alias.$(entity.getStr()), Alias.$(entity.getStr()), "abc"));
        // date
        exprs.addAll(projections.date(Alias.$(entity.getDate()), Alias.$(entity.getDate()), new Date(0)));
        exprs.addAll(matchers.date(Alias.$(entity.getDate()), Alias.$(entity.getDate()), new Date(0)));
        exprs.addAll(filters.date(Alias.$(entity.getDate()), Alias.$(entity.getDate()), new Date(0)));
        // dateTime
        exprs.addAll(projections.dateTime(Alias.$(entity.getDateTime()), Alias.$(entity.getDateTime()), new java.util.Date(0)));
        exprs.addAll(matchers.dateTime(Alias.$(entity.getDateTime()), Alias.$(entity.getDateTime()), new java.util.Date(0)));
        exprs.addAll(filters.dateTime(Alias.$(entity.getDateTime()), Alias.$(entity.getDateTime()), new java.util.Date(0)));
        // time
        exprs.addAll(projections.time(Alias.$(entity.getTime()), Alias.$(entity.getTime()), new Time(0)));
        exprs.addAll(matchers.time(Alias.$(entity.getTime()), Alias.$(entity.getTime()), new Time(0)));
        exprs.addAll(filters.time(Alias.$(entity.getTime()), Alias.$(entity.getTime()), new Time(0)));
        // boolean
        exprs.addAll(filters.booleanFilters(Alias.$(entity.isBool()), Alias.$(entity.isBool())));
        // collection
        exprs.addAll(projections.list(Alias.$(entity.getList()), Alias.$(entity.getList()), ""));
        exprs.addAll(filters.list(Alias.$(entity.getList()), Alias.$(entity.getList()), ""));
        // array
        exprs.addAll(projections.array(Alias.$(entity.getArray()), Alias.$(entity.getArray()), ""));
        exprs.addAll(filters.array(Alias.$(entity.getArray()), Alias.$(entity.getArray()), ""));
        // map
        exprs.addAll(projections.map(Alias.$(entity.getMap()), Alias.$(entity.getMap()), "", ""));
        exprs.addAll(filters.map(Alias.$(entity.getMap()), Alias.$(entity.getMap()), "", ""));
        for (Expression<?> e : exprs) {
            if (e instanceof Operation) {
                Operation<?> op = ((Operation<?>) (e));
                if ((op.getArg(0)) instanceof Operation) {
                    usedOperators.add(((Operation<?>) (op.getArg(0))).getOperator());
                } else
                    if (((op.getArgs().size()) > 1) && ((op.getArg(1)) instanceof Operation)) {
                        usedOperators.add(((Operation<?>) (op.getArg(1))).getOperator());
                    }

                usedOperators.add(op.getOperator());
            }
        }
        // missing mappings
        usedOperators.addAll(// Ops.DELEGATE,
        // TODO: add support
        // aggregation
        Arrays.<Operator>asList(INSTANCE_OF, ALIAS, ARRAY_SIZE, MOD, STRING_CAST, WRAPPED, ORDER, XOR, XNOR, CASE_WHEN, CASE_ELSE, CASE_EQ_WHEN, CASE_EQ_ELSE, LIST, SET, SINGLETON, COALESCE, ORDINAL, MATCHES_IC, AVG_AGG, MAX_AGG, MIN_AGG, SUM_AGG, COUNT_AGG, COUNT_ALL_AGG, EXISTS));
        List<Operator> notContained = new ArrayList<Operator>();
        for (Field field : Ops.class.getFields()) {
            if (Operator.class.isAssignableFrom(field.getType())) {
                Operator val = ((Operator) (field.get(null)));
                if (!(usedOperators.contains(val))) {
                    System.err.println(((field.getName()) + " was not contained"));
                    notContained.add(val);
                }
            }
        }
        Assert.assertTrue(((notContained.size()) + " errors in processing, see log for details"), notContained.isEmpty());
    }
}

