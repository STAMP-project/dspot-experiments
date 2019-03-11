/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.cluster.loadbalance;


import org.junit.Test;


/**
 *
 *
 * @author maijunsheng
 * @version ?????2013-6-14
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ActiveWeightLoadBalanceTest {
    private int lowActive = 1;

    private int smallSize = 5;

    private int largeSize = 15;

    private int testLoop = 100;

    @Test
    public void testSelect() {
        for (int i = 0; i < (testLoop); i++) {
            allAvailableCluster(smallSize);
            allAvailableCluster(largeSize);
            partOfUnAvailableCluster(smallSize, 1);
            partOfUnAvailableCluster(smallSize, ((smallSize) / 2));
            partOfUnAvailableCluster(smallSize, ((smallSize) - 1));
            partOfUnAvailableCluster(largeSize, 1);
            partOfUnAvailableCluster(largeSize, ((largeSize) / 2));
            partOfUnAvailableCluster(largeSize, ((largeSize) - 1));
            allUnAvailableCluster(smallSize);
            allUnAvailableCluster(largeSize);
        }
    }
}

