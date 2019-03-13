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
package com.liferay.petra.mail.template.internal;


import StringPool.BLANK;
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adolfo P?rez
 */
public class DefaultMailTemplateTest {
    @Test
    public void testEmptyTemplate() throws Exception {
        MailTemplate mailTemplate = new DefaultMailTemplate(StringPool.BLANK, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals(BLANK, result);
    }

    @Test
    public void testHrefAbsoluteURLRewriting() throws Exception {
        String template = "<a href=\"/resource\">";
        MailTemplate mailTemplate = new DefaultMailTemplate(template, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals("<a href=\"http://liferay.com/resource\">", result);
    }

    @Test
    public void testSrcAbsoluteURLRewriting() throws Exception {
        String template = "<img src=\"/resource\">";
        MailTemplate mailTemplate = new DefaultMailTemplate(template, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals("<img src=\"http://liferay.com/resource\">", result);
    }

    @Test
    public void testTemplateWithEscapedPlaceholder() throws Exception {
        String template = "This template contains a [$ESCAPED_VALUE$]";
        MailTemplate mailTemplate = new DefaultMailTemplate(template, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals("This template contains a <>", result);
    }

    @Test
    public void testTemplateWithLocalizedPlaceholder() throws Exception {
        String template = "This template is in [$LOCALIZED_VALUE$]";
        MailTemplate mailTemplate = new DefaultMailTemplate(template, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals("This template is in en", result);
    }

    @Test
    public void testTemplateWithNoPlaceholder() throws Exception {
        String template = StringUtil.randomString();
        MailTemplate mailTemplate = new DefaultMailTemplate(template, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals(template, result);
    }

    @Test
    public void testTemplateWithPlaceholder() throws Exception {
        String template = "This template contains a [$PLACEHOLDER$]";
        MailTemplate mailTemplate = new DefaultMailTemplate(template, false);
        String result = mailTemplate.renderAsString(Locale.ENGLISH, _mailTemplateContext);
        Assert.assertEquals("This template contains a replacement", result);
    }

    private MailTemplateContext _mailTemplateContext;
}

