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
package org.eclipse.che.ide.search;


import com.google.common.base.Optional;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.QueryExpression;
import org.eclipse.che.ide.api.resources.Container;
import org.eclipse.che.ide.api.resources.SearchItemReference;
import org.eclipse.che.ide.api.resources.SearchResult;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.search.presentation.FindResultPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link FullTextSearchPresenter}.
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class FullTextSearchPresenterTest {
    private final String SEARCHED_TEXT = "to be or not to be";

    @Mock
    private FullTextSearchView view;

    @Mock
    private FindResultPresenter findResultPresenter;

    @Mock
    private AppContext appContext;

    @Mock
    private Container workspaceRoot;

    @Mock
    private Container searchContainer;

    @Mock
    private SearchResult searchResult;

    @Mock
    private QueryExpression queryExpression;

    @Mock
    private Promise<Optional<Container>> optionalContainerPromise;

    @Captor
    private ArgumentCaptor<Operation<Optional<Container>>> optionalContainerCaptor;

    @Mock
    private Promise<SearchResult> searchResultPromise;

    @Captor
    private ArgumentCaptor<Operation<SearchResult>> searchResultCaptor;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> operationErrorCapture;

    @Captor
    private ArgumentCaptor<Operation<List<SearchItemReference>>> operationSuccessCapture;

    FullTextSearchPresenter fullTextSearchPresenter;

    @Test
    public void viewShouldBeShowed() {
        final Path path = Path.valueOf("/search");
        fullTextSearchPresenter.showDialog(path);
        Mockito.verify(view).showDialog();
        Mockito.verify(view).clearInput();
        Mockito.verify(view).setPathDirectory(ArgumentMatchers.eq(path.toString()));
    }

    @Test
    public void searchShouldBeSuccessfullyFinished() throws Exception {
        Mockito.when(view.getPathToSearch()).thenReturn("/search");
        Mockito.when(appContext.getWorkspaceRoot()).thenReturn(workspaceRoot);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.nullable(Path.class))).thenReturn(optionalContainerPromise);
        Mockito.when(searchContainer.search(ArgumentMatchers.nullable(QueryExpression.class))).thenReturn(searchResultPromise);
        Mockito.when(searchContainer.createSearchQueryExpression(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class))).thenReturn(queryExpression);
        fullTextSearchPresenter.search(SEARCHED_TEXT);
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.of(searchContainer));
        Mockito.verify(searchResultPromise).then(searchResultCaptor.capture());
        searchResultCaptor.getValue().apply(searchResult);
        Mockito.verify(view, Mockito.never()).showErrorMessage(ArgumentMatchers.anyString());
        Mockito.verify(view).close();
        Mockito.verify(findResultPresenter).handleResponse(ArgumentMatchers.eq(searchResult), ArgumentMatchers.eq(queryExpression), ArgumentMatchers.eq(SEARCHED_TEXT));
    }

    @Test
    public void searchWholeWordUnSelect() throws Exception {
        Mockito.when(view.getPathToSearch()).thenReturn("/search");
        Mockito.when(view.isWholeWordsOnly()).thenReturn(false);
        Mockito.when(appContext.getWorkspaceRoot()).thenReturn(workspaceRoot);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.nullable(Path.class))).thenReturn(optionalContainerPromise);
        Mockito.when(searchContainer.search(ArgumentMatchers.nullable(QueryExpression.class))).thenReturn(searchResultPromise);
        Mockito.when(searchContainer.createSearchQueryExpression(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class))).thenReturn(queryExpression);
        final String search = NameGenerator.generate("test", 10);
        fullTextSearchPresenter.search(search);
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.of(searchContainer));
        Mockito.verify(searchResultPromise).then(searchResultCaptor.capture());
        searchResultCaptor.getValue().apply(searchResult);
        Mockito.verify(searchContainer).search(queryExpression);
        Mockito.verify(view).isWholeWordsOnly();
        Mockito.verify(view, Mockito.never()).showErrorMessage(ArgumentMatchers.nullable(String.class));
        Mockito.verify(view).close();
        Mockito.verify(findResultPresenter).handleResponse(ArgumentMatchers.eq(searchResult), ArgumentMatchers.eq(queryExpression), ArgumentMatchers.eq(search));
    }

    @Test
    public void searchWholeWordSelect() throws Exception {
        Mockito.when(view.getPathToSearch()).thenReturn("/search");
        Mockito.when(view.isWholeWordsOnly()).thenReturn(true);
        Mockito.when(appContext.getWorkspaceRoot()).thenReturn(workspaceRoot);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.nullable(Path.class))).thenReturn(optionalContainerPromise);
        Mockito.when(searchContainer.search(ArgumentMatchers.nullable(QueryExpression.class))).thenReturn(searchResultPromise);
        Mockito.when(searchContainer.createSearchQueryExpression(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class))).thenReturn(queryExpression);
        final String search = NameGenerator.generate("test", 10);
        fullTextSearchPresenter.search(search);
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.of(searchContainer));
        Mockito.verify(searchResultPromise).then(searchResultCaptor.capture());
        searchResultCaptor.getValue().apply(searchResult);
        Mockito.verify(searchContainer).search(queryExpression);
        Mockito.verify(view).isWholeWordsOnly();
        Mockito.verify(view, Mockito.never()).showErrorMessage(ArgumentMatchers.anyString());
        Mockito.verify(view).close();
        Mockito.verify(findResultPresenter).handleResponse(ArgumentMatchers.eq(searchResult), ArgumentMatchers.eq(queryExpression), ArgumentMatchers.eq(search));
    }

    @Test
    public void searchHasDoneWithSomeError() throws Exception {
        Mockito.when(view.getPathToSearch()).thenReturn("/search");
        Mockito.when(appContext.getWorkspaceRoot()).thenReturn(workspaceRoot);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.nullable(Path.class))).thenReturn(optionalContainerPromise);
        fullTextSearchPresenter.search(SEARCHED_TEXT);
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.absent());
        Mockito.verify(view).showErrorMessage(ArgumentMatchers.anyString());
        Mockito.verify(view, Mockito.never()).close();
        Mockito.verify(findResultPresenter, Mockito.never()).handleResponse(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(queryExpression), ArgumentMatchers.anyString());
    }

    @Test
    public void onEnterClickedWhenAcceptButtonInFocus() throws Exception {
        Mockito.when(view.getSearchText()).thenReturn(SEARCHED_TEXT);
        Mockito.when(view.isAcceptButtonInFocus()).thenReturn(true);
        Mockito.when(view.getPathToSearch()).thenReturn("/search");
        Mockito.when(appContext.getWorkspaceRoot()).thenReturn(workspaceRoot);
        Mockito.when(workspaceRoot.getContainer(ArgumentMatchers.nullable(Path.class))).thenReturn(optionalContainerPromise);
        Mockito.when(searchContainer.search(ArgumentMatchers.nullable(QueryExpression.class))).thenReturn(searchResultPromise);
        Mockito.when(searchContainer.createSearchQueryExpression(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class))).thenReturn(queryExpression);
        List<SearchItemReference> result = Collections.emptyList();
        fullTextSearchPresenter.onEnterClicked();
        Mockito.verify(optionalContainerPromise).then(optionalContainerCaptor.capture());
        optionalContainerCaptor.getValue().apply(Optional.of(searchContainer));
        Mockito.verify(searchResultPromise).then(searchResultCaptor.capture());
        searchResultCaptor.getValue().apply(searchResult);
        Mockito.verify(view, Mockito.never()).showErrorMessage(ArgumentMatchers.anyString());
        Mockito.verify(view).close();
        Mockito.verify(findResultPresenter).handleResponse(ArgumentMatchers.eq(searchResult), ArgumentMatchers.eq(queryExpression), ArgumentMatchers.eq(SEARCHED_TEXT));
    }

    @Test
    public void onEnterClickedWhenCancelButtonInFocus() throws Exception {
        Mockito.when(view.isCancelButtonInFocus()).thenReturn(true);
        fullTextSearchPresenter.onEnterClicked();
        Mockito.verify(view).close();
        Mockito.verify(view, Mockito.never()).getSearchText();
    }

    @Test
    public void onEnterClickedWhenSelectPathButtonInFocus() throws Exception {
        Mockito.when(view.isSelectPathButtonInFocus()).thenReturn(true);
        fullTextSearchPresenter.onEnterClicked();
        Mockito.verify(view).showSelectPathDialog();
        Mockito.verify(view, Mockito.never()).getSearchText();
    }
}

