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
package com.owncloud.android.ui.activities.data.activities;


import ActivitiesRepository.LoadActivitiesCallback;
import com.owncloud.android.lib.common.OwnCloudClient;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RemoteActivitiesRepositoryTest {
    @Mock
    ActivitiesServiceApi serviceApi;

    @Mock
    LoadActivitiesCallback mockedLoadActivitiesCallback;

    @Mock
    OwnCloudClient ownCloudClient;

    @Captor
    private ArgumentCaptor<ActivitiesServiceApi.ActivitiesServiceCallback> activitiesServiceCallbackCaptor;

    private ActivitiesRepository mActivitiesRepository;

    private List<Object> activitiesList;

    @Test
    public void loadActivitiesReturnSuccess() {
        mActivitiesRepository.getActivities("null", mockedLoadActivitiesCallback);
        Mockito.verify(serviceApi).getAllActivities(ArgumentMatchers.eq("null"), activitiesServiceCallbackCaptor.capture());
        activitiesServiceCallbackCaptor.getValue().onLoaded(activitiesList, ownCloudClient, "nextPageUrl");
        Mockito.verify(mockedLoadActivitiesCallback).onActivitiesLoaded(ArgumentMatchers.eq(activitiesList), ArgumentMatchers.eq(ownCloudClient), ArgumentMatchers.eq("nextPageUrl"));
    }

    @Test
    public void loadActivitiesReturnError() {
        mActivitiesRepository.getActivities("null", mockedLoadActivitiesCallback);
        Mockito.verify(serviceApi).getAllActivities(ArgumentMatchers.eq("null"), activitiesServiceCallbackCaptor.capture());
        activitiesServiceCallbackCaptor.getValue().onError("error");
        Mockito.verify(mockedLoadActivitiesCallback).onActivitiesLoadedError(ArgumentMatchers.eq("error"));
    }
}

