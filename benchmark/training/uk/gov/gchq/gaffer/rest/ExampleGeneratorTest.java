/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.rest.factory.DefaultGraphFactory;
import uk.gov.gchq.gaffer.rest.factory.GraphFactory;
import uk.gov.gchq.gaffer.rest.service.v2.example.DefaultExamplesFactory;


@RunWith(Parameterized.class)
public class ExampleGeneratorTest {
    private final DefaultExamplesFactory generator = new DefaultExamplesFactory();

    private final GraphFactory graphFactory = new DefaultGraphFactory();

    private final Class<? extends Operation> opClass;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    public ExampleGeneratorTest(final Class<? extends Operation> opClass) {
        this.opClass = opClass;
    }

    @Test
    public void shouldBuildOperation() throws JsonProcessingException, IllegalAccessException, InstantiationException {
        // Given
        final Operation operation = generator.generateExample(opClass);
        // Then
        MatcherAssert.assertThat(operation, CoreMatchers.notNullValue());
    }
}

