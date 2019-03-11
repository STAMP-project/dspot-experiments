/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.accumulostore.integration.performance;


import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.RangeFactory;
import uk.gov.gchq.gaffer.accumulostore.key.exception.RangeFactoryException;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;


/**
 * Tests the performance of the Bloom filter - checks that looking up random data is quicker
 * than looking up data that is present.
 * This class is based on Accumulo's BloomFilterLayerLookupTest (org.apache.accumulo.core.file.BloomFilterLayerLookupTest).
 */
public class BloomFilterIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(BloomFilterIT.class);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private RangeFactory byteEntityRangeFactory;

    private AccumuloElementConverter byteEntityElementConverter;

    private RangeFactory Gaffer1RangeFactory;

    private AccumuloElementConverter gafferV1ElementConverter;

    @Test
    public void test() throws IOException, RangeFactoryException {
        testFilter(byteEntityElementConverter, byteEntityRangeFactory);
        testFilter(gafferV1ElementConverter, Gaffer1RangeFactory);
    }
}

