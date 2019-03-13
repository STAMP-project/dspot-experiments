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
package org.deeplearning4j.ui;


import org.deeplearning4j.ui.components.chart.ChartHistogram;
import org.deeplearning4j.ui.components.chart.ChartLine;
import org.deeplearning4j.ui.components.table.ComponentTable;
import org.deeplearning4j.ui.standalone.StaticPageUtil;
import org.junit.Test;


/**
 * Created by Alex on 2/06/2016.
 */
public class TestStandAlone {
    @Test
    public void testStandAlone() throws Exception {
        ComponentTable ct = content(new String[][]{ new String[]{ "First", "Second" }, new String[]{ "More", "More2" } }).build();
        ChartLine cl = addSeries("Second", new double[]{ 0, 0.5, 1, 1.5, 2 }, new double[]{ 5, 10, 15, 10, 5 }).build();
        ChartHistogram ch = addBin(2, 3, 1).build();
        System.out.println(StaticPageUtil.renderHTML(ct, cl, ch));
    }
}

