/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.sql.magic.command;


import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.MagicCommand;
import com.twosigma.beakerx.kernel.magic.command.outcome.MagicCommandOutcomeItem;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;


public class SQLMagicCommandTest {
    public static final ArrayList<MagicCommandOutcomeItem> NO_ERRORS = new ArrayList<>();

    private SQLKernelTest kernel;

    @Test
    public void handleDefaultDatasourceMagicCommand() throws Exception {
        // given
        String codeAsString = (DefaultDataSourcesMagicCommand.DEFAULT_DATASOURCE) + " jdbc:h2:mem:db1";
        MagicCommand command = new MagicCommand(new DefaultDataSourcesMagicCommand(kernel), codeAsString);
        Code code = Code.createCode(codeAsString, Collections.singletonList(command), SQLMagicCommandTest.NO_ERRORS, MessageFactorTest.commMsg());
        // when
        code.execute(kernel, 1);
        // then
        assertThat(getDefaultDatasource().get()).isEqualTo("jdbc:h2:mem:db1");
    }

    @Test
    public void handleDatasourceMagicCommand() throws Exception {
        // given
        String codeAsString = (DataSourcesMagicCommand.DATASOURCES) + " jdbc:h2:mem:db2";
        MagicCommand command = new MagicCommand(new DataSourcesMagicCommand(kernel), codeAsString);
        Code code = Code.createCode(codeAsString, Collections.singletonList(command), SQLMagicCommandTest.NO_ERRORS, MessageFactorTest.commMsg());
        // when
        code.execute(kernel, 1);
        // then
        assertThat(getDatasource().get()).isEqualTo("jdbc:h2:mem:db2");
    }
}

