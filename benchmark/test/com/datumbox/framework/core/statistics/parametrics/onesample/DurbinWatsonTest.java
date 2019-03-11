/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
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
package com.datumbox.framework.core.statistics.parametrics.onesample;


import com.datumbox.framework.common.dataobjects.FlatDataList;
import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for DurbinWatson.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class DurbinWatsonTest extends AbstractTest {
    /**
     * Test of test method, of class DurbinWatson.
     */
    @Test
    public void testTest() {
        logger.info("test");
        FlatDataList errorList = new FlatDataList(Arrays.asList(new Object[]{ -2.7755575615629E-15, -2.0816681711722E-15, -2.2204460492503E-16, -1.9984014443253E-15, 3.3306690738755E-16, -4.9960036108132E-16, 2.2204460492503E-16, 4.4408920985006E-16, 2.4424906541753E-15, -1.2212453270877E-15, -2.2204460492503E-16, -2.7755575615629E-15, 1.3322676295502E-15, 1.6653345369377E-15, -1.8318679906315E-15, -1.2212453270877E-15, 2.3314683517128E-15, 8.8817841970013E-16, -2.2204460492503E-15, -1.2212453270877E-15, -3.1641356201817E-15, -6.6613381477509E-16, 2.7755575615629E-16, -5.5511151231258E-16, -1.193489751472E-15, 1.9984014443253E-15, -1.1657341758564E-15, -6.1062266354384E-16, 2.2204460492503E-16, -1.1657341758564E-15, -4.4408920985006E-16, -2.3314683517128E-15, -5.5511151231258E-16, 4.4408920985006E-16, 6.6613381477509E-16, 1.1102230246252E-15, 2.2204460492503E-15, -6.6613381477509E-16, 2.7755575615629E-16, -9.4368957093138E-16, 1.3322676295502E-15, -1.3322676295502E-15, 6.6613381477509E-16, 1.8873791418628E-15, -2.9976021664879E-15, -1.8873791418628E-15, -1.4710455076283E-15, -2.1649348980191E-15, -7.2164496600635E-16, -1.498801083244E-15, 2.2204460492503E-15, 2.2204460492503E-15, 6.6613381477509E-16, 6.6613381477509E-16, -1.1379786002408E-15, 2.9976021664879E-15, -8.8817841970013E-16, -1.5473733405713E-15, 9.9920072216264E-16, 2.4424906541753E-15, -1.7763568394003E-15, -2.7755575615629E-16, -7.7715611723761E-16, 1.8873791418628E-15, -1.1657341758564E-15, 1.6653345369377E-16, 4.4408920985006E-16, 4.4408920985006E-16, 1.7763568394003E-15, -3.5249581031849E-15, -6.1062266354384E-16, -1.5300261058115E-15, 1.8873791418628E-15, 1.7763568394003E-15, -2.2204460492503E-16, -3.885780586188E-16, -6.6613381477509E-16, 5.5511151231258E-17, -1.3877787807814E-15, 1.1102230246252E-15, 1.8873791418628E-15, 2.6645352591004E-15, 1.6653345369377E-15, 1.6653345369377E-15, 2.2204460492503E-15, -4.1633363423443E-15, 6.6613381477509E-16, 1.3322676295502E-15, -1.6930901125534E-15, -1.5404344466674E-15, 2.3314683517128E-15, 5.5511151231258E-16, 1.8873791418628E-15, -8.3266726846887E-16, -2.2204460492503E-16, -4.2743586448069E-15, 1.6653345369377E-15, 2.4424906541753E-15, -5.5511151231258E-17, -3.3306690738755E-16 }));
        int k = 10;
        boolean is_twoTailed = true;
        double aLevel = 0.05;
        boolean expResult = false;
        boolean result = DurbinWatson.test(errorList, k, is_twoTailed, aLevel);
        Assert.assertEquals(expResult, result);
    }
}

