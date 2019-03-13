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
import android.app.Application;
import android.net.Uri;
import java.util.List;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 *
 *
 * @author F43nd1r
 * @since 30.11.2017
 */
@RunWith(RobolectricTestRunner.class)
public class DefaultAttachmentProviderTest {
    @Test
    public void getAttachments() throws Exception {
        Uri uri = Uri.parse("content://not-a-valid-content-uri");
        List<Uri> result = new DefaultAttachmentProvider().getAttachments(application, new org.acra.config.CoreConfigurationBuilder(new Application()).setAttachmentUris(uri.toString()).build());
        MatcherAssert.assertThat(result, Matchers.hasSize(1));
        Assert.assertEquals(uri, result.get(0));
    }
}

