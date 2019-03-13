/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Interval;


public class Gaps0100_domainOnTable_Test extends AbstractDTAnalysisTest {
    @Test
    public void test() {
        List<DMNMessage> validate = AbstractValidatorTest.validator.validate(getReader("Gaps0100-domainOnTable.dmn"), Validation.VALIDATE_COMPILATION, Validation.ANALYZE_DECISION_TABLE);
        DTAnalysis analysis = getAnalysis(validate, "_70a95e62-8f5b-4b75-8cb9-9a9f781077da");
        Assert.assertThat(analysis.getGaps(), Matchers.hasSize(2));
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<Hyperrectangle> gaps = Arrays.asList(new Hyperrectangle(1, Arrays.asList(Interval.newFromBounds(new org.kie.dmn.validation.dtanalysis.model.Bound(new BigDecimal("0"), RangeBoundary.CLOSED, null), new org.kie.dmn.validation.dtanalysis.model.Bound(new BigDecimal("0"), RangeBoundary.CLOSED, null)))), new Hyperrectangle(1, Arrays.asList(Interval.newFromBounds(new org.kie.dmn.validation.dtanalysis.model.Bound(new BigDecimal("100"), RangeBoundary.CLOSED, null), new org.kie.dmn.validation.dtanalysis.model.Bound(new BigDecimal("100"), RangeBoundary.CLOSED, null)))));
        Assert.assertThat(gaps, Matchers.hasSize(2));
        // Assert GAPS
        Assert.assertThat(analysis.getGaps(), Matchers.contains(gaps.toArray()));
    }
}

