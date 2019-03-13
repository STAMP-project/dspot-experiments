/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.scala.evaluator;


import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.evaluator.MagicCommandAutocompletePatternsMock;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ScalaAutocompleteTest {
    private static ScalaEvaluator scalaEvaluator;

    @Test
    public void autocomplete_autocompleteResultNotEmpty() throws Exception {
        // when
        AutocompleteResult autocomplete = ScalaAutocompleteTest.scalaEvaluator.autocomplete("val numbers = Li", 16);
        // then
        Assertions.assertThat(autocomplete.getMatches()).isNotEmpty();
        Assertions.assertThat(autocomplete.getStartIndex()).isEqualTo(14);
    }

    @Test
    public void autocomplete_multiLineOffsetCorrect() throws Exception {
        // when
        AutocompleteResult autocomplete = ScalaAutocompleteTest.scalaEvaluator.autocomplete("val x = 3\nval numbers = Li", 26);
        // then
        Assertions.assertThat(autocomplete.getMatches()).isNotEmpty();
        Assertions.assertThat(autocomplete.getStartIndex()).isEqualTo(24);
    }

    @Test
    public void autocomplete_namespaceShouldNotBeModified() {
        final String input1 = "val xyzzy = 32\nval xyz";
        // when
        AutocompleteResult autocomplete1 = ScalaAutocompleteTest.scalaEvaluator.autocomplete(input1, input1.length());
        AutocompleteResult autocomplete2 = ScalaAutocompleteTest.scalaEvaluator.autocomplete("xyz", 3);
        // then
        Assertions.assertThat(autocomplete2.getMatches()).isEmpty();
    }

    @Test
    public void autocomplete_internalDeclarationsAreVisible() {
        final String lines = "val xyzzy = 32\nxyz";
        // when
        AutocompleteResult autocomplete = ScalaAutocompleteTest.scalaEvaluator.autocomplete(lines, lines.length());
        // then
        Assertions.assertThat(autocomplete.getMatches()).isNotEmpty();
    }

    @Test
    public void autocomplete_interpretedResultsVisible() {
        // This test needs a fresh ScalaEvaluator to modify without disturbing other tests
        final ScalaEvaluator localEvaluator = new ScalaEvaluator("id", "sid", cellExecutor(), new NoBeakerxObjectTestFactory(), getTestTempFolderFactory(), EvaluatorTest.KERNEL_PARAMETERS, new EvaluatorTest.BeakexClientTestImpl(), new MagicCommandAutocompletePatternsMock());
        try {
            // when
            localEvaluator.evaluate(new SimpleEvaluationObject(""), "val xyzzy = 32");
            AutocompleteResult autocomplete = localEvaluator.autocomplete("xyz", 3);
            // then
            Assertions.assertThat(autocomplete.getMatches()).isNotEmpty();
        } finally {
            localEvaluator.exit();
        }
    }
}

