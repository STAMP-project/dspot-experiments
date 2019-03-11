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
package azkaban.jobtype;


import azkaban.jobExecutor.Job;
import azkaban.utils.Props;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test the flow run, especially with embedded flows. Files are in unit/plugins/jobtypes
 */
public class JobTypeManagerTest {
    public static final String TEST_PLUGIN_DIR = "jobtypes_test";

    private final Logger logger = Logger.getLogger(JobTypeManagerTest.class);

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private String testPluginDirPath;

    private JobTypeManager manager;

    public JobTypeManagerTest() {
    }

    /**
     * Tests that the common and common private properties are loaded correctly
     */
    @Test
    public void testCommonPluginProps() throws Exception {
        final JobTypePluginSet pluginSet = this.manager.getJobTypePluginSet();
        final Props props = pluginSet.getCommonPluginJobProps();
        System.out.println(props.toString());
        Assert.assertEquals("commonprop1", props.getString("commonprop1"));
        Assert.assertEquals("commonprop2", props.getString("commonprop2"));
        Assert.assertEquals("commonprop3", props.getString("commonprop3"));
        final Props priv = pluginSet.getCommonPluginLoadProps();
        Assert.assertEquals("commonprivate1", priv.getString("commonprivate1"));
        Assert.assertEquals("commonprivate2", priv.getString("commonprivate2"));
        Assert.assertEquals("commonprivate3", priv.getString("commonprivate3"));
    }

    /**
     * Tests that the proper classes were loaded and that the common and the load properties are
     * properly loaded.
     */
    @Test
    public void testLoadedClasses() throws Exception {
        final JobTypePluginSet pluginSet = this.manager.getJobTypePluginSet();
        final Props props = pluginSet.getCommonPluginJobProps();
        System.out.println(props.toString());
        Assert.assertEquals("commonprop1", props.getString("commonprop1"));
        Assert.assertEquals("commonprop2", props.getString("commonprop2"));
        Assert.assertEquals("commonprop3", props.getString("commonprop3"));
        Assert.assertNull(props.get("commonprivate1"));
        final Props priv = pluginSet.getCommonPluginLoadProps();
        Assert.assertEquals("commonprivate1", priv.getString("commonprivate1"));
        Assert.assertEquals("commonprivate2", priv.getString("commonprivate2"));
        Assert.assertEquals("commonprivate3", priv.getString("commonprivate3"));
        // Testing the anothertestjobtype
        final Class<? extends Job> aPluginClass = pluginSet.getPluginClass("anothertestjob");
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob", aPluginClass.getName());
        final Props ajobProps = pluginSet.getPluginJobProps("anothertestjob");
        final Props aloadProps = pluginSet.getPluginLoaderProps("anothertestjob");
        // Loader props
        Assert.assertEquals("lib/*", aloadProps.get("jobtype.classpath"));
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob", aloadProps.get("jobtype.class"));
        Assert.assertEquals("commonprivate1", aloadProps.get("commonprivate1"));
        Assert.assertEquals("commonprivate2", aloadProps.get("commonprivate2"));
        Assert.assertEquals("commonprivate3", aloadProps.get("commonprivate3"));
        // Job props
        Assert.assertEquals("commonprop1", ajobProps.get("commonprop1"));
        Assert.assertEquals("commonprop2", ajobProps.get("commonprop2"));
        Assert.assertEquals("commonprop3", ajobProps.get("commonprop3"));
        Assert.assertNull(ajobProps.get("commonprivate1"));
        final Class<? extends Job> tPluginClass = pluginSet.getPluginClass("testjob");
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob2", tPluginClass.getName());
        final Props tjobProps = pluginSet.getPluginJobProps("testjob");
        final Props tloadProps = pluginSet.getPluginLoaderProps("testjob");
        // Loader props
        Assert.assertNull(tloadProps.get("jobtype.classpath"));
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob2", tloadProps.get("jobtype.class"));
        Assert.assertEquals("commonprivate1", tloadProps.get("commonprivate1"));
        Assert.assertEquals("commonprivate2", tloadProps.get("commonprivate2"));
        Assert.assertEquals("private3", tloadProps.get("commonprivate3"));
        Assert.assertEquals("0", tloadProps.get("testprivate"));
        // Job props
        Assert.assertEquals("commonprop1", tjobProps.get("commonprop1"));
        Assert.assertEquals("commonprop2", tjobProps.get("commonprop2"));
        Assert.assertEquals("1", tjobProps.get("pluginprops1"));
        Assert.assertEquals("2", tjobProps.get("pluginprops2"));
        Assert.assertEquals("3", tjobProps.get("pluginprops3"));
        Assert.assertEquals("pluginprops", tjobProps.get("commonprop3"));
        // Testing that the private properties aren't shared with the public ones
        Assert.assertNull(tjobProps.get("commonprivate1"));
        Assert.assertNull(tjobProps.get("testprivate"));
    }

