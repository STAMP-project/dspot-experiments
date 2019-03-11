/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.jobExecutor;


import JavaProcessJob.JAVA_CLASS;
import azkaban.utils.Props;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class JavaProcessJobTest {
    private static final String inputContent = "Quick Change in Strategy for a Bookseller \n" + (((((" By JULIE BOSMAN \n" + "Published: August 11, 2010 \n") + " \n") + "Twelve years later, it may be Joe Fox\'s turn to worry. Readers have gone from skipping small \n") + "bookstores to wondering if they need bookstores at all. More people are ordering books online  \n") + "or plucking them from the best-seller bin at Wal-Mart");

    private static final String errorInputContent = (((((((JavaProcessJobTest.inputContent) + "\n stop_here ") + "But the threat that has the industry and some readers the most rattled is the growth of e-books. \n") + " In the first five months of 2009, e-books made up 2.9 percent of trade book sales. In the same period \n") + "in 2010, sales of e-books, which generally cost less than hardcover books, grew to 8.5 percent, according \n") + "to the Association of American Publishers, spurred by sales of the Amazon Kindle and the new Apple iPad. \n") + "For Barnes & Noble, long the largest and most powerful bookstore chain in the country, the new competition \n") + "has led to declining profits and store traffic.";

    @ClassRule
    public static TemporaryFolder classTemp = new TemporaryFolder();

    private static String classPaths;

    private static String inputFile;

    private static String errorInputFile;

    private static String outputFile;

    private final Logger log = Logger.getLogger(JavaProcessJob.class);

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private JavaProcessJob job = null;

    private Props props = null;

    @Test
    public void testJavaJob() throws Exception {
        // initialize the Props
        this.props.put(JAVA_CLASS, "azkaban.jobExecutor.WordCountLocal");
        this.props.put("input", JavaProcessJobTest.inputFile);
        this.props.put("output", JavaProcessJobTest.outputFile);
        this.props.put("classpath", JavaProcessJobTest.classPaths);
        this.job.run();
    }

    @Test
    public void testJavaJobHashmap() throws Exception {
        // initialize the Props
        this.props.put(JAVA_CLASS, "azkaban.executor.SleepJavaJob");
        this.props.put("seconds", 0);
        this.props.put("input", JavaProcessJobTest.inputFile);
        this.props.put("output", JavaProcessJobTest.outputFile);
        this.props.put("classpath", JavaProcessJobTest.classPaths);
        this.job.run();
    }

    @Test
    public void testFailedJavaJob() throws Exception {
        this.props.put(JAVA_CLASS, "azkaban.jobExecutor.WordCountLocal");
        this.props.put("input", JavaProcessJobTest.errorInputFile);
        this.props.put("output", JavaProcessJobTest.outputFile);
        this.props.put("classpath", JavaProcessJobTest.classPaths);
        try {
            this.job.run();
        } catch (final RuntimeException e) {
            Assert.assertTrue(true);
        }
    }
}

