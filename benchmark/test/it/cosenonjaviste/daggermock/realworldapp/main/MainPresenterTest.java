/**
 * Copyright 2016 Fabio Collini.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cosenonjaviste.daggermock.realworldapp.main;


import it.cosenonjaviste.daggermock.InjectFromComponent;
import it.cosenonjaviste.daggermock.realworldapp.JUnitDaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.services.RestService;
import it.cosenonjaviste.daggermock.realworldapp.services.SnackBarManager;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MainPresenterTest {
    @Rule
    public JUnitDaggerMockRule rule = new JUnitDaggerMockRule();

    @Mock
    RestService restService;

    @Mock
    MainView view;

    @Mock
    SnackBarManager snackBarManager;

    @InjectFromComponent(MainActivity.class)
    MainPresenter presenter;

    @Test
    public void testLoadData() {
        Mockito.when(restService.executeServerCall()).thenReturn(true);
        presenter.loadData();
        Mockito.verify(view).showText("Hello world");
        Mockito.verify(snackBarManager, Mockito.never()).showMessage(ArgumentMatchers.anyString());
    }

    @Test
    public void testErrorOnLoadData() {
        Mockito.when(restService.executeServerCall()).thenReturn(false);
        presenter.loadData();
        Mockito.verify(view, Mockito.never()).showText(ArgumentMatchers.anyString());
        Mockito.verify(snackBarManager).showMessage("Error!");
    }
}

