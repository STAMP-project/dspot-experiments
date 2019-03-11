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
package bisq.core.btc.nodes;


import bisq.core.btc.nodes.BtcNodes.BtcNode;
import bisq.core.user.Preferences;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Preferences.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class BtcNodesSetupPreferencesTest {
    @Test
    public void testSelectPreferredNodesWhenPublicOption() {
        Preferences delegate = Mockito.mock(Preferences.class);
        Mockito.when(delegate.getBitcoinNodesOptionOrdinal()).thenReturn(BitcoinNodesOption.PUBLIC.ordinal());
        BtcNodesSetupPreferences preferences = new BtcNodesSetupPreferences(delegate);
        List<BtcNode> nodes = preferences.selectPreferredNodes(Mockito.mock(BtcNodes.class));
        Assert.assertTrue(nodes.isEmpty());
    }

    @Test
    public void testSelectPreferredNodesWhenCustomOption() {
        Preferences delegate = Mockito.mock(Preferences.class);
        Mockito.when(delegate.getBitcoinNodesOptionOrdinal()).thenReturn(BitcoinNodesOption.CUSTOM.ordinal());
        Mockito.when(delegate.getBitcoinNodes()).thenReturn("aaa.onion,bbb.onion");
        BtcNodesSetupPreferences preferences = new BtcNodesSetupPreferences(delegate);
        List<BtcNode> nodes = preferences.selectPreferredNodes(Mockito.mock(BtcNodes.class));
        Assert.assertEquals(2, nodes.size());
    }
}

