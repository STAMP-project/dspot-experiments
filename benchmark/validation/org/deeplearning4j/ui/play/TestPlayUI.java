/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.ui.play;


import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by Alex on 08/10/2016.
 */
@Ignore
public class TestPlayUI {
    @Test
    public void testUIAttachDetach() throws Exception {
        StatsStorage ss = new InMemoryStatsStorage();
        UIServer uiServer = UIServer.getInstance();
        uiServer.attach(ss);
        Assert.assertFalse(uiServer.getStatsStorageInstances().isEmpty());
        uiServer.detach(ss);
        Assert.assertTrue(uiServer.getStatsStorageInstances().isEmpty());
    }
}

