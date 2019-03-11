/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.api.batch.sensor.error.internal;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.sensor.error.NewAnalysisError;
import org.sonar.api.batch.sensor.internal.SensorStorage;


public class DefaultAnalysisErrorTest {
    private InputFile inputFile;

    private SensorStorage storage;

    private TextPointer textPointer;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test_analysis_error() {
        DefaultAnalysisError analysisError = new DefaultAnalysisError(storage);
        analysisError.onFile(inputFile).at(textPointer).message("msg");
        Assert.assertThat(analysisError.location()).isEqualTo(textPointer);
        Assert.assertThat(analysisError.message()).isEqualTo("msg");
        Assert.assertThat(analysisError.inputFile()).isEqualTo(inputFile);
    }

    @Test
    public void test_save() {
        DefaultAnalysisError analysisError = new DefaultAnalysisError(storage);
        save();
        Mockito.verify(storage).store(analysisError);
        Mockito.verifyNoMoreInteractions(storage);
    }

    @Test
    public void test_no_storage() {
        exception.expect(NullPointerException.class);
        DefaultAnalysisError analysisError = new DefaultAnalysisError();
        save();
    }

    @Test
    public void test_validation() {
        try {
            onFile(null);
            Assert.fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
        NewAnalysisError error = new DefaultAnalysisError(storage).onFile(inputFile);
        try {
            error.onFile(inputFile);
            Assert.fail("Expected exception");
        } catch (IllegalStateException e) {
            // expected
        }
        error = new DefaultAnalysisError(storage).at(textPointer);
        try {
            error.at(textPointer);
            Assert.fail("Expected exception");
        } catch (IllegalStateException e) {
            // expected
        }
        try {
            save();
            Assert.fail("Expected exception");
        } catch (NullPointerException e) {
            // expected
        }
    }
}

