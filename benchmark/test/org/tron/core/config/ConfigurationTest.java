/**
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.core.config;


import Constant.TEST_CONF;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;


@Slf4j
public class ConfigurationTest {
    @Test
    public void testGetEcKey() {
        ECKey key = ECKey.fromPrivate(Hex.decode("1cd5a70741c6e583d2dd3c5f17231e608eb1e52437210d948c5085e141c2d830"));
        // log.debug("address = {}", ByteArray.toHexString(key.getOwnerAddress()));
        Assert.assertEquals(((Wallet.getAddressPreFixString()) + "125b6c87b3d67114b3873977888c34582f27bbb0"), ByteArray.toHexString(key.getAddress()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullPathGetShouldThrowIllegalArgumentException() {
        Configuration.getByFileName(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenEmptyPathGetShouldThrowIllegalArgumentException() {
        Configuration.getByFileName(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getShouldNotFindConfiguration() {
        Config config = Configuration.getByFileName("notExistingPath", "notExistingPath");
        Assert.assertFalse(config.hasPath("storage"));
        Assert.assertFalse(config.hasPath("overlay"));
        Assert.assertFalse(config.hasPath("seed.node"));
        Assert.assertFalse(config.hasPath("genesis.block"));
    }

    @Test
    public void getShouldReturnConfiguration() {
        Config config = Configuration.getByFileName(TEST_CONF, TEST_CONF);
        Assert.assertTrue(config.hasPath("storage"));
        Assert.assertTrue(config.hasPath("seed.node"));
        Assert.assertTrue(config.hasPath("genesis.block"));
    }
}

