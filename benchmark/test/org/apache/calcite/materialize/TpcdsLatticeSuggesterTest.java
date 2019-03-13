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
package org.apache.calcite.materialize;


import CalciteAssert.SchemaSpec;
import CalciteAssert.SchemaSpec.BLANK;
import CalciteConnectionProperty.CONFORMANCE;
import Frameworks.ConfigBuilder;
import SqlConformanceEnum.LENIENT;
import SqlParser.Config.DEFAULT;
import java.util.List;
import java.util.Properties;
import org.apache.calcite.adapter.tpcds.TpcdsSchema;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.test.CalciteAssert;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link LatticeSuggester}.
 */
public class TpcdsLatticeSuggesterTest {
    @Test
    public void testTpcdsAll() throws Exception {
        checkFoodMartAll(false);
    }

    @Test
    public void testTpcdsAllEvolve() throws Exception {
        checkFoodMartAll(true);
    }

    /**
     * Test helper.
     */
    private static class Tester {
        final LatticeSuggester suggester;

        private final FrameworkConfig config;

        Tester() {
            this(TpcdsLatticeSuggesterTest.Tester.config(BLANK).build());
        }

        private Tester(FrameworkConfig config) {
            this.config = config;
            suggester = new LatticeSuggester(config);
        }

        TpcdsLatticeSuggesterTest.Tester tpcds() {
            final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
            final double scaleFactor = 0.01;
            final SchemaPlus schema = rootSchema.add("tpcds", new TpcdsSchema(scaleFactor));
            final FrameworkConfig config = Frameworks.newConfigBuilder().parserConfig(DEFAULT).context(Contexts.of(new CalciteConnectionConfigImpl(new Properties()).set(CONFORMANCE, LENIENT.name()))).defaultSchema(schema).build();
            return withConfig(config);
        }

        TpcdsLatticeSuggesterTest.Tester withConfig(FrameworkConfig config) {
            return new TpcdsLatticeSuggesterTest.Tester(config);
        }

        List<Lattice> addQuery(String q) throws SqlParseException, RelConversionException, ValidationException {
            final Planner planner = new org.apache.calcite.prepare.PlannerImpl(config);
            final SqlNode node = planner.parse(q);
            final SqlNode node2 = planner.validate(node);
            final RelRoot root = planner.rel(node2);
            return suggester.addQuery(root.project());
        }

        /**
         * Parses a query returns its graph.
         */
        LatticeRootNode node(String q) throws SqlParseException, RelConversionException, ValidationException {
            final List<Lattice> list = addQuery(q);
            Assert.assertThat(list.size(), CoreMatchers.is(1));
            return list.get(0).rootNode;
        }

        static ConfigBuilder config(CalciteAssert.SchemaSpec spec) {
            final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
            final SchemaPlus schema = CalciteAssert.addSchema(rootSchema, spec);
            return Frameworks.newConfigBuilder().parserConfig(DEFAULT).defaultSchema(schema);
        }

        TpcdsLatticeSuggesterTest.Tester withEvolve(boolean evolve) {
            if (evolve == (config.isEvolveLattice())) {
                return this;
            }
            final Frameworks.ConfigBuilder configBuilder = Frameworks.newConfigBuilder(config);
            return new TpcdsLatticeSuggesterTest.Tester(configBuilder.evolveLattice(true).build());
        }
    }
}

/**
 * End TpcdsLatticeSuggesterTest.java
 */