    /**
     * Test building classes
     */
    @Test
    public void testBuildClass() throws Exception {
        final Props jobProps = new Props();
        jobProps.put("type", "anothertestjob");
        jobProps.put("test", "test1");
        jobProps.put("pluginprops3", "4");
        final Job job = this.manager.buildJobExecutor("anothertestjob", jobProps, this.logger);
        Assert.assertTrue((job instanceof FakeJavaJob));
        final FakeJavaJob fjj = ((FakeJavaJob) (job));
        final Props props = getJobProps();
        Assert.assertEquals("test1", props.get("test"));
        Assert.assertNull(props.get("pluginprops1"));
        Assert.assertEquals("4", props.get("pluginprops3"));
        Assert.assertEquals("commonprop1", props.get("commonprop1"));
        Assert.assertEquals("commonprop2", props.get("commonprop2"));
        Assert.assertEquals("commonprop3", props.get("commonprop3"));
        Assert.assertNull(props.get("commonprivate1"));
    }

    /**
     * Test building classes 2
     */
    @Test
    public void testBuildClass2() throws Exception {
        final Props jobProps = new Props();
        jobProps.put("type", "testjob");
        jobProps.put("test", "test1");
        jobProps.put("pluginprops3", "4");
        final Job job = this.manager.buildJobExecutor("testjob", jobProps, this.logger);
        Assert.assertTrue((job instanceof FakeJavaJob2));
        final FakeJavaJob2 fjj = ((FakeJavaJob2) (job));
        final Props props = getJobProps();
        Assert.assertEquals("test1", props.get("test"));
        Assert.assertEquals("1", props.get("pluginprops1"));
        Assert.assertEquals("2", props.get("pluginprops2"));
        Assert.assertEquals("4", props.get("pluginprops3"));// Overridden value

        Assert.assertEquals("commonprop1", props.get("commonprop1"));
        Assert.assertEquals("commonprop2", props.get("commonprop2"));
        Assert.assertEquals("pluginprops", props.get("commonprop3"));
        Assert.assertNull(props.get("commonprivate1"));
    }

