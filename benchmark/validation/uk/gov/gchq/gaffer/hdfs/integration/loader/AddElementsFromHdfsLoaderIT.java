/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.hdfs.integration.loader;


import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs;
import uk.gov.gchq.gaffer.integration.impl.loader.ParameterizedLoaderIT;
import uk.gov.gchq.gaffer.integration.impl.loader.schemas.SchemaLoader;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.schema.TestSchema;
import uk.gov.gchq.gaffer.user.User;


public class AddElementsFromHdfsLoaderIT extends ParameterizedLoaderIT<AddElementsFromHdfs> {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    protected String inputDir1;

    protected String inputDir2;

    protected String inputDir3;

    protected String outputDir;

    protected String failureDir;

    protected String splitsDir;

    protected String splitsFile;

    protected String workingDir;

    protected String stagingDir;

    public AddElementsFromHdfsLoaderIT(final TestSchema schema, final SchemaLoader loader, final Map<String, User> userMap) {
        super(schema, loader, userMap);
    }

    @Test
    public void shouldThrowExceptionWhenAddElementsFromHdfsWhenFailureDirectoryContainsFiles() throws Exception {
        tearDown();
        final FileSystem fs = FileSystem.getLocal(createLocalConf());
        fs.mkdirs(new Path(failureDir));
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(((failureDir) + "/someFile.txt")), true)))) {
            writer.write("Some content");
        }
        try {
            setup();
            Assert.fail("Exception expected");
        } catch (final OperationException e) {
            Assert.assertEquals(("Failure directory is not empty: " + (failureDir)), e.getCause().getMessage());
        } finally {
            tearDown();
        }
    }

    @Test
    public void shouldAddElementsFromHdfsWhenDirectoriesAlreadyExist() throws Exception {
        // Given
        tearDown();
        final FileSystem fs = FileSystem.getLocal(createLocalConf());
        fs.mkdirs(new Path(outputDir));
        fs.mkdirs(new Path(failureDir));
        // When
        setup();
        // Then
        shouldGetAllElements();
    }

    @Test
    public void shouldThrowExceptionWhenAddElementsFromHdfsWhenOutputDirectoryContainsFiles() throws Exception {
        // Given
        tearDown();
        final FileSystem fs = FileSystem.getLocal(createLocalConf());
        fs.mkdirs(new Path(outputDir));
        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.create(new Path(((outputDir) + "/someFile.txt")), true)))) {
            writer.write("Some content");
        }
        // When
        try {
            setup();
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains(("Output directory exists and is not empty: " + (outputDir))));
        } finally {
            tearDown();
        }
    }
}

