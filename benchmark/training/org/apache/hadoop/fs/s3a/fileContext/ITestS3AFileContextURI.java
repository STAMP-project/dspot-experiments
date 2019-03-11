/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a.fileContext;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContextURIBase;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.junit.Test;


/**
 * S3a implementation of FileContextURIBase.
 */
public class ITestS3AFileContextURI extends FileContextURIBase {
    private Configuration conf;

    private boolean hasMetadataStore;

    @Test
    @Override
    public void testModificationTime() throws IOException {
        // skip modtime tests as there may be some inconsistency during creation
        S3ATestUtils.assume("modification time tests are skipped", (!(hasMetadataStore)));
        super.testModificationTime();
    }
}

