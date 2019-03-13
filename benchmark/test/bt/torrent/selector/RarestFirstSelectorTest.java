/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.torrent.selector;


import bt.test.torrent.selector.UpdatablePieceStatistics;
import org.junit.Assert;
import org.junit.Test;


public class RarestFirstSelectorTest {
    @Test
    public void testSelector() {
        UpdatablePieceStatistics statistics = new UpdatablePieceStatistics(8);
        statistics.setPiecesCount(0, 0, 0, 0, 0, 0, 0, 0);
        Assert.assertEquals(0, RarestFirstSelectorTest.collect(RarestFirstSelector.rarest().getNextPieces(statistics)).length);
        statistics.setPiecesCount(0, 3, 0, 2, 1, 0, 0, 0);
        Assert.assertArrayEquals(new Integer[]{ 4, 3, 1 }, RarestFirstSelectorTest.collect(RarestFirstSelector.rarest().getNextPieces(statistics)));
        statistics.setPieceCount(0, 1);
        Assert.assertArrayEquals(new Integer[]{ 0, 4, 3, 1 }, RarestFirstSelectorTest.collect(RarestFirstSelector.rarest().getNextPieces(statistics)));
    }
}

