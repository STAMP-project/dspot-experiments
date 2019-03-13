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
package uk.gov.gchq.gaffer.graph.hook;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.export.GetExport;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.SplitStoreFromFile;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.compare.Max;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.job.GetAllJobDetails;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;
import uk.gov.gchq.gaffer.operation.impl.output.ToEntitySeeds;
import uk.gov.gchq.gaffer.operation.impl.output.ToMap;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;


public class AdditionalOperationsTest extends JSONSerialisationTest<AdditionalOperations> {
    @Test
    public void shouldReturnClonedOperations() throws IOException {
        // Given
        final AdditionalOperations additionalOperations = JSONSerialiser.deserialise(StreamUtil.openStream(getClass(), "additionalOperations.json"), AdditionalOperations.class);
        // When / Then
        assertClonedOperations(additionalOperations.getStart(), additionalOperations.getStart());
        assertClonedOperations(additionalOperations.getBefore(), additionalOperations.getBefore());
        assertClonedOperations(additionalOperations.getAfter(), additionalOperations.getAfter());
        assertClonedOperations(additionalOperations.getEnd(), additionalOperations.getEnd());
    }

    @Test
    public void shouldSerialiseAndDeserialise() throws IOException {
        // When
        final AdditionalOperations original = new AdditionalOperations();
        original.setStart(Arrays.asList(new Limit(), new SplitStoreFromFile()));
        original.setEnd(Arrays.asList(new GetElements(), new GetAllElements()));
        final Map<String, List<Operation>> after = new HashMap<>();
        after.put(GetElements.class.getName(), Arrays.asList(new GetAdjacentIds(), new Max()));
        after.put(GetExport.class.getName(), Arrays.asList(new GetJobDetails(), new uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements()));
        original.setAfter(after);
        final Map<String, List<Operation>> before = new HashMap<>();
        before.put(ToSet.class.getName(), Arrays.asList(new uk.gov.gchq.gaffer.operation.impl.output.ToArray(), new ToEntitySeeds()));
        before.put(ToMap.class.getName(), Arrays.asList(new AddElements(), new GetAllJobDetails()));
        original.setBefore(before);
        final byte[] json = JSONSerialiser.serialise(original);
        final AdditionalOperations cloned = JSONSerialiser.deserialise(json, AdditionalOperations.class);
        // Then
        assertClonedOperations(original.getStart(), cloned.getStart());
        assertClonedOperations(original.getBefore(), cloned.getBefore());
        assertClonedOperations(original.getAfter(), cloned.getAfter());
        assertClonedOperations(original.getEnd(), cloned.getEnd());
    }
}

