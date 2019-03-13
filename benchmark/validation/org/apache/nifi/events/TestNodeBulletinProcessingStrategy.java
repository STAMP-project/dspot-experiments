/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.events;


import NodeBulletinProcessingStrategy.MAX_ENTRIES;
import org.junit.Assert;
import org.junit.Test;


public class TestNodeBulletinProcessingStrategy {
    @Test
    public void testUpdate() {
        NodeBulletinProcessingStrategy nBulletinProcessingStrategy = new NodeBulletinProcessingStrategy();
        nBulletinProcessingStrategy.update(new ComponentBulletin(1));
        nBulletinProcessingStrategy.update(new ComponentBulletin(2));
        nBulletinProcessingStrategy.update(new ComponentBulletin(3));
        nBulletinProcessingStrategy.update(new ComponentBulletin(4));
        nBulletinProcessingStrategy.update(new ComponentBulletin(5));
        Assert.assertEquals(5, nBulletinProcessingStrategy.getBulletins().size());
        nBulletinProcessingStrategy.update(new ComponentBulletin(1));
        nBulletinProcessingStrategy.update(new ComponentBulletin(2));
        nBulletinProcessingStrategy.update(new ComponentBulletin(3));
        nBulletinProcessingStrategy.update(new ComponentBulletin(4));
        nBulletinProcessingStrategy.update(new ComponentBulletin(5));
        nBulletinProcessingStrategy.update(new ComponentBulletin(6));
        nBulletinProcessingStrategy.update(new ComponentBulletin(7));
        Assert.assertEquals(MAX_ENTRIES, nBulletinProcessingStrategy.getBulletins().size());
    }
}