    /**
     * Test out reloading properties
     */
    @Test
    public void testResetPlugins() throws Exception {
        // Add a plugins file to the anothertestjob folder
        final File anothertestfolder = new File(((this.testPluginDirPath) + "/anothertestjob"));
        final Props pluginProps = new Props();
        pluginProps.put("test1", "1");
        pluginProps.put("test2", "2");
        pluginProps.put("pluginprops3", "4");
        pluginProps.storeFlattened(new File(anothertestfolder, "plugin.properties"));
        // clone the testjob folder
        final File testFolder = new File(((this.testPluginDirPath) + "/testjob"));
        FileUtils.copyDirectory(testFolder, new File(((this.testPluginDirPath) + "/newtestjob")));
        // change the common properties
        final Props commonPlugin = new Props(null, ((this.testPluginDirPath) + "/common.properties"));
        commonPlugin.put("commonprop1", "1");
        commonPlugin.put("newcommonprop1", "2");
        commonPlugin.removeLocal("commonprop2");
        commonPlugin.storeFlattened(new File(((this.testPluginDirPath) + "/common.properties")));
        // change the common properties
        final Props commonPrivate = new Props(null, ((this.testPluginDirPath) + "/commonprivate.properties"));
        commonPrivate.put("commonprivate1", "1");
        commonPrivate.put("newcommonprivate1", "2");
        commonPrivate.removeLocal("commonprivate2");
        commonPrivate.storeFlattened(new File(((this.testPluginDirPath) + "/commonprivate.properties")));
        // change testjob private property
        final Props loadProps = new Props(null, ((this.testPluginDirPath) + "/testjob/private.properties"));
        loadProps.put("privatetest", "test");
        // Reload the plugins here!!
        this.manager.loadPlugins();
        // Checkout common props
        final JobTypePluginSet pluginSet = this.manager.getJobTypePluginSet();
        final Props commonProps = pluginSet.getCommonPluginJobProps();
        Assert.assertEquals("1", commonProps.get("commonprop1"));
        Assert.assertEquals("commonprop3", commonProps.get("commonprop3"));
        Assert.assertEquals("2", commonProps.get("newcommonprop1"));
        Assert.assertNull(commonProps.get("commonprop2"));
        // Checkout common private
        final Props commonPrivateProps = pluginSet.getCommonPluginLoadProps();
        Assert.assertEquals("1", commonPrivateProps.get("commonprivate1"));
        Assert.assertEquals("commonprivate3", commonPrivateProps.get("commonprivate3"));
        Assert.assertEquals("2", commonPrivateProps.get("newcommonprivate1"));
        Assert.assertNull(commonPrivateProps.get("commonprivate2"));
        // Verify anothertestjob changes
        final Class<? extends Job> atjClass = pluginSet.getPluginClass("anothertestjob");
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob", atjClass.getName());
        final Props ajobProps = pluginSet.getPluginJobProps("anothertestjob");
        Assert.assertEquals("1", ajobProps.get("test1"));
        Assert.assertEquals("2", ajobProps.get("test2"));
        Assert.assertEquals("4", ajobProps.get("pluginprops3"));
        Assert.assertEquals("commonprop3", ajobProps.get("commonprop3"));
        final Props aloadProps = pluginSet.getPluginLoaderProps("anothertestjob");
        Assert.assertEquals("1", aloadProps.get("commonprivate1"));
        Assert.assertNull(aloadProps.get("commonprivate2"));
        Assert.assertEquals("commonprivate3", aloadProps.get("commonprivate3"));
        // Verify testjob changes
        final Class<? extends Job> tjClass = pluginSet.getPluginClass("testjob");
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob2", tjClass.getName());
        final Props tjobProps = pluginSet.getPluginJobProps("testjob");
        Assert.assertEquals("1", tjobProps.get("commonprop1"));
        Assert.assertEquals("2", tjobProps.get("newcommonprop1"));
        Assert.assertEquals("1", tjobProps.get("pluginprops1"));
        Assert.assertEquals("2", tjobProps.get("pluginprops2"));
        Assert.assertEquals("3", tjobProps.get("pluginprops3"));
        Assert.assertEquals("pluginprops", tjobProps.get("commonprop3"));
        Assert.assertNull(tjobProps.get("commonprop2"));
        final Props tloadProps = pluginSet.getPluginLoaderProps("testjob");
        Assert.assertNull(tloadProps.get("jobtype.classpath"));
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob2", tloadProps.get("jobtype.class"));
        Assert.assertEquals("1", tloadProps.get("commonprivate1"));
        Assert.assertNull(tloadProps.get("commonprivate2"));
        Assert.assertEquals("private3", tloadProps.get("commonprivate3"));
        // Verify newtestjob
        final Class<? extends Job> ntPluginClass = pluginSet.getPluginClass("newtestjob");
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob2", ntPluginClass.getName());
        final Props ntjobProps = pluginSet.getPluginJobProps("newtestjob");
        final Props ntloadProps = pluginSet.getPluginLoaderProps("newtestjob");
        // Loader props
        Assert.assertNull(ntloadProps.get("jobtype.classpath"));
        Assert.assertEquals("azkaban.jobtype.FakeJavaJob2", ntloadProps.get("jobtype.class"));
        Assert.assertEquals("1", ntloadProps.get("commonprivate1"));
        Assert.assertNull(ntloadProps.get("commonprivate2"));
        Assert.assertEquals("private3", ntloadProps.get("commonprivate3"));
        Assert.assertEquals("0", ntloadProps.get("testprivate"));
        // Job props
        Assert.assertEquals("1", ntjobProps.get("commonprop1"));
        Assert.assertNull(ntjobProps.get("commonprop2"));
        Assert.assertEquals("1", ntjobProps.get("pluginprops1"));
        Assert.assertEquals("2", ntjobProps.get("pluginprops2"));
        Assert.assertEquals("3", ntjobProps.get("pluginprops3"));
        Assert.assertEquals("pluginprops", ntjobProps.get("commonprop3"));
    }
}

