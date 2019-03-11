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
package bisq.core.arbitration;


import bisq.core.user.User;
import bisq.network.p2p.NodeAddress;
import java.util.ArrayList;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ User.class, ArbitratorService.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ArbitratorManagerTest {
    @Test
    public void testIsArbitratorAvailableForLanguage() {
        User user = Mockito.mock(User.class);
        ArbitratorService arbitratorService = Mockito.mock(ArbitratorService.class);
        ArbitratorManager manager = new ArbitratorManager(null, arbitratorService, user, null, null, false);
        ArrayList<String> languagesOne = new ArrayList<String>() {
            {
                add("en");
                add("de");
            }
        };
        ArrayList<String> languagesTwo = new ArrayList<String>() {
            {
                add("en");
                add("es");
            }
        };
        Arbitrator one = new Arbitrator(new NodeAddress("arbitrator:1"), null, null, null, languagesOne, 0L, null, "", null, null, null);
        Arbitrator two = new Arbitrator(new NodeAddress("arbitrator:2"), null, null, null, languagesTwo, 0L, null, "", null, null, null);
        manager.addArbitrator(one, () -> {
        }, ( errorMessage) -> {
        });
        manager.addArbitrator(two, () -> {
        }, ( errorMessage) -> {
        });
        Assert.assertTrue(manager.isArbitratorAvailableForLanguage("en"));
        Assert.assertFalse(manager.isArbitratorAvailableForLanguage("th"));
    }

    @Test
    public void testGetArbitratorLanguages() {
        User user = Mockito.mock(User.class);
        ArbitratorService arbitratorService = Mockito.mock(ArbitratorService.class);
        ArbitratorManager manager = new ArbitratorManager(null, arbitratorService, user, null, null, false);
        ArrayList<String> languagesOne = new ArrayList<String>() {
            {
                add("en");
                add("de");
            }
        };
        ArrayList<String> languagesTwo = new ArrayList<String>() {
            {
                add("en");
                add("es");
            }
        };
        Arbitrator one = new Arbitrator(new NodeAddress("arbitrator:1"), null, null, null, languagesOne, 0L, null, "", null, null, null);
        Arbitrator two = new Arbitrator(new NodeAddress("arbitrator:2"), null, null, null, languagesTwo, 0L, null, "", null, null, null);
        ArrayList<NodeAddress> nodeAddresses = new ArrayList<NodeAddress>() {
            {
                add(two.getNodeAddress());
            }
        };
        manager.addArbitrator(one, () -> {
        }, ( errorMessage) -> {
        });
        manager.addArbitrator(two, () -> {
        }, ( errorMessage) -> {
        });
        Assert.assertThat(manager.getArbitratorLanguages(nodeAddresses), containsInAnyOrder("en", "es"));
        Assert.assertThat(manager.getArbitratorLanguages(nodeAddresses), CoreMatchers.not(containsInAnyOrder("de")));
    }
}

