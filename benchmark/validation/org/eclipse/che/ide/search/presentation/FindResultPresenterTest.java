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
package org.eclipse.che.ide.search.presentation;


import PartStackType.INFORMATION;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.project.QueryExpression;
import org.eclipse.che.ide.api.resources.SearchItemReference;
import org.eclipse.che.ide.api.resources.SearchResult;
import org.eclipse.che.ide.project.ProjectServiceClient;
import org.eclipse.che.ide.search.FullTextSearchPresenter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link FindResultPresenter}.
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class FindResultPresenterTest {
    @Mock
    private CoreLocalizationConstant localizationConstant;

    @Mock
    private FindResultView view;

    @Mock
    private WorkspaceAgent workspaceAgent;

    @Mock
    private Resources resources;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private EventBus eventBus;

    @Mock
    private QueryExpression queryExpression;

    @Mock
    private Promise<SearchResult> searchResultPromise;

    @Mock
    private SearchItemReference searchItemReference;

    @Captor
    private ArgumentCaptor<Operation<SearchResult>> argumentCaptor;

    @Mock
    private SearchResult result;

    @InjectMocks
    FindResultPresenter findResultPresenter;

    private ArrayList<SearchItemReference> items = new ArrayList(FullTextSearchPresenter.SEARCH_RESULT_ITEMS);

    @Test
    public void titleShouldBeReturned() {
        findResultPresenter.getTitle();
        Mockito.verify(localizationConstant).actionFullTextSearch();
    }

    @Test
    public void viewShouldBeReturned() {
        Assert.assertEquals(findResultPresenter.getView(), view);
    }

    @Test
    public void imageShouldBeReturned() {
        findResultPresenter.getTitleImage();
        Mockito.verify(resources).find();
    }

    @Test
    public void methodGoShouldBePerformed() {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        findResultPresenter.go(container);
        Mockito.verify(container).setWidget(view);
    }

    @Test
    public void responseShouldBeHandled() throws Exception {
        QueryExpression queryExpression = Mockito.mock(QueryExpression.class);
        findResultPresenter.handleResponse(result, queryExpression, "request");
        Mockito.verify(workspaceAgent).openPart(findResultPresenter, INFORMATION);
        Mockito.verify(workspaceAgent).setActivePart(findResultPresenter);
        Mockito.verify(view).showResults(result, "request");
        Mockito.verify(view).setPreviousBtnActive(false);
        Mockito.verify(view).setNextBtnActive(true);
    }

    @Test
    public void nextPageShouldNotBeShownIfNoResults() throws Exception {
        findResultPresenter.handleResponse(result, queryExpression, "request");
        Mockito.reset(view);
        findResultPresenter.onNextButtonClicked();
        Mockito.verify(queryExpression).setSkipCount(FullTextSearchPresenter.SEARCH_RESULT_ITEMS);
        Mockito.verify(searchResultPromise).then(argumentCaptor.capture());
        argumentCaptor.getValue().apply(new SearchResult(Collections.emptyList(), 0));
        Mockito.verify(view).setPreviousBtnActive(true);
        Mockito.verify(view).setNextBtnActive(false);
        Mockito.verify(view, Mockito.never()).showResults(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString());
    }

    @Test
    public void nextButtonShouldBeActiveIfResultHasMaxValueElements() throws Exception {
        findResultPresenter.handleResponse(result, queryExpression, "request");
        findResultPresenter.handleResponse(result, queryExpression, "request");
        Mockito.reset(view);
        findResultPresenter.onNextButtonClicked();
        Mockito.verify(queryExpression).setSkipCount(FullTextSearchPresenter.SEARCH_RESULT_ITEMS);
        Mockito.verify(searchResultPromise).then(argumentCaptor.capture());
        SearchResult searchResult = new SearchResult(items, 0);
        argumentCaptor.getValue().apply(searchResult);
        Mockito.verify(view).setPreviousBtnActive(true);
        Mockito.verify(view).setNextBtnActive(true);
        Mockito.verify(view).showResults(searchResult, "request");
    }

    @Test
    public void nextButtonShouldBeDisableIfResultHasLessThanMaxValue() throws Exception {
        items.remove(0);
        findResultPresenter.handleResponse(result, queryExpression, "request");
        Mockito.reset(view);
        findResultPresenter.onNextButtonClicked();
        Mockito.verify(queryExpression).setSkipCount(FullTextSearchPresenter.SEARCH_RESULT_ITEMS);
        Mockito.verify(searchResultPromise).then(argumentCaptor.capture());
        SearchResult searchResult = new SearchResult(items, 0);
        argumentCaptor.getValue().apply(searchResult);
        Mockito.verify(view).setPreviousBtnActive(true);
        Mockito.verify(view).setNextBtnActive(false);
        Mockito.verify(view).showResults(searchResult, "request");
    }

    @Test
    public void previousButtonShouldBeActiveIfResultHasLessThanMaxValue() throws Exception {
        items.remove(0);
        findResultPresenter.handleResponse(result, queryExpression, "request");
        Mockito.reset(view);
        findResultPresenter.onPreviousButtonClicked();
        Mockito.verify(queryExpression).setSkipCount((-(FullTextSearchPresenter.SEARCH_RESULT_ITEMS)));
        Mockito.verify(searchResultPromise).then(argumentCaptor.capture());
        SearchResult searchResult = new SearchResult(items, 0);
        argumentCaptor.getValue().apply(searchResult);
        Mockito.verify(view).setNextBtnActive(true);
        Mockito.verify(view).setPreviousBtnActive(false);
        Mockito.verify(view).showResults(searchResult, "request");
    }

    @Test
    public void previousButtonShouldBeActiveIfResultHasMaxValueElements() throws Exception {
        findResultPresenter.handleResponse(result, queryExpression, "request");
        Mockito.reset(view);
        findResultPresenter.onPreviousButtonClicked();
        Mockito.verify(queryExpression).setSkipCount((-(FullTextSearchPresenter.SEARCH_RESULT_ITEMS)));
        Mockito.verify(searchResultPromise).then(argumentCaptor.capture());
        SearchResult searchResult = new SearchResult(items, 0);
        argumentCaptor.getValue().apply(searchResult);
        Mockito.verify(view).setNextBtnActive(true);
        Mockito.verify(view).setPreviousBtnActive(true);
        Mockito.verify(view).showResults(searchResult, "request");
    }
}

