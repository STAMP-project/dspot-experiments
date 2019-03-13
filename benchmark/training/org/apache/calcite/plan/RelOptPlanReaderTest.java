/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.plan;


import JdbcRules.JdbcProject;
import org.apache.calcite.rel.AbstractRelNode;
import org.apache.calcite.rel.externalize.RelJson;
import org.apache.calcite.rel.logical.LogicalProject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link org.apache.calcite.rel.externalize.RelJson}.
 */
public class RelOptPlanReaderTest {
    @Test
    public void testTypeToClass() {
        RelJson relJson = new RelJson(null);
        // in org.apache.calcite.rel package
        Assert.assertThat(relJson.classToTypeName(LogicalProject.class), CoreMatchers.is("LogicalProject"));
        Assert.assertThat(relJson.typeNameToClass("LogicalProject"), CoreMatchers.sameInstance(((Class) (LogicalProject.class))));
        // in org.apache.calcite.adapter.jdbc.JdbcRules outer class
        Assert.assertThat(relJson.classToTypeName(JdbcProject.class), CoreMatchers.is("JdbcProject"));
        Assert.assertThat(relJson.typeNameToClass("JdbcProject"), CoreMatchers.equalTo(((Class) (JdbcProject.class))));
        try {
            Class clazz = relJson.typeNameToClass("NonExistentRel");
            Assert.fail(("expected exception, got " + clazz));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("unknown type NonExistentRel"));
        }
        try {
            Class clazz = relJson.typeNameToClass("org.apache.calcite.rel.NonExistentRel");
            Assert.fail(("expected exception, got " + clazz));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("unknown type org.apache.calcite.rel.NonExistentRel"));
        }
        // In this class; no special treatment. Note: '$MyRel' not '.MyRel'.
        Assert.assertThat(relJson.classToTypeName(RelOptPlanReaderTest.MyRel.class), CoreMatchers.is("org.apache.calcite.plan.RelOptPlanReaderTest$MyRel"));
        Assert.assertThat(relJson.typeNameToClass(RelOptPlanReaderTest.MyRel.class.getName()), CoreMatchers.equalTo(((Class) (RelOptPlanReaderTest.MyRel.class))));
        // Using canonical name (with '$'), not found
        try {
            Class clazz = relJson.typeNameToClass(RelOptPlanReaderTest.MyRel.class.getCanonicalName());
            Assert.fail(("expected exception, got " + clazz));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("unknown type org.apache.calcite.plan.RelOptPlanReaderTest.MyRel"));
        }
    }

    /**
     * Dummy relational expression.
     */
    public static class MyRel extends AbstractRelNode {
        public MyRel(RelOptCluster cluster, RelTraitSet traitSet) {
            super(cluster, traitSet);
        }
    }
}

/**
 * End RelOptPlanReaderTest.java
 */
