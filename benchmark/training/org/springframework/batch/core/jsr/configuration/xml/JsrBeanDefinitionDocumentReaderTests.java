/**
 * Copyright 2013-2017 the original author or authors.
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
package org.springframework.batch.core.jsr.configuration.xml;


import java.util.Properties;
import javax.batch.api.Batchlet;
import javax.batch.runtime.JobExecution;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.jsr.AbstractJsrTestCase;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;


/**
 * <p>
 * Test cases around {@link JsrBeanDefinitionDocumentReader}.
 * </p>
 *
 * @author Chris Schaefer
 */
public class JsrBeanDefinitionDocumentReaderTests extends AbstractJsrTestCase {
    private static final String JOB_PARAMETERS_BEAN_DEFINITION_NAME = "jsr_jobParameters";

    private Log logger = LogFactory.getLog(getClass());

    private DocumentLoader documentLoader = new DefaultDocumentLoader();

    private ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);

    @Test
    @SuppressWarnings("resource")
    public void testGetJobParameters() {
        Properties jobParameters = new Properties();
        jobParameters.setProperty("jobParameter1", "jobParameter1Value");
        jobParameters.setProperty("jobParameter2", "jobParameter2Value");
        JsrXmlApplicationContext applicationContext = new JsrXmlApplicationContext(jobParameters);
        applicationContext.setValidating(false);
        applicationContext.load(new ClassPathResource("jsrBaseContext.xml"), new ClassPathResource("/META-INF/batch.xml"), new ClassPathResource("/META-INF/batch-jobs/jsrPropertyPreparseTestJob.xml"));
        applicationContext.refresh();
        BeanDefinition beanDefinition = applicationContext.getBeanDefinition(JsrBeanDefinitionDocumentReaderTests.JOB_PARAMETERS_BEAN_DEFINITION_NAME);
        Properties processedJobParameters = ((Properties) (beanDefinition.getConstructorArgumentValues().getGenericArgumentValue(Properties.class).getValue()));
        Assert.assertNotNull(processedJobParameters);
        Assert.assertTrue("Wrong number of job parameters", ((processedJobParameters.size()) == 2));
        Assert.assertEquals("jobParameter1Value", processedJobParameters.getProperty("jobParameter1"));
        Assert.assertEquals("jobParameter2Value", processedJobParameters.getProperty("jobParameter2"));
    }

    @Test
    public void testGetJobProperties() {
        Document document = getDocument("/META-INF/batch-jobs/jsrPropertyPreparseTestJob.xml");
        @SuppressWarnings("resource")
        JsrXmlApplicationContext applicationContext = new JsrXmlApplicationContext();
        JsrBeanDefinitionDocumentReader documentReader = new JsrBeanDefinitionDocumentReader(applicationContext);
        documentReader.initProperties(document.getDocumentElement());
        Properties documentJobProperties = documentReader.getJobProperties();
        Assert.assertNotNull(documentJobProperties);
        Assert.assertTrue("Wrong number of job properties", ((documentJobProperties.size()) == 3));
        Assert.assertEquals("jobProperty1Value", documentJobProperties.getProperty("jobProperty1"));
        Assert.assertEquals("jobProperty1Value", documentJobProperties.getProperty("jobProperty2"));
        Assert.assertEquals("", documentJobProperties.getProperty("jobProperty3"));
    }

    @Test
    public void testJobParametersResolution() {
        Properties jobParameters = new Properties();
        jobParameters.setProperty("jobParameter1", "myfile.txt");
        jobParameters.setProperty("jobParameter2", "#{jobProperties['jobProperty2']}");
        jobParameters.setProperty("jobParameter3", "#{jobParameters['jobParameter1']}");
        @SuppressWarnings("resource")
        JsrXmlApplicationContext applicationContext = new JsrXmlApplicationContext(jobParameters);
        applicationContext.setValidating(false);
        applicationContext.load(new ClassPathResource("jsrBaseContext.xml"), new ClassPathResource("/META-INF/batch.xml"), new ClassPathResource("/META-INF/batch-jobs/jsrPropertyPreparseTestJob.xml"));
        applicationContext.refresh();
        Document document = getDocument("/META-INF/batch-jobs/jsrPropertyPreparseTestJob.xml");
        JsrBeanDefinitionDocumentReader documentReader = new JsrBeanDefinitionDocumentReader(applicationContext);
        documentReader.initProperties(document.getDocumentElement());
        Properties resolvedParameters = documentReader.getJobParameters();
        Assert.assertNotNull(resolvedParameters);
        Assert.assertTrue("Wrong number of job parameters", ((resolvedParameters.size()) == 3));
        Assert.assertEquals("myfile.txt", resolvedParameters.getProperty("jobParameter1"));
        Assert.assertEquals("jobProperty1Value", resolvedParameters.getProperty("jobParameter2"));
        Assert.assertEquals("myfile.txt", resolvedParameters.getProperty("jobParameter3"));
    }

    @Test
    public void testJobPropertyResolution() {
        Properties jobParameters = new Properties();
        jobParameters.setProperty("file.name", "myfile.txt");
        @SuppressWarnings("resource")
        JsrXmlApplicationContext applicationContext = new JsrXmlApplicationContext(jobParameters);
        applicationContext.setValidating(false);
        applicationContext.load(new ClassPathResource("jsrBaseContext.xml"), new ClassPathResource("/META-INF/batch.xml"), new ClassPathResource("/META-INF/batch-jobs/jsrPropertyPreparseTestJob.xml"));
        applicationContext.refresh();
        Document document = getDocument("/META-INF/batch-jobs/jsrPropertyPreparseTestJob.xml");
        JsrBeanDefinitionDocumentReader documentReader = new JsrBeanDefinitionDocumentReader(applicationContext);
        documentReader.initProperties(document.getDocumentElement());
        Properties resolvedProperties = documentReader.getJobProperties();
        Assert.assertNotNull(resolvedProperties);
        Assert.assertTrue("Wrong number of job properties", ((resolvedProperties.size()) == 3));
        Assert.assertEquals("jobProperty1Value", resolvedProperties.getProperty("jobProperty1"));
        Assert.assertEquals("jobProperty1Value", resolvedProperties.getProperty("jobProperty2"));
        Assert.assertEquals("myfile.txt", resolvedProperties.getProperty("jobProperty3"));
    }

    @SuppressWarnings("resource")
    @Test
    public void testGenerationOfBeanDefinitionsForMultipleReferences() throws Exception {
        JsrXmlApplicationContext applicationContext = new JsrXmlApplicationContext(new Properties());
        applicationContext.setValidating(false);
        applicationContext.load(new ClassPathResource("jsrBaseContext.xml"), new ClassPathResource("/META-INF/batch.xml"), new ClassPathResource("/META-INF/batch-jobs/jsrUniqueInstanceTests.xml"));
        applicationContext.refresh();
        Assert.assertTrue("exitStatusSettingStepListener bean definition not found", applicationContext.containsBeanDefinition("exitStatusSettingStepListener"));
        Assert.assertTrue("exitStatusSettingStepListener1 bean definition not found", applicationContext.containsBeanDefinition("exitStatusSettingStepListener1"));
        Assert.assertTrue("exitStatusSettingStepListener2 bean definition not found", applicationContext.containsBeanDefinition("exitStatusSettingStepListener2"));
        Assert.assertTrue("exitStatusSettingStepListener3 bean definition not found", applicationContext.containsBeanDefinition("exitStatusSettingStepListener3"));
        Assert.assertTrue("exitStatusSettingStepListenerClassBeanDefinition bean definition not found", applicationContext.containsBeanDefinition("org.springframework.batch.core.jsr.step.listener.ExitStatusSettingStepListener"));
        Assert.assertTrue("exitStatusSettingStepListener1ClassBeanDefinition bean definition not found", applicationContext.containsBeanDefinition("org.springframework.batch.core.jsr.step.listener.ExitStatusSettingStepListener1"));
        Assert.assertTrue("exitStatusSettingStepListener2ClassBeanDefinition bean definition not found", applicationContext.containsBeanDefinition("org.springframework.batch.core.jsr.step.listener.ExitStatusSettingStepListener2"));
        Assert.assertTrue("exitStatusSettingStepListener3ClassBeanDefinition bean definition not found", applicationContext.containsBeanDefinition("org.springframework.batch.core.jsr.step.listener.ExitStatusSettingStepListener3"));
        Assert.assertTrue("testBatchlet bean definition not found", applicationContext.containsBeanDefinition("testBatchlet"));
        Assert.assertTrue("testBatchlet1 bean definition not found", applicationContext.containsBeanDefinition("testBatchlet1"));
    }

    @Test
    public void testArtifactUniqueness() throws Exception {
        JobExecution jobExecution = AbstractJsrTestCase.runJob("jsrUniqueInstanceTests", new Properties(), 10000L);
        String exitStatus = jobExecution.getExitStatus();
        Assert.assertTrue("Exit status must contain listener3", exitStatus.contains("listener3"));
        exitStatus = exitStatus.replace("listener3", "");
        Assert.assertTrue("Exit status must contain listener2", exitStatus.contains("listener2"));
        exitStatus = exitStatus.replace("listener2", "");
        Assert.assertTrue("Exit status must contain listener1", exitStatus.contains("listener1"));
        exitStatus = exitStatus.replace("listener1", "");
        Assert.assertTrue("Exit status must contain listener0", exitStatus.contains("listener0"));
        exitStatus = exitStatus.replace("listener0", "");
        Assert.assertTrue("Exit status must contain listener7", exitStatus.contains("listener7"));
        exitStatus = exitStatus.replace("listener7", "");
        Assert.assertTrue("Exit status must contain listener6", exitStatus.contains("listener6"));
        exitStatus = exitStatus.replace("listener6", "");
        Assert.assertTrue("Exit status must contain listener5", exitStatus.contains("listener5"));
        exitStatus = exitStatus.replace("listener5", "");
        Assert.assertTrue("Exit status must contain listener4", exitStatus.contains("listener4"));
        exitStatus = exitStatus.replace("listener4", "");
        Assert.assertTrue("exitStatus must be empty", "".equals(exitStatus));
    }

    @Test
    @SuppressWarnings("resource")
    public void testGenerationOfSpringBeanDefinitionsForMultipleReferences() {
        JsrXmlApplicationContext applicationContext = new JsrXmlApplicationContext(new Properties());
        applicationContext.setValidating(false);
        applicationContext.load(new ClassPathResource("jsrBaseContext.xml"), new ClassPathResource("/META-INF/batch-jobs/jsrSpringInstanceTests.xml"));
        applicationContext.refresh();
        Assert.assertTrue("exitStatusSettingStepListener bean definition not found", applicationContext.containsBeanDefinition("exitStatusSettingStepListener"));
        Assert.assertTrue("scopedTarget.exitStatusSettingStepListener bean definition not found", applicationContext.containsBeanDefinition("scopedTarget.exitStatusSettingStepListener"));
        BeanDefinition exitStatusSettingStepListenerBeanDefinition = applicationContext.getBeanDefinition("scopedTarget.exitStatusSettingStepListener");
        Assert.assertTrue("step".equals(exitStatusSettingStepListenerBeanDefinition.getScope()));
        Assert.assertTrue("Should not contain bean definition for exitStatusSettingStepListener1", (!(applicationContext.containsBeanDefinition("exitStatusSettingStepListener1"))));
        Assert.assertTrue("Should not contain bean definition for exitStatusSettingStepListener2", (!(applicationContext.containsBeanDefinition("exitStatusSettingStepListener2"))));
        Assert.assertTrue("Should not contain bean definition for exitStatusSettingStepListener3", (!(applicationContext.containsBeanDefinition("exitStatusSettingStepListener3"))));
        Assert.assertTrue("Should not contain bean definition for testBatchlet1", (!(applicationContext.containsBeanDefinition("testBatchlet1"))));
        Assert.assertTrue("Should not contain bean definition for testBatchlet2", (!(applicationContext.containsBeanDefinition("testBatchlet2"))));
        Assert.assertTrue("testBatchlet bean definition not found", applicationContext.containsBeanDefinition("testBatchlet"));
        BeanDefinition testBatchletBeanDefinition = applicationContext.getBeanDefinition("testBatchlet");
        Assert.assertTrue("singleton".equals(testBatchletBeanDefinition.getScope()));
    }

    @Test
    public void testSpringArtifactUniqueness() throws Exception {
        JobExecution jobExecution = AbstractJsrTestCase.runJob("jsrSpringInstanceTests", new Properties(), 10000L);
        String exitStatus = jobExecution.getExitStatus();
        Assert.assertTrue("Exit status must contain listener1", exitStatus.contains("listener1"));
        Assert.assertTrue("exitStatus must contain 2 listener1 values", ((StringUtils.countOccurrencesOf(exitStatus, "listener1")) == 2));
        exitStatus = exitStatus.replace("listener1", "");
        Assert.assertTrue("Exit status must contain listener4", exitStatus.contains("listener4"));
        Assert.assertTrue("exitStatus must contain 2 listener4 values", ((StringUtils.countOccurrencesOf(exitStatus, "listener4")) == 2));
        exitStatus = exitStatus.replace("listener4", "");
        Assert.assertTrue("exitStatus must be empty", "".equals(exitStatus));
    }

    public static class TestBatchlet implements Batchlet {
        @Override
        public String process() throws Exception {
            return null;
        }

        @Override
        public void stop() throws Exception {
        }
    }
}

