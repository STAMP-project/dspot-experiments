package com.alibaba.json.bvt;


import JSONToken.COLON;
import JSONToken.COMMA;
import JSONToken.EOF;
import JSONToken.FALSE;
import JSONToken.FIELD_NAME;
import JSONToken.IDENTIFIER;
import JSONToken.LBRACE;
import JSONToken.LBRACKET;
import JSONToken.LITERAL_FLOAT;
import JSONToken.LITERAL_INT;
import JSONToken.LITERAL_ISO8601_DATE;
import JSONToken.LITERAL_STRING;
import JSONToken.LPAREN;
import JSONToken.NEW;
import JSONToken.NULL;
import JSONToken.RBRACE;
import JSONToken.RBRACKET;
import JSONToken.RPAREN;
import JSONToken.SET;
import JSONToken.TREE_SET;
import JSONToken.TRUE;
import com.alibaba.fastjson.parser.JSONToken;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONTokenTest extends TestCase {
    public void test_0() throws Exception {
        new JSONToken();
        Assert.assertEquals("int", JSONToken.name(LITERAL_INT));
        Assert.assertEquals("float", JSONToken.name(LITERAL_FLOAT));
        Assert.assertEquals("string", JSONToken.name(LITERAL_STRING));
        Assert.assertEquals("iso8601", JSONToken.name(LITERAL_ISO8601_DATE));
        Assert.assertEquals("true", JSONToken.name(TRUE));
        Assert.assertEquals("false", JSONToken.name(FALSE));
        Assert.assertEquals("null", JSONToken.name(NULL));
        Assert.assertEquals("new", JSONToken.name(NEW));
        Assert.assertEquals("(", JSONToken.name(LPAREN));
        Assert.assertEquals(")", JSONToken.name(RPAREN));
        Assert.assertEquals("{", JSONToken.name(LBRACE));
        Assert.assertEquals("}", JSONToken.name(RBRACE));
        Assert.assertEquals("[", JSONToken.name(LBRACKET));
        Assert.assertEquals("]", JSONToken.name(RBRACKET));
        Assert.assertEquals(",", JSONToken.name(COMMA));
        Assert.assertEquals(":", JSONToken.name(COLON));
        Assert.assertEquals("ident", JSONToken.name(IDENTIFIER));
        Assert.assertEquals("fieldName", JSONToken.name(FIELD_NAME));
        Assert.assertEquals("EOF", JSONToken.name(EOF));
        Assert.assertEquals("Unknown", JSONToken.name(Integer.MAX_VALUE));
        Assert.assertEquals("Set", JSONToken.name(SET));
        Assert.assertEquals("TreeSet", JSONToken.name(TREE_SET));
    }
}

