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
package com.twosigma.beakerx.sql;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.autocomplete.AutocompleteResult;
import com.twosigma.beakerx.sql.evaluator.SQLEvaluator;
import org.junit.Test;


public class SQLAutocompleteTest {
    private SQLEvaluator sqlEvaluator;

    private KernelTest kernelTest;

    @Test
    public void shouldAutocompleteTo_s() throws Exception {
        // when
        AutocompleteResult autocomplete = sqlEvaluator.autocomplete("s", 1);
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void shouldAutocompleteToValues() throws Exception {
        // given
        givenColorTable();
        // when
        String code = "INSERT INTO color (id, name, code) VALU";
        AutocompleteResult autocomplete = sqlEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(35);
    }

    @Test
    public void shouldAutocompleteToPercentPercentbeakerD() throws Exception {
        // given
        givenColorTable();
        // when
        String code = "%%beakerD";
        AutocompleteResult autocomplete = sqlEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(0);
    }

    @Test
    public void shouldAutocompleteAfterSpace() throws Exception {
        // given
        givenColorTable();
        // when
        String code = "%%beakerDB ";
        AutocompleteResult autocomplete = sqlEvaluator.autocomplete(code, code.length());
        // then
        assertThat(autocomplete.getMatches()).isNotEmpty();
        assertThat(autocomplete.getStartIndex()).isEqualTo(code.length());
    }
}

