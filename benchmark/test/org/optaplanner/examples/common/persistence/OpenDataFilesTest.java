/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
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
package org.optaplanner.examples.common.persistence;


import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingTest;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;


/**
 *
 *
 * @param <Solution_>
 * 		the solution type, the class with the {@link PlanningSolution} annotation
 */
@RunWith(Parameterized.class)
public abstract class OpenDataFilesTest<Solution_> extends LoggingTest {
    protected final CommonApp<Solution_> commonApp;

    protected final File solutionFile;

    protected SolutionFileIO<Solution_> solutionFileIO;

    protected OpenDataFilesTest(CommonApp<Solution_> commonApp, File solutionFile) {
        this.commonApp = commonApp;
        this.solutionFile = solutionFile;
    }

    @Test
    public void readSolution() {
        solutionFileIO.read(solutionFile);
        logger.info("Opened: {}", solutionFile);
    }
}

