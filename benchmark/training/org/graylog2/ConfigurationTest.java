/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2;


import com.github.joschi.jadconfig.ParameterException;
import com.github.joschi.jadconfig.RepositoryException;
import com.github.joschi.jadconfig.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class ConfigurationTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Map<String, String> validProperties;

    @Test
    public void testPasswordSecretIsTooShort() throws RepositoryException, ValidationException {
        validProperties.put("password_secret", "too short");
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("The minimum length for \"password_secret\" is 16 characters.");
        Configuration configuration = new Configuration();
        process();
    }

    @Test
    public void testPasswordSecretIsEmpty() throws RepositoryException, ValidationException {
        validProperties.put("password_secret", "");
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Parameter password_secret should not be blank");
        Configuration configuration = new Configuration();
        process();
    }

    @Test
    public void testPasswordSecretIsNull() throws RepositoryException, ValidationException {
        validProperties.put("password_secret", null);
        expectedException.expect(ParameterException.class);
        expectedException.expectMessage("Required parameter \"password_secret\" not found.");
        Configuration configuration = new Configuration();
        process();
    }

    @Test
    public void testPasswordSecretIsValid() throws RepositoryException, ValidationException {
        validProperties.put("password_secret", "abcdefghijklmnopqrstuvwxyz");
        Configuration configuration = new Configuration();
        process();
        assertThat(configuration.getPasswordSecret()).isEqualTo("abcdefghijklmnopqrstuvwxyz");
    }

    @Test
    public void testNodeIdFilePermissions() throws IOException {
        final File nonEmptyNodeIdFile = temporaryFolder.newFile("non-empty-node-id");
        final File emptyNodeIdFile = temporaryFolder.newFile("empty-node-id");
        // create a node-id file and write some id
        Files.write(nonEmptyNodeIdFile.toPath(), "test-node-id".getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        assertThat(nonEmptyNodeIdFile.length()).isGreaterThan(0);
        // parent "directory" is not a directory is not ok
        final File parentNotDirectory = new File(emptyNodeIdFile, "parent-is-file");
        assertThat(ConfigurationTest.validateWithPermissions(parentNotDirectory, "rw-------")).isFalse();
        // file missing and parent directory doesn't exist is not ok
        final File directoryNotExist = temporaryFolder.newFolder("not-readable");
        assertThat(directoryNotExist.delete()).isTrue();
        final File parentNotExist = new File(directoryNotExist, "node-id");
        assertThat(ConfigurationTest.validateWithPermissions(parentNotExist, "rw-------")).isFalse();
        // file missing and parent directory not readable is not ok
        final File directoryNotReadable = temporaryFolder.newFolder("not-readable");
        assertThat(directoryNotReadable.setReadable(false)).isTrue();
        final File parentNotReadable = new File(directoryNotReadable, "node-id");
        assertThat(ConfigurationTest.validateWithPermissions(parentNotReadable, "rw-------")).isFalse();
        // file missing and parent directory not writable is not ok
        final File directoryNotWritable = temporaryFolder.newFolder("not-writable");
        assertThat(directoryNotWritable.setWritable(false)).isTrue();
        final File parentNotWritable = new File(directoryNotWritable, "node-id");
        assertThat(ConfigurationTest.validateWithPermissions(parentNotWritable, "rw-------")).isFalse();
        // file missing and parent directory readable and writable is ok
        final File parentDirectory = temporaryFolder.newFolder();
        assertThat(parentDirectory.setReadable(true)).isTrue();
        assertThat(parentDirectory.setWritable(true)).isTrue();
        final File parentOk = new File(parentDirectory, "node-id");
        assertThat(ConfigurationTest.validateWithPermissions(parentOk, "rw-------")).isTrue();
        // read/write permissions should make the validation pass
        assertThat(ConfigurationTest.validateWithPermissions(nonEmptyNodeIdFile, "rw-------")).isTrue();
        assertThat(ConfigurationTest.validateWithPermissions(emptyNodeIdFile, "rw-------")).isTrue();
        // existing, but not writable is ok if the file is not empty
        assertThat(ConfigurationTest.validateWithPermissions(nonEmptyNodeIdFile, "r--------")).isTrue();
        // existing, but not writable is not ok if the file is empty
        assertThat(ConfigurationTest.validateWithPermissions(emptyNodeIdFile, "r--------")).isFalse();
        // existing, but not readable is not ok
        assertThat(ConfigurationTest.validateWithPermissions(nonEmptyNodeIdFile, "-w-------")).isFalse();
    }
}

