/**
 * *****************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 * ****************************************************************************
 */
package com.eclipsesource.v8.debug;


import PropertyKind.Named;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.debug.DebugHandler.DebugEvent;
import com.eclipsesource.v8.debug.mirror.BooleanMirror;
import com.eclipsesource.v8.debug.mirror.Frame;
import com.eclipsesource.v8.debug.mirror.NumberMirror;
import com.eclipsesource.v8.debug.mirror.ObjectMirror;
import com.eclipsesource.v8.debug.mirror.PropertiesArray;
import com.eclipsesource.v8.debug.mirror.PropertyMirror;
import com.eclipsesource.v8.debug.mirror.StringMirror;
import com.eclipsesource.v8.debug.mirror.ValueMirror;
import org.junit.Assert;
import org.junit.Test;


public class MirrorTest {
    private static String script = "            // 1  \n" + (((((((((((((("function foo(a, b, c)  {         // 2  \n" + "  var integer = 7;               // 3  \n") + "  var boolean = false;           // 4  \n") + "  var obj = { \'num\' : 3,         // 5  \n") + "              \'bool\' : false,    // 6  \n") + "              \'string\' : \'bar\',  // 7  \n") + "              \'float\' : 3.14 };  // 8  \n") + "  var array = [1,2,3];           // 9  \n") + "  var string = \'foo\';            // 10 \n") + "  var nullValue = null;          // 11 \n") + "  var undef;                     // 12 \n") + "  var fun = function() {};       // 13 \n") + "  return obj;                    // 14 \n") + "}                                // 15 \n") + "foo(1,2,\'yes\').foo;              // 16 \n");

    private Object result = false;

    private V8 v8;

    private DebugHandler debugHandler;

    private BreakHandler breakHandler;

