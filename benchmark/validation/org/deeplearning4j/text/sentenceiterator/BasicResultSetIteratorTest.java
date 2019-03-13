/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.text.sentenceiterator;


import java.sql.ResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Brad Heap nzv8fan@gmail.com
 */
public class BasicResultSetIteratorTest {
    @Test
    public void testHasMoreLines() throws Exception {
        // Setup a mock ResultSet object
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        // when .next() is called, first time true, then false
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock.getString("line")).thenReturn("The quick brown fox");
        BasicResultSetIterator iterator = new BasicResultSetIterator(resultSetMock, "line");
        int cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        } 
        Assert.assertEquals(1, cnt);
    }

    @Test
    public void testHasMoreLinesAndReset() throws Exception {
        // Setup a mock ResultSet object
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        // when .next() is called, first time true, then false, then after we reset we want the same behaviour
        Mockito.when(resultSetMock.next()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        Mockito.when(resultSetMock.getString("line")).thenReturn("The quick brown fox");
        BasicResultSetIterator iterator = new BasicResultSetIterator(resultSetMock, "line");
        int cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        } 
        Assert.assertEquals(1, cnt);
        iterator.reset();
        cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        } 
        Assert.assertEquals(1, cnt);
    }
}

