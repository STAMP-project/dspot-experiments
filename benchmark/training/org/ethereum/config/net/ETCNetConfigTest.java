/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.config.net;


import org.ethereum.config.blockchain.Eip150HFConfig;
import org.ethereum.config.blockchain.Eip160HFConfig;
import org.ethereum.config.blockchain.FrontierConfig;
import org.ethereum.config.blockchain.HomesteadConfig;
import org.junit.Test;


public class ETCNetConfigTest {
    @Test
    public void verifyETCNetConfigConstruction() {
        ETCNetConfig config = new ETCNetConfig();
        assertBlockchainConfigExistsAt(config, 0, FrontierConfig.class);
        assertBlockchainConfigExistsAt(config, 1150000, HomesteadConfig.class);
        assertBlockchainConfigExistsAt(config, 2500000, Eip150HFConfig.class);
        assertBlockchainConfigExistsAt(config, 3000000, Eip160HFConfig.class);
    }
}

