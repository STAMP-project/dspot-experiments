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
package com.liferay.poshi.runner.pql;


import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 *
 * @author Michael Hashimoto
 */
public class PQLEntityFactoryTest extends TestCase {
    @Test
    public void testPQLQueryGetPQLResultComparativeOperator() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        Set<String> queries = new TreeSet<>();
        queries.add("component.names !~ 'Journal'");
        queries.add("component.names ~ 'Message Boards'");
        queries.add("portal.smoke != false");
        queries.add("portal.smoke == true");
        queries.add("priority < 6");
        queries.add("priority <= 5");
        queries.add("priority > 4");
        queries.add("priority >= 5");
        queries.add("priority < 5.1");
        queries.add("priority <= 5.1");
        queries.add("priority > 4.1");
        queries.add("priority >= 4.9");
        for (String query : queries) {
            PQLEntityFactoryTest._validateGetPQLResult(query, Boolean.TRUE, properties);
        }
        queries = new TreeSet<>();
        queries.add("component.names !~ 'Message Boards'");
        queries.add("component.names ~ 'Journal'");
        queries.add("portal.smoke != true");
        queries.add("portal.smoke == false");
        queries.add("priority < 4");
        queries.add("priority <= 4");
        queries.add("priority > 6");
        queries.add("priority >= 6");
        queries.add("priority < 4.1");
        queries.add("priority <= 4.9");
        queries.add("priority > 5.1");
        queries.add("priority >= 5.1");
        for (String query : queries) {
            PQLEntityFactoryTest._validateGetPQLResult(query, Boolean.FALSE, properties);
        }
    }

    @Test
    public void testPQLQueryGetPQLResultComparativeOperatorError() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        PQLEntityFactoryTest._validateGetPQLResultError("true ==", "Invalid value: true ==", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("false ==", "Invalid value: false ==", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("== true", "Invalid value: == true", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("== false", "Invalid value: == false", properties);
    }

    @Test
    public void testPQLQueryGetPQLResultConditionalOperator() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        Set<String> queries = new TreeSet<>();
        queries.add("portal.smoke == true AND portal.smoke != false");
        queries.add("portal.smoke == true OR portal.smoke == false");
        queries.add("false OR true");
        queries.add("true AND true");
        queries.add("true OR false");
        queries.add("true OR true");
        for (String query : queries) {
            PQLEntityFactoryTest._validateGetPQLResult(query, Boolean.TRUE, properties);
        }
        queries = new TreeSet<>();
        queries.add("portal.smoke != true OR portal.smoke == false");
        queries.add("portal.smoke == true AND portal.smoke == false");
        queries.add("false AND false");
        queries.add("false AND true");
        queries.add("false OR false");
        queries.add("true AND false");
        for (String query : queries) {
            PQLEntityFactoryTest._validateGetPQLResult(query, Boolean.FALSE, properties);
        }
    }

    @Test
    public void testPQLQueryGetPQLResultConditionalOperatorError() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        PQLEntityFactoryTest._validateGetPQLResultError("AND true == true", "Invalid value: AND true == true", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("true == true AND", "Invalid value: true == true AND", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("OR true == true", "Invalid value: OR true == true", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("true == true OR", "Invalid value: true == true OR", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("true == true AND AND false == false", "Invalid value: AND false == false", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("(true == true) AND", "Invalid value: (true == true) AND", properties);
        PQLEntityFactoryTest._validateGetPQLResultError("(true == true) OR", "Invalid value: (true == true) OR", properties);
    }

    @Test
    public void testPQLQueryGetPQLResultModifier() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("portal.smoke", "true");
        PQLEntityFactoryTest._validateGetPQLResult("NOT portal.smoke == true", Boolean.FALSE, properties);
        PQLEntityFactoryTest._validateGetPQLResult("NOT portal.smoke == false", Boolean.TRUE, properties);
    }

    @Test
    public void testPQLQueryGetPQLResultModifierError() throws Exception {
        PQLEntityFactoryTest._validateGetPQLResultError("portal.smoke == true NOT", "Invalid value: true NOT");
        PQLEntityFactoryTest._validateGetPQLResultError("portal.smoke == false NOT", "Invalid value: false NOT");
        PQLEntityFactoryTest._validateGetPQLResultError("portal.smoke == true NOT AND true", "Invalid value: true NOT");
        PQLEntityFactoryTest._validateGetPQLResultError("portal.smoke == false NOT AND true", "Invalid value: false NOT");
    }

    @Test
    public void testPQLQueryGetPQLResultParenthesis() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        Set<String> queries = new TreeSet<>();
        queries.add("(portal.smoke == true AND portal.smoke == false) OR true");
        queries.add("(portal.smoke == true OR portal.smoke == false) AND true");
        queries.add("(true AND false) OR true");
        queries.add("(true AND false) OR true");
        queries.add("(true AND true) OR false");
        queries.add("(true OR false) AND true");
        for (String query : queries) {
            PQLEntityFactoryTest._validateGetPQLResult(query, Boolean.TRUE, properties);
        }
        queries = new TreeSet<>();
        queries.add("(portal.smoke != true AND portal.smoke == true) OR false");
        queries.add("(portal.smoke != true OR portal.smoke == false) AND true");
        queries.add("(false AND true) OR false");
        queries.add("(false OR false) AND false");
        queries.add("(false OR false) AND true");
        queries.add("(false OR true) AND false");
        for (String query : queries) {
            PQLEntityFactoryTest._validateGetPQLResult(query, Boolean.FALSE, properties);
        }
    }

    @Test
    public void testPQLValueGetPQLResult() throws Exception {
        PQLEntityFactoryTest._validateGetPQLResult("false", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResult("'false'", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResult("\"false\"", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResult("true", Boolean.TRUE);
        PQLEntityFactoryTest._validateGetPQLResult("'true'", Boolean.TRUE);
        PQLEntityFactoryTest._validateGetPQLResult("\"true\"", Boolean.TRUE);
        PQLEntityFactoryTest._validateGetPQLResult("3.2", 3.2);
        PQLEntityFactoryTest._validateGetPQLResult("'3.2'", 3.2);
        PQLEntityFactoryTest._validateGetPQLResult("\"3.2\"", 3.2);
        PQLEntityFactoryTest._validateGetPQLResult("2016.0", 2016.0);
        PQLEntityFactoryTest._validateGetPQLResult("'2016.0'", 2016.0);
        PQLEntityFactoryTest._validateGetPQLResult("\"2016.0\"", 2016.0);
        PQLEntityFactoryTest._validateGetPQLResult("2016", 2016);
        PQLEntityFactoryTest._validateGetPQLResult("'2016'", 2016);
        PQLEntityFactoryTest._validateGetPQLResult("\"2016\"", 2016);
        PQLEntityFactoryTest._validateGetPQLResult("test", "test");
        PQLEntityFactoryTest._validateGetPQLResult("'test'", "test");
        PQLEntityFactoryTest._validateGetPQLResult("\"test\"", "test");
        PQLEntityFactoryTest._validateGetPQLResult("'test test'", "test test");
        PQLEntityFactoryTest._validateGetPQLResult("\"test test\"", "test test");
    }

    @Test
    public void testPQLValueGetPQLResultModifier() throws Exception {
        PQLEntityFactoryTest._validateGetPQLResult("NOT true", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResult("NOT false", Boolean.TRUE);
    }

    @Test
    public void testPQLValueGetPQLResultModifierError() throws Exception {
        PQLEntityFactoryTest._validateGetPQLResultError("NOT 3.2", "Modifier must be used with a boolean value: NOT");
        PQLEntityFactoryTest._validateGetPQLResultError("NOT 2016", "Modifier must be used with a boolean value: NOT");
        PQLEntityFactoryTest._validateGetPQLResultError("NOT test", "Modifier must be used with a boolean value: NOT");
        PQLEntityFactoryTest._validateGetPQLResultError("NOT 'test test'", "Modifier must be used with a boolean value: NOT");
    }

    @Test
    public void testPQLVariableGetPQLResult() throws Exception {
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("false", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'false'", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"false\"", Boolean.FALSE);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("true", Boolean.TRUE);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'true'", Boolean.TRUE);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"true\"", Boolean.TRUE);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("3.2", 3.2);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'3.2'", 3.2);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"3.2\"", 3.2);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("2016.0", 2016.0);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'2016.0'", 2016.0);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"2016.0\"", 2016.0);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("2016", 2016);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'2016'", 2016);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"2016\"", 2016);
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("test", "test");
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'test'", "test");
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"test\"", "test");
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("'test test'", "test test");
        PQLEntityFactoryTest._validateGetPQLResultFromVariable("\"test test\"", "test test");
    }
}

