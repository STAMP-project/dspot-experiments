/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.template.freemarker.internal;


import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.template.TemplateContextHelper;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tina Tian
 */
public class FreeMarkerTemplateTest {
    @Test
    public void testGet() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME), null, null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        Object result = template.get(FreeMarkerTemplateTest._TEST_KEY);
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof String));
        String stringResult = ((String) (result));
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, stringResult);
    }

    @Test
    public void testPrepare() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME), null, null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        template.prepare(null);
        Object result = template.get(FreeMarkerTemplateTest._TEST_VALUE);
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof String));
        String stringResult = ((String) (result));
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, stringResult);
    }

    @Test
    public void testProcessTemplate1() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME), null, null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        template.processTemplate(unsyncStringWriter);
        String result = unsyncStringWriter.toString();
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, result);
    }

    @Test
    public void testProcessTemplate2() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._WRONG_TEMPLATE_ID), null, null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        try {
            template.processTemplate(unsyncStringWriter);
            Assert.fail();
        } catch (TemplateException te) {
            String message = te.getMessage();
            Assert.assertTrue(message, message.contains(FreeMarkerTemplateTest._WRONG_TEMPLATE_ID));
        }
    }

    @Test
    public void testProcessTemplate3() throws Exception {
        Template template = new FreeMarkerTemplate(new StringTemplateResource(FreeMarkerTemplateTest._WRONG_TEMPLATE_ID, FreeMarkerTemplateTest._TEST_TEMPLATE_CONTENT), null, null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        template.processTemplate(unsyncStringWriter);
        String result = unsyncStringWriter.toString();
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, result);
    }

    @Test
    public void testProcessTemplate4() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME), new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._WRONG_ERROR_TEMPLATE_ID), null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        template.processTemplate(unsyncStringWriter);
        String result = unsyncStringWriter.toString();
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, result);
    }

    @Test
    public void testProcessTemplate5() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._WRONG_TEMPLATE_ID), new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME), null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        template.processTemplate(unsyncStringWriter);
        String result = unsyncStringWriter.toString();
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, result);
    }

    @Test
    public void testProcessTemplate6() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._WRONG_TEMPLATE_ID), new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._WRONG_ERROR_TEMPLATE_ID), null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        try {
            template.processTemplate(unsyncStringWriter);
            Assert.fail();
        } catch (TemplateException te) {
            String message = te.getMessage();
            Assert.assertTrue(message, message.contains(FreeMarkerTemplateTest._WRONG_ERROR_TEMPLATE_ID));
        }
    }

    @Test
    public void testProcessTemplate7() throws Exception {
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._WRONG_TEMPLATE_ID), new StringTemplateResource(FreeMarkerTemplateTest._WRONG_ERROR_TEMPLATE_ID, FreeMarkerTemplateTest._TEST_TEMPLATE_CONTENT), null, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        template.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        template.processTemplate(unsyncStringWriter);
        String result = unsyncStringWriter.toString();
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, result);
    }

    @Test
    public void testProcessTemplate8() throws Exception {
        Map<String, Object> context = new HashMap<>();
        context.put(FreeMarkerTemplateTest._TEST_KEY, FreeMarkerTemplateTest._TEST_VALUE);
        Template template = new FreeMarkerTemplate(new FreeMarkerTemplateTest.MockTemplateResource(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME), null, context, _configuration, _templateContextHelper, _freeMarkerEngineConfiguration.resourceModificationCheck());
        UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();
        template.processTemplate(unsyncStringWriter);
        String result = unsyncStringWriter.toString();
        Assert.assertEquals(FreeMarkerTemplateTest._TEST_VALUE, result);
    }

    private static final String _TEMPLATE_FILE_NAME = "test.ftl";

    private static final String _TEST_KEY = "TEST_KEY";

    private static final String _TEST_TEMPLATE_CONTENT = ("${" + (FreeMarkerTemplateTest._TEST_KEY)) + "}";

    private static final String _TEST_VALUE = "TEST_VALUE";

    private static final String _WRONG_ERROR_TEMPLATE_ID = "WRONG_ERROR_TEMPLATE_ID";

    private static final String _WRONG_TEMPLATE_ID = "WRONG_TEMPLATE_ID";

    private Configuration _configuration;

    private final FreeMarkerEngineConfiguration _freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(FreeMarkerEngineConfiguration.class, Collections.emptyMap());

    private TemplateContextHelper _templateContextHelper;

    private static class MockTemplateContextHelper extends TemplateContextHelper {
        @Override
        public Map<String, Object> getHelperUtilities(ClassLoader classLoader, boolean restricted) {
            return Collections.emptyMap();
        }

        @Override
        public Set<String> getRestrictedVariables() {
            return Collections.emptySet();
        }

        @Override
        public void prepare(Map<String, Object> contextObjects, HttpServletRequest request) {
            String testValue = ((String) (contextObjects.get(FreeMarkerTemplateTest._TEST_KEY)));
            contextObjects.put(testValue, testValue);
        }
    }

    private static class MockTemplateResource implements TemplateResource {
        /**
         * The empty constructor is required by {@link java.io.Externalizable}.
         * Do not use this for any other purpose.
         */
        @SuppressWarnings("unused")
        public MockTemplateResource() {
        }

        public MockTemplateResource(String templateId) {
            _templateId = templateId;
        }

        @Override
        public long getLastModified() {
            return _lastModified;
        }

        @Override
        public Reader getReader() throws IOException {
            if (_templateId.equals(FreeMarkerTemplateTest._TEMPLATE_FILE_NAME)) {
                return new StringReader(FreeMarkerTemplateTest._TEST_TEMPLATE_CONTENT);
            }
            throw new ParseException(("Unable to get reader for template source " + (_templateId)), 0, 0);
        }

        @Override
        public String getTemplateId() {
            return _templateId;
        }

        @Override
        public void readExternal(ObjectInput objectInput) throws IOException {
            _lastModified = objectInput.readLong();
            _templateId = objectInput.readUTF();
        }

        @Override
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            objectOutput.writeLong(_lastModified);
            objectOutput.writeUTF(_templateId);
        }

        private long _lastModified = System.currentTimeMillis();

        private String _templateId;
    }
}

