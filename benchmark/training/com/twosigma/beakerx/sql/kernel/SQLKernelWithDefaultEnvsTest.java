/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.sql.kernel;


import com.twosigma.beakerx.kernel.EvaluatorParameters;
import com.twosigma.beakerx.sql.SQLKernelTest;
import org.junit.Test;


public class SQLKernelWithDefaultEnvsTest extends SQLKernelTest {
    @Test
    public void incorrectlyFormattedJdbcUri() {
        // give
        // when
        EvaluatorParameters kernelParameters = SQL.getKernelParameters(( name) -> {
            if (BEAKERX_SQL_DEFAULT_JDBC.equals(name)) {
                return "jdb:h2:mem:db1";
            }
            return null;
        });
        // then
        assertThat(kernelParameters.getParams().containsKey(SQL.BEAKERX_SQL_DEFAULT_JDBC)).isFalse();
    }

    @Test
    public void noDefaultJdbc() {
        // give
        // when
        EvaluatorParameters kernelParameters = SQL.getKernelParameters(( name) -> null);
        // then
        assertThat(kernelParameters.getParams().containsKey(SQL.BEAKERX_SQL_DEFAULT_JDBC)).isFalse();
    }
}

