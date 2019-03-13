/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.ivwloader;


import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * User: Dzmitry Stsiapanau Date: 11/20/13 Time: 12:41 PM
 */
public class IngresVectorwiseTest {
    private class IngresVectorwiseLoaderTest extends IngresVectorwiseLoader {
        // public List<Throwable> errors = new ArrayList<Throwable>();
        public IngresVectorwiseLoaderTest(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        /**
         * Create the command line for a sql process depending on the meta information supplied.
         *
         * @param meta
         * 		The meta data to create the command line from
         * @return The string to execute.
         * @throws org.pentaho.di.core.exception.KettleException
         * 		Upon any exception
         */
        @Override
        public String createCommandLine(IngresVectorwiseLoaderMeta meta) throws KettleException {
            String bufferSizeString = environmentSubstitute(meta.getBufferSize());
            int bufferSize = (Utils.isEmpty(bufferSizeString)) ? 5000 : Const.toInt(bufferSizeString, 5000);
            Class<?> vwload = VWLoadMocker.class;
            String userDir;
            try {
                // note: we need to convert to URI to get a valid Path to use with windows
                userDir = Paths.get(vwload.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
            } catch (URISyntaxException e) {
                throw new KettleException(e);
            }
            return (((((((("java -cp . -Duser.dir=" + userDir) + ' ') + (vwload.getCanonicalName())) + ' ') + bufferSize) + ' ') + (meta.getMaxNrErrors())) + ' ') + (meta.getErrorFileName());
        }
    }

    private static final String IVW_TEMP_PREFIX = "IngresVectorwiseLoaderTest";

    private static final String IVW_TEMP_EXTENSION = ".txt";

    private String lineSeparator = System.getProperty("line.separator");

    private static StepMockHelper<IngresVectorwiseLoaderMeta, IngresVectorwiseLoaderData> stepMockHelper;

    private String[] fieldStream = new String[]{ "Number data", "String data" };

    private Object[] row = new Object[]{ 1L, "another data" };

    private Object[] row2 = new Object[]{ 2L, "another data2" };

    private Object[] row3 = new Object[]{ 3L, "another data3" };

    private Object[] wrongRow = new Object[]{ 10000L, "wrong data" };

    private List<Object[]> wrongRows = new ArrayList<Object[]>();

    private List<Object[]> rows = new ArrayList<Object[]>();

    {
        rows.add(row);
        rows.add(row2);
        rows.add(row3);
        wrongRows.add(row);
        wrongRows.add(wrongRow);
        wrongRows.add(row3);
    }

    @Test
    public void testGuiErrors() {
        try {
            int r = wrongRows.size();
            BaseStep step = doOutput(wrongRows, "0");
            ((IngresVectorwiseLoader) (step)).vwLoadMonitorThread.join();
            Assert.assertEquals(0, step.getLinesOutput());
            Assert.assertEquals(r, step.getLinesRead());
            Assert.assertEquals(r, step.getLinesWritten());
            Assert.assertEquals(1, step.getLinesRejected());
            Assert.assertEquals(1, step.getErrors());
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGuiErrorsWithErrorsAllowed() {
        try {
            int r = wrongRows.size();
            BaseStep step = doOutput(wrongRows, "2");
            ((IngresVectorwiseLoader) (step)).vwLoadMonitorThread.join();
            Assert.assertEquals((r - 1), step.getLinesOutput());
            Assert.assertEquals(r, step.getLinesRead());
            Assert.assertEquals(r, step.getLinesWritten());
            Assert.assertEquals(1, step.getLinesRejected());
            Assert.assertEquals(0, step.getErrors());
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGuiSuccess() {
        try {
            int r = rows.size();
            BaseStep step = doOutput(rows, "0");
            ((IngresVectorwiseLoader) (step)).vwLoadMonitorThread.join();
            Assert.assertEquals(r, step.getLinesOutput());
            Assert.assertEquals(r, step.getLinesRead());
            Assert.assertEquals(r, step.getLinesWritten());
            Assert.assertEquals(0, step.getLinesRejected());
            Assert.assertEquals(0, step.getErrors());
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWaitForFinish() {
        try {
            int r = rows.size();
            BaseStep step = doOutput(wrongRows, "2");
            Assert.assertEquals((r - 1), step.getLinesOutput());
            Assert.assertEquals(r, step.getLinesRead());
            Assert.assertEquals(r, step.getLinesWritten());
            Assert.assertEquals(1, step.getLinesRejected());
            Assert.assertEquals(0, step.getErrors());
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }
}

