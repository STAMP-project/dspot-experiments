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
package org.ethereum.net.rlpx;


import java.io.FileNotFoundException;
import org.ethereum.config.NoAutoscan;
import org.ethereum.config.SystemProperties;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 02.11.2017
 */
@Ignore
public class SnappyConnectionTest {
    @Configuration
    @NoAutoscan
    public static class SysPropConfig1 {
        static SystemProperties props;

        @Bean
        public SystemProperties systemProperties() {
            return SnappyConnectionTest.SysPropConfig1.props;
        }
    }

    @Configuration
    @NoAutoscan
    public static class SysPropConfig2 {
        static SystemProperties props;

        @Bean
        public SystemProperties systemProperties() {
            return SnappyConnectionTest.SysPropConfig2.props;
        }
    }

    @Test
    public void test4To4() throws FileNotFoundException, InterruptedException {
        runScenario(4, 4);
    }

    @Test
    public void test4To5() throws FileNotFoundException, InterruptedException {
        runScenario(4, 5);
    }

    @Test
    public void test5To4() throws FileNotFoundException, InterruptedException {
        runScenario(5, 4);
    }

    @Test
    public void test5To5() throws FileNotFoundException, InterruptedException {
        runScenario(5, 5);
    }
}

