/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.subst;


import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.ScanException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class NodeToStringTransformerTest {
    ContextBase propertyContainer0 = new ContextBase();

    @Test
    public void literal() throws ScanException {
        String input = "abv";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals(input, nodeToStringTransformer.transform());
    }

    @Test
    public void literalWithNestedAccolades() throws ScanException {
        checkInputEqualsOutput("%logger{35}");
        checkInputEqualsOutput("%a{35} %b{35} c");
        checkInputEqualsOutput("%replace(%msg){\'\\d{14,16}\', \'XXXX\'}");
        checkInputEqualsOutput("TEST %d{HHmmssSSS} [%thread] %-5level %logger{36} - %msg%n");
    }

    @Test
    public void variable() throws ScanException {
        String input = "${k0}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("v0", nodeToStringTransformer.transform());
    }

    @Test
    public void literalVariableLiteral() throws ScanException {
        String input = "a${k0}c";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("av0c", nodeToStringTransformer.transform());
    }

    @Test
    public void nestedVariable() throws ScanException {
        String input = "a${k${zero}}b";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("av0b", nodeToStringTransformer.transform());
    }

    @Test
    public void LOGBACK729() throws ScanException {
        String input = "${${k0}.jdbc.url}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("http://..", nodeToStringTransformer.transform());
    }

    @Test
    public void LOGBACK744_withColon() throws ScanException {
        String input = "%d{HH:mm:ss.SSS} host:${host} %logger{36} - %msg%n";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        System.out.println(nodeToStringTransformer.transform());
        Assert.assertEquals("%d{HH:mm:ss.SSS} host:local %logger{36} - %msg%n", nodeToStringTransformer.transform());
    }

    @Test
    public void loneColonShouldReadLikeAnyOtherCharacter() throws ScanException {
        String input = "java:comp/env/jdbc/datasource";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals(input, nodeToStringTransformer.transform());
    }

    @Test
    public void withDefaultValue() throws ScanException {
        String input = "${k67:-b}c";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("bc", nodeToStringTransformer.transform());
    }

    @Test
    public void defaultValueNestedAsVar() throws ScanException {
        String input = "a${k67:-x${k0}}c";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("axv0c", nodeToStringTransformer.transform());
    }

    @Test
    public void LOGBACK_1101() throws ScanException {
        String input = "a: {y}";
        Node node = makeNode(input);
        NodeToStringTransformer nodeToStringTransformer = new NodeToStringTransformer(node, propertyContainer0);
        Assert.assertEquals("a: {y}", nodeToStringTransformer.transform());
    }
}

