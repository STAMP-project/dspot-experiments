/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


@PrepareForTest({ ProfileCache.class })
public class ProfileManagerTest extends FacebookPowerMockTestCase {
    @Test
    public void testLoadCurrentProfileEmptyCache() {
        ProfileCache profileCache = mock(ProfileCache.class);
        LocalBroadcastManager localBroadcastManager = mock(LocalBroadcastManager.class);
        ProfileManager profileManager = new ProfileManager(localBroadcastManager, profileCache);
        Assert.assertFalse(profileManager.loadCurrentProfile());
        Mockito.verify(profileCache, Mockito.times(1)).load();
    }

    @Test
    public void testLoadCurrentProfileWithCache() {
        ProfileCache profileCache = mock(ProfileCache.class);
        Profile profile = ProfileTest.createDefaultProfile();
        Mockito.when(profileCache.load()).thenReturn(profile);
        LocalBroadcastManager localBroadcastManager = mock(LocalBroadcastManager.class);
        ProfileManager profileManager = new ProfileManager(localBroadcastManager, profileCache);
        Assert.assertTrue(profileManager.loadCurrentProfile());
        Mockito.verify(profileCache, Mockito.times(1)).load();
        // Verify that we don't save it back
        Mockito.verify(profileCache, Mockito.never()).save(ArgumentMatchers.any(Profile.class));
        // Verify that we broadcast
        Mockito.verify(localBroadcastManager).sendBroadcast(ArgumentMatchers.any(Intent.class));
        // Verify that if we set the same (semantically) profile there is no additional broadcast.
        profileManager.setCurrentProfile(ProfileTest.createDefaultProfile());
        Mockito.verify(localBroadcastManager, Mockito.times(1)).sendBroadcast(ArgumentMatchers.any(Intent.class));
        // Verify that if we unset the profile there is a broadcast
        profileManager.setCurrentProfile(null);
        Mockito.verify(localBroadcastManager, Mockito.times(2)).sendBroadcast(ArgumentMatchers.any(Intent.class));
    }
}

