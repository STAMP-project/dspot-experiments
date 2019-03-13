/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.web;


import Scripts.SCRIPTS;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.web.Scripts.Script;


/**
 *
 *
 * @author Krystof Tulinger
 */
public class ScriptsTest {
    private Scripts scripts;

    @Test
    public void testInstance() {
        scripts.addScript(new Scripts.FileScript("http://example.com/main1.js", 0));
        scripts.addScript(new Scripts.FileScript("http://example.com/main2.js", 0));
        scripts.addScript(new Scripts.FileScript("http://example.com/main3.js", 0));
        Assert.assertEquals(3, scripts.size());
        Assert.assertEquals(scripts.get(0).getScriptData(), "http://example.com/main1.js");
        Assert.assertEquals(scripts.get(0).getPriority(), 0);
        Assert.assertEquals(scripts.get(1).getScriptData(), "http://example.com/main2.js");
        Assert.assertEquals(scripts.get(1).getPriority(), 0);
        Assert.assertEquals(scripts.get(2).getScriptData(), "http://example.com/main3.js");
        Assert.assertEquals(scripts.get(2).getPriority(), 0);
    }

    @Test
    public void testSorted() {
        scripts.addScript(new Scripts.FileScript("http://example.com/main1.js", 3));
        scripts.addScript(new Scripts.FileScript("http://example.com/main2.js", 1));
        scripts.addScript(new Scripts.FileScript("http://example.com/main3.js", 2));
        Assert.assertEquals(3, scripts.size());
        Assert.assertEquals(scripts.get(0).getScriptData(), "http://example.com/main2.js");
        Assert.assertEquals(scripts.get(0).getPriority(), 1);
        Assert.assertEquals(scripts.get(1).getScriptData(), "http://example.com/main3.js");
        Assert.assertEquals(scripts.get(1).getPriority(), 2);
        Assert.assertEquals(scripts.get(2).getScriptData(), "http://example.com/main1.js");
        Assert.assertEquals(scripts.get(2).getPriority(), 3);
    }

    @Test
    public void testContent() {
        scripts.addScript(new Scripts.FileScript("http://example.com/main1.js", 0));
        scripts.addScript(new Scripts.FileScript("http://example.com/main2.js", 0));
        scripts.addScript(new Scripts.FileScript("http://example.com/main3.js", 0));
        Assert.assertEquals(3, scripts.size());
        Assert.assertTrue(scripts.toHtml().contains(("<script type=\"text/javascript\"" + (" src=\"http://example.com/main1.js\"" + " data-priority=\"0\"></script>"))));
        Assert.assertTrue(scripts.toHtml().contains(("<script type=\"text/javascript\"" + (" src=\"http://example.com/main2.js\"" + " data-priority=\"0\"></script>"))));
        Assert.assertTrue(scripts.toHtml().contains(("<script type=\"text/javascript\"" + (" src=\"http://example.com/main3.js\"" + " data-priority=\"0\"></script>"))));
    }

    @Test
    public void testLookup() {
        scripts.addScript("", "utils");
        scripts.addScript("", "jquery");
        scripts.addScript("", "diff");
        scripts.addScript("", "jquery-tablesorter");
        Assert.assertEquals(4, scripts.size());
        int prev = -1;
        for (Script s : scripts) {
            if (prev > (s.getPriority())) {
                Assert.fail(((("The scripts must be sorted in ascending order by the priority, " + prev) + " > ") + (s.getPriority())));
            }
            prev = s.getPriority();
        }
        for (Map.Entry<String, Script> s : SCRIPTS.entrySet()) {
            if ((((!(s.getKey().equals("utils"))) && (!(s.getKey().equals("jquery")))) && (!(s.getKey().equals("jquery-tablesorter")))) && (!(s.getKey().equals("diff")))) {
                continue;
            }
            Assert.assertTrue(((((((((scripts.toHtml()) + " must contain <script type=\"text/javascript\"") + " src=\"/") + (s.getValue().getScriptData())) + "\"") + " data-priority=\"") + (s.getValue().getPriority())) + "\"></script>"), scripts.toHtml().contains((((((("<script type=\"text/javascript\"" + " src=\"/") + (s.getValue().getScriptData())) + "\"") + " data-priority=\"") + (s.getValue().getPriority())) + "\"></script>")));
        }
    }

    @Test
    public void testLookupWithContextPath() {
        String contextPath = "/source";
        scripts.addScript(contextPath, "utils");
        scripts.addScript(contextPath, "jquery");
        scripts.addScript(contextPath, "diff");
        scripts.addScript(contextPath, "jquery-tablesorter");
        Assert.assertEquals(4, scripts.size());
        int prev = -1;
        for (Script s : scripts) {
            if (prev > (s.getPriority())) {
                Assert.fail(((("The scripts must be sorted in ascending order by the priority, " + prev) + " > ") + (s.getPriority())));
            }
            prev = s.getPriority();
        }
        for (Map.Entry<String, Script> s : SCRIPTS.entrySet()) {
            if ((((!(s.getKey().equals("utils"))) && (!(s.getKey().equals("jquery")))) && (!(s.getKey().equals("jquery-tablesorter")))) && (!(s.getKey().equals("diff")))) {
                continue;
            }
            Assert.assertTrue(((((((((((scripts.toHtml()) + " must contain <script type=\"text/javascript\"") + " src=\"") + contextPath) + '/') + (s.getValue().getScriptData())) + "\"") + " data-priority=\"") + (s.getValue().getPriority())) + "\"></script>"), scripts.toHtml().contains((((((((("<script type=\"text/javascript\"" + " src=\"") + contextPath) + '/') + (s.getValue().getScriptData())) + "\"") + " data-priority=\"") + (s.getValue().getPriority())) + "\"></script>")));
        }
    }
}

