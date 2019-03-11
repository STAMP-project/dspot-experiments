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
package org.nd4j.jdbc.hsql;


import javax.sql.DataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class HSqlLoaderTest {
    private static HsqlLoader hsqlLoader;

    private static DataSource dataSource;

    public static final String JDBC_URL = "jdbc:hsqldb:mem:ndarrays";

    public static final String TABLE_NAME = "testarrays";

    public static final String ID_COLUMN_NAME = "id";

    public static final String COLUMN_NAME = "array";

    @Test
    public void getTotalRecordsTest() throws Exception {
        Assert.assertThat(1, CoreMatchers.is(getTotalRecords()));
        INDArray load = HSqlLoaderTest.hsqlLoader.load(HSqlLoaderTest.hsqlLoader.loadForID("1"));
        Assert.assertNotNull(load);
        Assert.assertEquals(Nd4j.linspace(1, 4, 4, Nd4j.dataType()), load);
    }
}