    @Test
    public void testEquals() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror objectValue1 = frame.getLocalValue(2);
                ValueMirror objectValue2 = frame.getLocalValue(2);
                result = objectValue1.equals(objectValue2);
                result = ((Boolean) (result)) & (objectValue2.equals(objectValue1));
                objectValue1.close();
                objectValue2.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testHashEquals() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror objectValue1 = frame.getLocalValue(2);
                ValueMirror objectValue2 = frame.getLocalValue(2);
                result = (objectValue1.hashCode()) == (objectValue2.hashCode());
                objectValue1.close();
                objectValue2.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testNotEquals() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror objectValue1 = frame.getLocalValue(2);
                ValueMirror objectValue2 = frame.getLocalValue(1);
                result = objectValue1.equals(objectValue2);
                objectValue1.close();
                objectValue2.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertFalse(((Boolean) (result)));
    }

    @Test
    public void testNotEqualsWrongType() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror objectValue1 = frame.getLocalValue(2);
                result = objectValue1.equals(new Object());
                objectValue1.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertFalse(((Boolean) (result)));
    }

    @Test
    public void testNotEqualsNull() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror objectValue1 = frame.getLocalValue(2);
                result = objectValue1.equals(null);
                objectValue1.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertFalse(((Boolean) (result)));
    }

    @Test
    public void testGetNumberValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror number = frame.getLocalValue(0);
                result = (number.isValue()) && (number.isNumber());
                result = ((Boolean) (result)) && (number.getValue().equals(7));
                number.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetBooleanValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror booleanValue = frame.getLocalValue(1);
                result = (booleanValue.isValue()) && (booleanValue.isBoolean());
                result = ((Boolean) (result)) && (booleanValue.getValue().equals(false));
                booleanValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetObjectValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror objectValue = frame.getLocalValue(2);
                result = (objectValue.isValue()) && (objectValue.isObject());
                objectValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetObjectProperties() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ObjectMirror objectValue = ((ObjectMirror) (frame.getLocalValue(2)));
                PropertiesArray properties = objectValue.getProperties(Named, 0);
                result = (properties.length()) == 4;
                PropertyMirror property = properties.getProperty(0);
                result = ((Boolean) (result)) && (property.isProperty());
                result = ((Boolean) (result)) && (property.getName().equals("num"));
                properties.close();
                property.close();
                objectValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetObjectProperties_Number() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ObjectMirror objectValue = ((ObjectMirror) (frame.getLocalValue(2)));
                PropertiesArray properties = objectValue.getProperties(Named, 0);
                PropertyMirror property = properties.getProperty(0);
                result = property.getName().equals("num");
                NumberMirror value = ((NumberMirror) (property.getValue()));
                result = ((Boolean) (result)) && (value.isNumber());
                result = ((Boolean) (result)) && (value.toString().equals("3"));
                value.close();
                properties.close();
                property.close();
                objectValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetObjectProperties_Boolean() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ObjectMirror objectValue = ((ObjectMirror) (frame.getLocalValue(2)));
                PropertiesArray properties = objectValue.getProperties(Named, 0);
                PropertyMirror property = properties.getProperty(1);
                result = property.getName().equals("bool");
                BooleanMirror value = ((BooleanMirror) (property.getValue()));
                result = ((Boolean) (result)) && (value.isBoolean());
                result = ((Boolean) (result)) && (value.toString().equals("false"));
                value.close();
                properties.close();
                property.close();
                objectValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetObjectProperties_String() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ObjectMirror objectValue = ((ObjectMirror) (frame.getLocalValue(2)));
                PropertiesArray properties = objectValue.getProperties(Named, 0);
                PropertyMirror property = properties.getProperty(2);
                result = property.getName().equals("string");
                StringMirror value = ((StringMirror) (property.getValue()));
                result = ((Boolean) (result)) && (value.isString());
                result = ((Boolean) (result)) && (value.toString().equals("bar"));
                value.close();
                properties.close();
                property.close();
                objectValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetObjectProperties_Float() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ObjectMirror objectValue = ((ObjectMirror) (frame.getLocalValue(2)));
                PropertiesArray properties = objectValue.getProperties(Named, 0);
                PropertyMirror property = properties.getProperty(3);
                result = property.getName().equals("float");
                NumberMirror value = ((NumberMirror) (property.getValue()));
                result = ((Boolean) (result)) && (value.isNumber());
                result = ((Boolean) (result)) && (value.toString().equals("3.14"));
                value.close();
                properties.close();
                property.close();
                objectValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetArrayValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror arrayValue = frame.getLocalValue(3);
                result = ((arrayValue.isValue()) && (arrayValue.isObject())) && (arrayValue.isArray());
                result = ((Boolean) (result)) && ((length()) == 3);
                arrayValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetStringValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror stringValue = frame.getLocalValue(4);
                result = (stringValue.isValue()) && (stringValue.isString());
                result = ((Boolean) (result)) && (stringValue.getValue().equals("foo"));
                stringValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetNullValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror nullValue = frame.getLocalValue(5);
                result = (nullValue.isValue()) && (nullValue.isNull());
                nullValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetUndefinedValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror undefinedValue = frame.getLocalValue(6);
                result = (undefinedValue.isValue()) && (undefinedValue.isUndefined());
                undefinedValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testGetFunctionValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ValueMirror functionValue = frame.getLocalValue(7);
                result = (functionValue.isValue()) && (functionValue.isFunction());
                functionValue.close();
                frame.close();
            }
        });
        v8.executeScript(MirrorTest.script, "script", 0);
        Assert.assertTrue(((Boolean) (result)));
    }

    @Test
    public void testChangeValue() {
        handleBreak(new BreakHandler() {
            @Override
            public void onBreak(final DebugEvent event, final ExecutionState state, final EventData eventData, final V8Object data) {
                Frame frame = state.getFrame(0);
                ObjectMirror mirror = ((ObjectMirror) (frame.getLocalValue(2)));
                V8Object object = ((V8Object) (mirror.getValue()));
                object.add("foo", 7);
                mirror.close();
                object.close();
                frame.close();
            }
        });
        int result = v8.executeIntegerScript(MirrorTest.script, "script", 0);
        Assert.assertEquals(7, result);
    }
}

