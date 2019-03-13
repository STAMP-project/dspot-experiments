/**
 * Copyright (c) 2017 the ACRA team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acra.attachment;


import RuntimeEnvironment.application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import com.google.common.net.MediaType;
import java.io.File;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 *
 *
 * @author F43nd1r
 * @since 04.12.2017
 */
@RunWith(RobolectricTestRunner.class)
public class AcraContentProviderTest {
    private static final String JSON_EXTENSION = "json";

    private static final String JSON_MIMETYPE = ((MediaType.JSON_UTF_8.type()) + "/") + (MediaType.JSON_UTF_8.subtype());

    private ContentResolver resolver;

    private File file;

    @Test
    public void query() {
        final Cursor cursor = resolver.query(AcraContentProvider.getUriForFile(application, file), new String[]{ OpenableColumns.SIZE, OpenableColumns.DISPLAY_NAME }, null, null, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(file.length(), cursor.getInt(0));
        Assert.assertEquals(file.getName(), cursor.getString(1));
        Assert.assertFalse(cursor.moveToNext());
        cursor.close();
    }

    @Test
    public void openFile() throws Exception {
        final Uri uri = AcraContentProvider.getUriForFile(application, file);
        Assert.assertNotNull(resolver.openFileDescriptor(uri, "r"));
    }

    @Test
    public void guessMimeType() {
        Assert.assertEquals(AcraContentProviderTest.JSON_MIMETYPE, AcraContentProvider.guessMimeType(AcraContentProvider.getUriForFile(application, file)));
    }
}

