/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.statepersistance;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import elemental.json.Json;
import elemental.json.JsonFactory;
import elemental.json.JsonObject;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.api.statepersistance.AppStateServiceClient;
import org.eclipse.che.ide.api.statepersistance.StateComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Test covers {@link AppStateManager} functionality.
 *
 * @author Artem Zatsarynnyi
 * @author Dmitry Shnurenko
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class AppStateManagerTest {
    private static final String COMPONENT_ONE_ID = "component1";

    private static final String COMPONENT_TWO_ID = "component2";

    @Mock
    private StateComponentRegistry stateComponentRegistry;

    @Mock
    private Provider<StateComponentRegistry> stateComponentRegistryProvider;

    @Mock
    private Provider<PerspectiveManager> perspectiveManagerProvider;

    @Mock
    private StateComponent component1;

    @Mock
    private StateComponent component2;

    @Mock
    private Provider<StateComponent> component1Provider;

    @Mock
    private Provider<StateComponent> component2Provider;

    @Mock
    private Promise<Void> promise;

    @Mock
    private Promise<String> getStatePromise;

    @Mock
    private JsonFactory jsonFactory;

    @Mock
    private AppStateServiceClient appStateService;

    @Mock
    private PromiseProvider promiseProvider;

    @Mock
    private Promise<Void> sequentialRestore;

    @Captor
    private ArgumentCaptor<Function<Void, Promise<Void>>> sequentialRestoreThenFunction;

    @Captor
    private ArgumentCaptor<String> saveStateArgumentCaptor;

    private AppStateManager appStateManager;

    @Test
    public void shouldStoreState() throws Exception {
        appStateManager.persistState();
        Mockito.verify(appStateService).saveState(saveStateArgumentCaptor.capture());
        assertThat(saveStateArgumentCaptor.getValue()).isNotNull();
    }

    @Test
    public void shouldCallGetStateOnStateComponent() throws Exception {
        appStateManager.persistState();
        Mockito.verify(component1, Mockito.atLeastOnce()).getState();
        Mockito.verify(component2, Mockito.atLeastOnce()).getState();
    }

    @Test
    public void shouldSaveStateInFile() throws Exception {
        JsonObject firstComponentState = Json.createObject();
        firstComponentState.put("key1", "value1");
        Mockito.when(component1.getState()).thenReturn(firstComponentState);
        JsonObject secondComponentState = Json.createObject();
        secondComponentState.put("key2", "value2");
        Mockito.when(component2.getState()).thenReturn(secondComponentState);
        appStateManager.persistState();
        Mockito.verify(component1).getState();
        Mockito.verify(component2).getState();
        Mockito.verify(appStateService).saveState(saveStateArgumentCaptor.capture());
        String json = saveStateArgumentCaptor.getValue();
        assertThat(json).isNotNull();
        JsonObject appState = Json.parse(json);
        assertThat(appState).isNotNull();
        JsonObject jsonObject1 = appState.getObject(AppStateManagerTest.COMPONENT_ONE_ID);
        assertThat(jsonObject1.jsEquals(firstComponentState)).isTrue();
        JsonObject jsonObject2 = appState.getObject(AppStateManagerTest.COMPONENT_TWO_ID);
        assertThat(jsonObject2.jsEquals(secondComponentState)).isTrue();
    }

    @Test
    public void restoreShouldCallLoadState() throws Exception {
        JsonObject appState = Json.createObject();
        JsonObject firstComponent = Json.createObject();
        appState.put("component1", firstComponent);
        firstComponent.put("key1", "value1");
        Mockito.when(promiseProvider.resolve(ArgumentMatchers.nullable(Void.class))).thenReturn(sequentialRestore);
        appStateManager.restoreState(appState);
        Mockito.verify(sequentialRestore).thenPromise(sequentialRestoreThenFunction.capture());
        sequentialRestoreThenFunction.getValue().apply(null);
        ArgumentCaptor<JsonObject> stateCaptor = ArgumentCaptor.forClass(JsonObject.class);
        Mockito.verify(component1).loadState(stateCaptor.capture());
        JsonObject jsonObject = stateCaptor.getValue();
        assertThat(jsonObject.hasKey("key1")).isTrue();
        assertThat(jsonObject.getString("key1")).isEqualTo("value1");
    }
}

