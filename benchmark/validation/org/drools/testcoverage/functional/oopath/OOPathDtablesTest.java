/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.testcoverage.functional.oopath;


import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;


/**
 * Test basic OOPath expressions used in Decision tables (*.xls, *.xlsx, *.csv)
 * in both RuleTable and Queries as well.
 */
@RunWith(Parameterized.class)
public class OOPathDtablesTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathDtablesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void xlsWithOOPathTest() {
        final KieSession kieSession = getKieSessionFromXls("oopath.xls");
        testOOPathWithDTable(kieSession);
    }

    @Test
    public void xlsxWithOOPathTest() {
        final KieSession kieSession = getKieSessionFromXlsx("oopath.xlsx");
        testOOPathWithDTable(kieSession);
    }

    @Test
    public void csvWithOOPathTest() {
        final KieSession kieSession = getKieSessionFromCsv("oopath.csv");
        testOOPathWithDTable(kieSession);
    }
}

