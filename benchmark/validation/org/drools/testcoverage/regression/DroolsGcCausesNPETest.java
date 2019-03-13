/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.testcoverage.regression;


import KieServices.Factory;
import java.util.Random;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.kie.test.testcategory.TurtleTestCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Reproducer for BZ 1181584, by Mike Wilson.
 */
public class DroolsGcCausesNPETest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsGcCausesNPETest.class);

    private static final String KIE_BASE_NAME = "defaultBase";

    private static final String DRL_FILE_NAME = "DroolsGcCausesNPE.drl";

    private static final KieServices SERVICES = Factory.get();

    private static final ReleaseId RELEASE_ID = DroolsGcCausesNPETest.SERVICES.newReleaseId("org.drools.testcoverage", "drools-gc-causes-npe-example", "1.0");

    private KieSession session;

    private SessionPseudoClock clock;

    private FactType eventFactType;

    /**
     * The original test method reproducing NPE during event GC.
     * BZ 1181584
     */
    @Test
    @Category(TurtleTestCategory.class)
    public void testMoreTimesRepeated() throws Exception {
        final Random r = new Random(1);
        int i = 0;
        try {
            for (; i < 100000; i++) {
                insertAndAdvanceTime(i, r.nextInt(4000));
            }
        } catch (NullPointerException e) {
            DroolsGcCausesNPETest.LOGGER.warn(("failed at i = " + i));
            DroolsGcCausesNPETest.LOGGER.warn(("fact count: " + (session.getFactCount())));
            logActiveFacts();
            Assertions.fail("NPE thrown - consider reopening BZ 1181584", e);
        }
    }

    /**
     * Deterministic variant of the previous test method that reliably illustrates BZ 1274696.
     */
    @Test
    public void test() throws Exception {
        insertAndAdvanceTime(1, 4000);
    }
}

