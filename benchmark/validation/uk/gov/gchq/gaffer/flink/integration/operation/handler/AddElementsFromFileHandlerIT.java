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
package uk.gov.gchq.gaffer.flink.integration.operation.handler;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.flink.operation.FlinkTest;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.impl.add.AddElementsFromFile;
import uk.gov.gchq.gaffer.user.User;


public class AddElementsFromFileHandlerIT extends FlinkTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private File file;

    @Test
    public void shouldAddElements() throws Exception {
        // Given
        final Graph graph = FlinkTest.createGraph();
        final boolean validate = true;
        final boolean skipInvalid = false;
        final AddElementsFromFile op = new AddElementsFromFile.Builder().filename(file.getAbsolutePath()).generator(uk.gov.gchq.gaffer.generator.TestGeneratorImpl.class).parallelism(1).validate(validate).skipInvalidElements(skipInvalid).build();
        // When
        graph.execute(op, new User());
        // Then
        FlinkTest.verifyElements(graph);
    }
}

