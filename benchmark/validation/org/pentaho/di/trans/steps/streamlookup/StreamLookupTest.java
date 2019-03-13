/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.streamlookup;


import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Test for StreamLookup step
 *
 * @author Pavel Sakun
 * @see StreamLookup
 */
public class StreamLookupTest {
    private StepMockHelper<StreamLookupMeta, StreamLookupData> smh;

    @Test
    public void testWithNormalStreams() throws KettleException {
        doTest(false, false, false);
    }

    @Test
    public void testWithBinaryLookupStream() throws KettleException {
        doTest(false, true, false);
    }

    @Test
    public void testWithBinaryDateStream() throws KettleException {
        doTest(false, false, true);
    }

    @Test
    public void testWithBinaryStreams() throws KettleException {
        doTest(false, false, true);
    }

    @Test
    public void testMemoryPreservationWithNormalStreams() throws KettleException {
        doTest(true, false, false);
    }

    @Test
    public void testMemoryPreservationWithBinaryLookupStream() throws KettleException {
        doTest(true, true, false);
    }

    @Test
    public void testMemoryPreservationWithBinaryDateStream() throws KettleException {
        doTest(true, false, true);
    }

    @Test
    public void testMemoryPreservationWithBinaryStreams() throws KettleException {
        doTest(true, false, true);
    }
}

