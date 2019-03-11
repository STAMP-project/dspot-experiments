/**
 * Nextcloud Android client application
 *
 *   Copyright (C) 2018 Edvard Holst
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 *   License as published by the Free Software Foundation; either
 *   version 3 of the License, or any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.ui.activities;


import ActivitiesContract.View;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.ui.activities.data.activities.ActivitiesRepository;
import com.owncloud.android.ui.activities.data.files.FilesRepository;
import com.owncloud.android.ui.activity.BaseActivity;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ActivitiesPresenterTest {
    @Mock
    private FilesRepository mFileRepository;

    @Mock
    private View mView;

    @Mock
    private ActivitiesRepository mActivitiesRepository;

    @Mock
    private BaseActivity mBaseActivity;

    @Mock
    private OwnCloudClient mOwnCloudClient;

    @Mock
    private OCFile mOCFile;

    @Captor
    private ArgumentCaptor<FilesRepository.ReadRemoteFileCallback> mReadRemoteFilleCallbackCaptor;

    @Captor
    private ArgumentCaptor<ActivitiesRepository.LoadActivitiesCallback> mLoadActivitiesCallbackCaptor;

    private ActivitiesPresenter mPresenter;

    private List<Object> activitiesList;

    @Test
    public void loadActivitiesFromRepositoryIntoView() {
        // When loading activities from repository is requested from presenter...
        mPresenter.loadActivities(null);
        // Progress indicator is shown in view
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(true));
        // Repository starts retrieving activities from server
        Mockito.verify(mActivitiesRepository).getActivities(ArgumentMatchers.eq(null), mLoadActivitiesCallbackCaptor.capture());
        // Repository returns data
        mLoadActivitiesCallbackCaptor.getValue().onActivitiesLoaded(activitiesList, mOwnCloudClient, null);
        // Progress indicator is hidden
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(false));
        // List of activities is shown in view.
        Mockito.verify(mView).showActivities(ArgumentMatchers.eq(activitiesList), ArgumentMatchers.eq(mOwnCloudClient), ArgumentMatchers.eq(null));
    }

    @Test
    public void loadActivitiesFromRepositoryShowError() {
        // When loading activities from repository is requested from presenter...
        mPresenter.loadActivities(null);
        // Progress indicator is shown in view
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(true));
        // Repository starts retrieving activities from server
        Mockito.verify(mActivitiesRepository).getActivities(ArgumentMatchers.eq(null), mLoadActivitiesCallbackCaptor.capture());
        // Repository returns data
        mLoadActivitiesCallbackCaptor.getValue().onActivitiesLoadedError("error");
        // Progress indicator is hidden
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(false));
        // Correct error is shown in view
        Mockito.verify(mView).showActivitiesLoadError(ArgumentMatchers.eq("error"));
    }

    @Test
    public void loadRemoteFileFromRepositoryShowDetailUI() {
        // When retrieving remote file from repository...
        mPresenter.openActivity("null", mBaseActivity);
        // Progress indicator is shown in view
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(true));
        // Repository retrieves remote file
        Mockito.verify(mFileRepository).readRemoteFile(ArgumentMatchers.eq("null"), ArgumentMatchers.eq(mBaseActivity), mReadRemoteFilleCallbackCaptor.capture());
        // Repository returns valid file object
        mReadRemoteFilleCallbackCaptor.getValue().onFileLoaded(mOCFile);
        // Progress indicator is hidden
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(false));
        // File detail UI is shown
        Mockito.verify(mView).showActivityDetailUI(ArgumentMatchers.eq(mOCFile));
    }

    @Test
    public void loadRemoteFileFromRepositoryShowEmptyFile() {
        // When retrieving remote file from repository...
        mPresenter.openActivity("null", mBaseActivity);
        // Progress indicator is shown in view
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(true));
        // Repository retrieves remote file
        Mockito.verify(mFileRepository).readRemoteFile(ArgumentMatchers.eq("null"), ArgumentMatchers.eq(mBaseActivity), mReadRemoteFilleCallbackCaptor.capture());
        // Repository returns an valid but Null value file object.
        mReadRemoteFilleCallbackCaptor.getValue().onFileLoaded(null);
        // Progress indicator is hidden
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(false));
        // Returned file is null. Inform user.
        Mockito.verify(mView).showActivityDetailUIIsNull();
    }

    @Test
    public void loadRemoteFileFromRepositoryShowError() {
        // When retrieving remote file from repository...
        mPresenter.openActivity("null", mBaseActivity);
        // Progress indicator is shown in view
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(true));
        // Repository retrieves remote file
        Mockito.verify(mFileRepository).readRemoteFile(ArgumentMatchers.eq("null"), ArgumentMatchers.eq(mBaseActivity), mReadRemoteFilleCallbackCaptor.capture());
        // Repository returns valid file object
        mReadRemoteFilleCallbackCaptor.getValue().onFileLoadError("error");
        // Progress indicator is hidden
        Mockito.verify(mView).setProgressIndicatorState(ArgumentMatchers.eq(false));
        // Error message is shown to the user.
        Mockito.verify(mView).showActivityDetailError(ArgumentMatchers.eq("error"));
    }
}

