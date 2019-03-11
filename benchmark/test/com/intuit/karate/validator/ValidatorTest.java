package com.intuit.karate.validator;


import ScriptValue.NULL;
import com.intuit.karate.ScriptValue;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;

import static IgnoreValidator.INSTANCE;


/**
 *
 *
 * @author pthomas3
 */
public class ValidatorTest {
    private final Validator IGNORE = INSTANCE;

    private final Validator NOT_NULL = NotNullValidator.INSTANCE;

    private final Validator NULL = NullValidator.INSTANCE;

    private final Validator STRING = StringValidator.INSTANCE;

    private final Validator NUMBER = NumberValidator.INSTANCE;

    private final Validator BOOLEAN = BooleanValidator.INSTANCE;

    private final Validator ARRAY = ArrayValidator.INSTANCE;

    private final Validator OBJECT = ObjectValidator.INSTANCE;

    @Test
    public void testSimpleValidators() {
        ScriptValue sv = new ScriptValue(null);
        Assert.assertTrue(IGNORE.validate(sv).isPass());
        Assert.assertTrue(NULL.validate(sv).isPass());
        Assert.assertFalse(NOT_NULL.validate(sv).isPass());
        Assert.assertFalse(NUMBER.validate(sv).isPass());
        Assert.assertFalse(BOOLEAN.validate(sv).isPass());
        Assert.assertFalse(STRING.validate(sv).isPass());
        Assert.assertFalse(ARRAY.validate(sv).isPass());
        Assert.assertFalse(OBJECT.validate(sv).isPass());
        sv = new ScriptValue(1);
        Assert.assertTrue(IGNORE.validate(sv).isPass());
        Assert.assertFalse(NULL.validate(sv).isPass());
        Assert.assertTrue(NOT_NULL.validate(sv).isPass());
        Assert.assertTrue(NUMBER.validate(sv).isPass());
        Assert.assertFalse(BOOLEAN.validate(sv).isPass());
        Assert.assertFalse(STRING.validate(sv).isPass());
        Assert.assertFalse(ARRAY.validate(sv).isPass());
        Assert.assertFalse(OBJECT.validate(sv).isPass());
        sv = new ScriptValue(true);
        Assert.assertTrue(IGNORE.validate(sv).isPass());
        Assert.assertFalse(NULL.validate(sv).isPass());
        Assert.assertTrue(NOT_NULL.validate(sv).isPass());
        Assert.assertFalse(NUMBER.validate(sv).isPass());
        Assert.assertTrue(BOOLEAN.validate(sv).isPass());
        Assert.assertFalse(STRING.validate(sv).isPass());
        Assert.assertFalse(ARRAY.validate(sv).isPass());
        Assert.assertFalse(OBJECT.validate(sv).isPass());
        sv = new ScriptValue("foo");
        Assert.assertTrue(IGNORE.validate(sv).isPass());
        Assert.assertFalse(NULL.validate(sv).isPass());
        Assert.assertTrue(NOT_NULL.validate(sv).isPass());
        Assert.assertFalse(NUMBER.validate(sv).isPass());
        Assert.assertFalse(BOOLEAN.validate(sv).isPass());
        Assert.assertTrue(STRING.validate(sv).isPass());
        Assert.assertFalse(ARRAY.validate(sv).isPass());
        Assert.assertFalse(OBJECT.validate(sv).isPass());
        sv = new ScriptValue(JsonPath.parse("[1, 2]"));
        Assert.assertTrue(IGNORE.validate(sv).isPass());
        Assert.assertFalse(NULL.validate(sv).isPass());
        Assert.assertTrue(NOT_NULL.validate(sv).isPass());
        Assert.assertFalse(NUMBER.validate(sv).isPass());
        Assert.assertFalse(BOOLEAN.validate(sv).isPass());
        Assert.assertFalse(STRING.validate(sv).isPass());
        Assert.assertTrue(ARRAY.validate(sv).isPass());
        Assert.assertFalse(OBJECT.validate(sv).isPass());
        sv = new ScriptValue(JsonPath.parse("{ foo: 'bar' }"));
        Assert.assertTrue(IGNORE.validate(sv).isPass());
        Assert.assertFalse(NULL.validate(sv).isPass());
        Assert.assertTrue(NOT_NULL.validate(sv).isPass());
        Assert.assertFalse(NUMBER.validate(sv).isPass());
        Assert.assertFalse(BOOLEAN.validate(sv).isPass());
        Assert.assertFalse(STRING.validate(sv).isPass());
        Assert.assertFalse(ARRAY.validate(sv).isPass());
        Assert.assertTrue(OBJECT.validate(sv).isPass());
    }

    @Test
    public void testRegexValidator() {
        Validator v = new RegexValidator("a");
        Assert.assertFalse(v.validate(ScriptValue.NULL).isPass());
        Assert.assertFalse(v.validate(new ScriptValue("b")).isPass());
        Assert.assertFalse(v.validate(new ScriptValue(1)).isPass());
        Assert.assertTrue(v.validate(new ScriptValue("a")).isPass());
        v = new RegexValidator("[\\d]{5}");
        Assert.assertFalse(v.validate(ScriptValue.NULL).isPass());
        Assert.assertFalse(v.validate(new ScriptValue(1)).isPass());
        Assert.assertFalse(v.validate(new ScriptValue("b")).isPass());
        Assert.assertFalse(v.validate(new ScriptValue("1111")).isPass());
        Assert.assertTrue(v.validate(new ScriptValue("11111")).isPass());
    }

    @Test
    public void testUuidValidator() {
        Validator v = new UuidValidator();
        Assert.assertTrue(v.validate(new ScriptValue("a9f7a56b-8d5c-455c-9d13-808461d17b91")).isPass());
        Assert.assertFalse(v.validate(new ScriptValue("a9f7a56b-8d5c-455c-9d13")).isPass());
        Assert.assertFalse(v.validate(new ScriptValue("a9f7a56b8d5c455c9d13808461d17b91")).isPass());
    }
}

