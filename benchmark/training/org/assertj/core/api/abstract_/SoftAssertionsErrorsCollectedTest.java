/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
/**
 * Make sure that package-private access is lost
 */
package org.assertj.core.api.abstract_;


import java.util.List;
import org.assertj.core.api.AbstractStandardSoftAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This tests that classes extended from {@link AbstractStandardSoftAssertions} will have access to the list of
 * collected errors that the various proxies have collected.
 */
public class SoftAssertionsErrorsCollectedTest {
    private final Object objectForTesting = null;

    private final SoftAssertionsErrorsCollectedTest.TestCollector softly = new SoftAssertionsErrorsCollectedTest.TestCollector();

    @Test
    public void return_empty_list_of_errors() {
        softly.assertThat(objectForTesting).isNull();// No errors to collect

        Assertions.assertThat(softly.getErrors()).isEmpty();
    }

    @Test
    public void returns_nonempty_list_of_errors() {
        softly.assertThat(objectForTesting).isNotNull();// This should allow something to be collected

        Assertions.assertThat(softly.getErrors()).hasAtLeastOneElementOfType(Throwable.class);
    }

    private class TestCollector extends AbstractStandardSoftAssertions {
        public List<Throwable> getErrors() {
            return errorsCollected();
        }
    }
}

