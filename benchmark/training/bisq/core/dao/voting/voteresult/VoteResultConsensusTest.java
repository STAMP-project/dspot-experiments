/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.core.dao.voting.voteresult;


import bisq.core.dao.governance.merit.MeritConsensus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@Slf4j
public class VoteResultConsensusTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetWeightedMeritAmount() {
        int currentChainHeight;
        int blocksPerYear = 50000;// 144*365=51264;

        currentChainHeight = 1000000;
        Assert.assertEquals("fresh issuance", 100000, MeritConsensus.getWeightedMeritAmount(100000, 1000000, currentChainHeight, blocksPerYear));
        Assert.assertEquals("0.5 year old issuance", 75000, MeritConsensus.getWeightedMeritAmount(100000, 975000, currentChainHeight, blocksPerYear));
        Assert.assertEquals("1 year old issuance", 50000, MeritConsensus.getWeightedMeritAmount(100000, 950000, currentChainHeight, blocksPerYear));
        Assert.assertEquals("1.5 year old issuance", 25000, MeritConsensus.getWeightedMeritAmount(100000, 925000, currentChainHeight, blocksPerYear));
        Assert.assertEquals("2 year old issuance", 0, MeritConsensus.getWeightedMeritAmount(100000, 900000, currentChainHeight, blocksPerYear));
        Assert.assertEquals("3 year old issuance", 0, MeritConsensus.getWeightedMeritAmount(100000, 850000, currentChainHeight, blocksPerYear));
        Assert.assertEquals("1 block old issuance", 99999, MeritConsensus.getWeightedMeritAmount(100000, 999999, currentChainHeight, blocksPerYear));
        Assert.assertEquals("2 block old issuance", 99998, MeritConsensus.getWeightedMeritAmount(100000, 999998, currentChainHeight, blocksPerYear));
        Assert.assertEquals("10 blocks old issuance", 99990, MeritConsensus.getWeightedMeritAmount(100000, 999990, currentChainHeight, blocksPerYear));
        Assert.assertEquals("100 blocks old issuance", 99900, MeritConsensus.getWeightedMeritAmount(100000, 999900, currentChainHeight, blocksPerYear));
        Assert.assertEquals("99_999 blocks old issuance", 1, MeritConsensus.getWeightedMeritAmount(100000, 900001, currentChainHeight, blocksPerYear));
        Assert.assertEquals("99_990 blocks old issuance", 10, MeritConsensus.getWeightedMeritAmount(100000, 900010, currentChainHeight, blocksPerYear));
        Assert.assertEquals("100_001 blocks old issuance", 0, MeritConsensus.getWeightedMeritAmount(100000, 899999, currentChainHeight, blocksPerYear));
        Assert.assertEquals("1_000_000 blocks old issuance", 0, MeritConsensus.getWeightedMeritAmount(100000, 0, currentChainHeight, blocksPerYear));
    }

    @Test
    public void testInvalidChainHeight() {
        exception.expect(IllegalArgumentException.class);
        MeritConsensus.getWeightedMeritAmount(100000, 2000000, 1000000, 1000000);
    }

    @Test
    public void testInvalidIssuanceHeight() {
        exception.expect(IllegalArgumentException.class);
        MeritConsensus.getWeightedMeritAmount(100000, (-1), 1000000, 1000000);
    }

    @Test
    public void testInvalidAmount() {
        exception.expect(IllegalArgumentException.class);
        MeritConsensus.getWeightedMeritAmount((-100000), 1, 1000000, 1000000);
    }

    @Test
    public void testInvalidCurrentChainHeight() {
        exception.expect(IllegalArgumentException.class);
        MeritConsensus.getWeightedMeritAmount(100000, 1, (-1), 1000000);
    }

    @Test
    public void testInvalidBlockPerYear() {
        exception.expect(IllegalArgumentException.class);
        MeritConsensus.getWeightedMeritAmount(100000, 1, 11, (-1));
    }
}

