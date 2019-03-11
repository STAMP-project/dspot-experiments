package org.jak_linux.dns66.db;


import android.content.ContentResolver;
import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jak_linux.dns66.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by jak on 19/05/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class, Uri.class })
public class RuleDatabaseUpdateTaskTest {
    HashMap<String, Uri> uriLocations = new HashMap<>();

    @Test
    public void testReleaseGarbagePermissions() throws Exception {
        Context mockContext = mock(Context.class);
        ContentResolver mockResolver = mock(ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockResolver);
        final List<UriPermission> persistedPermissions = new LinkedList<>();
        when(mockResolver.getPersistedUriPermissions()).thenReturn(persistedPermissions);
        UriPermission usedPermission = mock(UriPermission.class);
        when(usedPermission.getUri()).thenReturn(newUri("content://used"));
        persistedPermissions.add(usedPermission);
        UriPermission garbagePermission = mock(UriPermission.class);
        when(garbagePermission.getUri()).thenReturn(newUri("content://garbage"));
        persistedPermissions.add(garbagePermission);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Iterator<UriPermission> iter = persistedPermissions.iterator();
                while (iter.hasNext()) {
                    UriPermission perm = iter.next();
                    if ((perm.getUri()) == (getArgumentAt(0, Uri.class)))
                        iter.remove();

                } 
                return null;
            }
        }).when(mockResolver, "releasePersistableUriPermission", ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyInt());
        Configuration configuration = new Configuration();
        configuration.hosts.items.add(newItemForLocation("content://used"));
        Assert.assertTrue(persistedPermissions.contains(usedPermission));
        Assert.assertTrue(persistedPermissions.contains(garbagePermission));
        releaseGarbagePermissions();
        Assert.assertTrue(persistedPermissions.contains(usedPermission));
        Assert.assertFalse(persistedPermissions.contains(garbagePermission));
    }
}

