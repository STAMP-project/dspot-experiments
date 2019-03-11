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
import com.google.security.zynamics.binnavi.Database.Exceptions.CPartialLoadException;
import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import com.google.security.zynamics.binnavi.Database.Exceptions.LoadCancelledException;
import com.google.security.zynamics.binnavi.Exceptions.MaybeNullException;
import com.google.security.zynamics.binnavi.disassembly.INaviModule;
import com.google.security.zynamics.binnavi.disassembly.views.INaviView;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CPostgreSQLReilViewCreatorTest {
    private CDatabase m_database;

    @Test
    public void testSimple() throws CPartialLoadException, CouldntLoadDataException, LoadCancelledException, MaybeNullException, InternalTranslationException {
        final INaviModule module = m_database.getContent().getModules().get(0);
        module.load();
        final INaviView view = module.getContent().getViewContainer().getView(module.getContent().getFunctionContainer().getFunction("sub_1003429"));
        view.load();
        Assert.assertEquals(92, view.getNodeCount());
        Assert.assertEquals(144, view.getEdgeCount());
        final INaviView reilView = CReilViewCreator.create(module, view.getContent().getReilCode().getGraph());
        reilView.close();
        view.close();
        module.close();
    }
}

