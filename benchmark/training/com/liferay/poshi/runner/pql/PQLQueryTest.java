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
public class PQLQueryTest extends TestCase {
    @Test
    public void testGetPQLResultComparativeOperator() throws Exception {
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
            PQLQueryTest._validateGetPQLResult(query, Boolean.TRUE, properties);
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
            PQLQueryTest._validateGetPQLResult(query, Boolean.FALSE, properties);
        }
    }

    @Test
    public void testGetPQLResultComparativeOperatorError() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        PQLQueryTest._validateGetPQLResultError("true ==", "Invalid query: true ==", properties);
        PQLQueryTest._validateGetPQLResultError("false ==", "Invalid query: false ==", properties);
        PQLQueryTest._validateGetPQLResultError("== true", "Invalid query: == true", properties);
        PQLQueryTest._validateGetPQLResultError("== false", "Invalid query: == false", properties);
    }

    @Test
    public void testGetPQLResultConditionalOperator() throws Exception {
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
            PQLQueryTest._validateGetPQLResult(query, Boolean.TRUE, properties);
        }
        queries = new TreeSet<>();
        queries.add("portal.smoke != true OR portal.smoke == false");
        queries.add("portal.smoke == true AND portal.smoke == false");
        queries.add("false AND false");
        queries.add("false AND true");
        queries.add("false OR false");
        queries.add("true AND false");
        for (String query : queries) {
            PQLQueryTest._validateGetPQLResult(query, Boolean.FALSE, properties);
        }
    }

    @Test
    public void testGetPQLResultConditionalOperatorError() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("component.names", "Blogs,Message Boards,WEM");
        properties.setProperty("portal.smoke", "true");
        properties.setProperty("priority", "5");
        PQLQueryTest._validateGetPQLResultError("AND true == true", "Invalid query: AND true == true", properties);
        PQLQueryTest._validateGetPQLResultError("true == true AND", "Invalid query: true == true AND", properties);
        PQLQueryTest._validateGetPQLResultError("OR true == true", "Invalid query: OR true == true", properties);
        PQLQueryTest._validateGetPQLResultError("true == true OR", "Invalid query: true == true OR", properties);
        PQLQueryTest._validateGetPQLResultError("true == true AND AND false == false", "Invalid value: AND false == false", properties);
        PQLQueryTest._validateGetPQLResultError("(true == true) AND", "Invalid query: (true == true) AND", properties);
        PQLQueryTest._validateGetPQLResultError("(true == true) OR", "Invalid query: (true == true) OR", properties);
    }

    @Test
    public void testGetPQLResultModifier() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("portal.smoke", "true");
        PQLQueryTest._validateGetPQLResult("NOT portal.smoke == true", Boolean.FALSE, properties);
        PQLQueryTest._validateGetPQLResult("NOT portal.smoke == false", Boolean.TRUE, properties);
    }

    @Test
    public void testGetPQLResultModifierError() throws Exception {
        PQLQueryTest._validateGetPQLResultError("portal.smoke == true NOT", "Invalid value: true NOT");
        PQLQueryTest._validateGetPQLResultError("portal.smoke == false NOT", "Invalid value: false NOT");
        PQLQueryTest._validateGetPQLResultError("portal.smoke == true NOT AND true", "Invalid value: true NOT");
        PQLQueryTest._validateGetPQLResultError("portal.smoke == false NOT AND true", "Invalid value: false NOT");
    }

    @Test
    public void testGetPQLResultParenthesis() throws Exception {
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
            PQLQueryTest._validateGetPQLResult(query, Boolean.TRUE, properties);
        }
        queries = new TreeSet<>();
        queries.add("(portal.smoke != true AND portal.smoke == true) OR false");
        queries.add("(portal.smoke != true OR portal.smoke == false) AND true");
        queries.add("(false AND true) OR false");
        queries.add("(false OR false) AND false");
        queries.add("(false OR false) AND true");
        queries.add("(false OR true) AND false");
        for (String query : queries) {
            PQLQueryTest._validateGetPQLResult(query, Boolean.FALSE, properties);
        }
    }
}

