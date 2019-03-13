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
package com.twosigma.beakerx.clojure.kernel;


import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.clojure.evaluator.ClojureEvaluator;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ClojureAutocompleteTest {
    private static ClojureEvaluator clojureEvaluator;

    @Test
    public void autocomplete_autocompleteResultNotEmpty() throws Exception {
        // when
        AutocompleteResult autocomplete = ClojureAutocompleteTest.clojureEvaluator.autocomplete("def", 3);
        // then
        Assertions.assertThat(autocomplete.getMatches()).isNotEmpty();
        Assertions.assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }
}

