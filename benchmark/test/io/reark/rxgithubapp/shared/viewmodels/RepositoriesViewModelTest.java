/**
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.shared.viewmodels;


import io.reactivex.observers.TestObserver;
import io.reark.rxgithubapp.shared.data.DataFunctions.GetGitHubRepository;
import io.reark.rxgithubapp.shared.data.DataFunctions.GetGitHubRepositorySearch;
import io.reark.rxgithubapp.shared.pojo.GitHubRepository;
import io.reark.rxgithubapp.shared.pojo.GitHubRepositorySearch;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RepositoriesViewModelTest {
    private RepositoriesViewModel viewModel;

    @Test
    public void testStartFetchingReportedAsLoading() throws Exception {
        Assert.assertEquals(ProgressStatus.LOADING, RepositoriesViewModel.toProgressStatus().apply(ongoing()));
    }

    @Test
    public void testFetchingErrorReportedAsError() throws Exception {
        Assert.assertEquals(ProgressStatus.ERROR, RepositoriesViewModel.toProgressStatus().apply(completedWithError(null)));
    }

    @Test
    public void testAnyValueReportedAsIdle() throws Exception {
        GitHubRepositorySearch value = new GitHubRepositorySearch("", Collections.emptyList());
        Assert.assertEquals(ProgressStatus.IDLE, RepositoriesViewModel.toProgressStatus().apply(onNext(value)));
    }

    @Test
    public void testTooManyRepositoriesAreCappedToFive() throws Exception {
        TestObserver<List<GitHubRepository>> observer = new TestObserver();
        viewModel.toGitHubRepositoryList().apply(Arrays.asList(1, 2, 3, 4, 5, 6)).subscribe(observer);
        observer.awaitTerminalEvent();
        Assert.assertEquals("Invalid number of repositories", 5, ((Collection<?>) (observer.getEvents().get(0).get(0))).size());
    }

    @Test
    public void testTooLittleRepositoriesReturnThoseRepositories() throws Exception {
        TestObserver<List<GitHubRepository>> observer = new TestObserver();
        viewModel.toGitHubRepositoryList().apply(Arrays.asList(1, 2, 3, 4, 5)).subscribe(observer);
        Assert.assertEquals("Invalid number of repositories", 5, ((Collection<?>) (observer.getEvents().get(0).get(0))).size());
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionWhenRepositoryIdIsNull() {
        // noinspection ConstantConditions
        viewModel.getGitHubRepositoryObservable(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionWhenNetworkStatusIsNull() {
        // noinspection ConstantConditions
        viewModel.setNetworkStatusText(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionWhenSearchStringIsNull() {
        // noinspection ConstantConditions
        viewModel.setSearchString(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionWhenSelectedRepositoryIsNull() {
        // noinspection ConstantConditions
        viewModel.selectRepository(null);
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionConstructedWithNullRepositorySearch() {
        // noinspection ConstantConditions
        new RepositoriesViewModel(null, Mockito.mock(GetGitHubRepository.class));
    }

    @Test(expected = NullPointerException.class)
    public void testThrowsNullPointerExceptionConstructedWithNullRepository() {
        // noinspection ConstantConditions
        new RepositoriesViewModel(Mockito.mock(GetGitHubRepositorySearch.class), null);
    }
}

