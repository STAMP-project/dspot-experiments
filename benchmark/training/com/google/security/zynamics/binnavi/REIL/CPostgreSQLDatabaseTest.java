/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.REIL;


import com.google.security.zynamics.binnavi.Database.CDatabase;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.disassembly.INaviFunction;
import com.google.security.zynamics.binnavi.disassembly.INaviInstruction;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.ReilTranslator;
import com.google.security.zynamics.reil.translators.StandardEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CPostgreSQLDatabaseTest {
    private CDatabase m_database;

    private final ReilTranslator<INaviInstruction> m_translator = new ReilTranslator<INaviInstruction>();

    @Test
    public void testCalc() throws CouldntLoadDataException, LoadCancelledException, InternalTranslationException {
        final INaviModule calc = m_database.getContent().getModule(2);
        calc.load();
        for (final INaviFunction function : calc.getContent().getFunctionContainer().getFunctions()) {
            function.load();
            m_translator.translate(new StandardEnvironment(), function);
            function.close();
        }
        calc.close();
    }

    @Test
    public void testNotepad() throws CouldntLoadDataException, LoadCancelledException, InternalTranslationException {
        final INaviModule notepad = m_database.getContent().getModule(1);
        notepad.load();
        for (final INaviFunction function : notepad.getContent().getFunctionContainer().getFunctions()) {
            function.load();
            m_translator.translate(new StandardEnvironment(), function);
            function.close();
        }
        notepad.close();
    }
}

