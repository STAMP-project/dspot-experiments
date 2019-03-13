/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.expression.spel;


import SpelMessage.CONSTRUCTOR_INVOCATION_PROBLEM;
import SpelMessage.INITIALIZER_LENGTH_INCORRECT;
import SpelMessage.METHOD_NOT_FOUND;
import SpelMessage.MISSING_ARRAY_DIMENSION;
import SpelMessage.MULTIDIM_ARRAY_INITIALIZER_NOT_SUPPORTED;
import SpelMessage.TYPE_CONVERSION_ERROR;
import org.junit.Test;


/**
 * Test construction of arrays.
 *
 * @author Andy Clement
 */
public class ArrayConstructorTests extends AbstractExpressionTests {
    @Test
    public void simpleArrayWithInitializer() {
        evaluateArrayBuildingExpression("new int[]{1,2,3}", "[1,2,3]");
        evaluateArrayBuildingExpression("new int[]{}", "[]");
        evaluate("new int[]{}.length", "0", Integer.class);
    }

    @Test
    public void conversion() {
        evaluate("new String[]{1,2,3}[0]", "1", String.class);
        evaluate("new int[]{'123'}[0]", 123, Integer.class);
    }

    @Test
    public void multidimensionalArrays() {
        evaluateAndCheckError("new int[][]{{1,2},{3,4}}", MULTIDIM_ARRAY_INITIALIZER_NOT_SUPPORTED);
        evaluateAndCheckError("new int[3][]", MISSING_ARRAY_DIMENSION);
        evaluateAndCheckError("new int[]", MISSING_ARRAY_DIMENSION);
        evaluateAndCheckError("new String[]", MISSING_ARRAY_DIMENSION);
        evaluateAndCheckError("new int[][1]", MISSING_ARRAY_DIMENSION);
    }

    @Test
    public void primitiveTypeArrayConstructors() {
        evaluateArrayBuildingExpression("new int[]{1,2,3,4}", "[1,2,3,4]");
        evaluateArrayBuildingExpression("new boolean[]{true,false,true}", "[true,false,true]");
        evaluateArrayBuildingExpression("new char[]{'a','b','c'}", "[a,b,c]");
        evaluateArrayBuildingExpression("new long[]{1,2,3,4,5}", "[1,2,3,4,5]");
        evaluateArrayBuildingExpression("new short[]{2,3,4,5,6}", "[2,3,4,5,6]");
        evaluateArrayBuildingExpression("new double[]{1d,2d,3d,4d}", "[1.0,2.0,3.0,4.0]");
        evaluateArrayBuildingExpression("new float[]{1f,2f,3f,4f}", "[1.0,2.0,3.0,4.0]");
        evaluateArrayBuildingExpression("new byte[]{1,2,3,4}", "[1,2,3,4]");
    }

    @Test
    public void primitiveTypeArrayConstructorsElements() {
        evaluate("new int[]{1,2,3,4}[0]", 1, Integer.class);
        evaluate("new boolean[]{true,false,true}[0]", true, Boolean.class);
        evaluate("new char[]{'a','b','c'}[0]", 'a', Character.class);
        evaluate("new long[]{1,2,3,4,5}[0]", 1L, Long.class);
        evaluate("new short[]{2,3,4,5,6}[0]", ((short) (2)), Short.class);
        evaluate("new double[]{1d,2d,3d,4d}[0]", ((double) (1)), Double.class);
        evaluate("new float[]{1f,2f,3f,4f}[0]", ((float) (1)), Float.class);
        evaluate("new byte[]{1,2,3,4}[0]", ((byte) (1)), Byte.class);
        evaluate("new String(new char[]{'h','e','l','l','o'})", "hello", String.class);
    }

    @Test
    public void errorCases() {
        evaluateAndCheckError("new char[7]{'a','c','d','e'}", INITIALIZER_LENGTH_INCORRECT);
        evaluateAndCheckError("new char[3]{'a','c','d','e'}", INITIALIZER_LENGTH_INCORRECT);
        evaluateAndCheckError("new char[2]{'hello','world'}", TYPE_CONVERSION_ERROR);
        evaluateAndCheckError("new String('a','c','d')", CONSTRUCTOR_INVOCATION_PROBLEM);
    }

    @Test
    public void typeArrayConstructors() {
        evaluate("new String[]{'a','b','c','d'}[1]", "b", String.class);
        evaluateAndCheckError("new String[]{'a','b','c','d'}.size()", METHOD_NOT_FOUND, 30, "size()", "java.lang.String[]");
        evaluate("new String[]{'a','b','c','d'}.length", 4, Integer.class);
    }

    @Test
    public void basicArray() {
        evaluate("new String[3]", "java.lang.String[3]{null,null,null}", String[].class);
    }

    @Test
    public void multiDimensionalArray() {
        evaluate("new String[2][2]", "[Ljava.lang.String;[2]{[2]{null,null},[2]{null,null}}", String[][].class);
        evaluate("new String[3][2][1]", "[[Ljava.lang.String;[3]{[2]{[1]{null},[1]{null}},[2]{[1]{null},[1]{null}},[2]{[1]{null},[1]{null}}}", String[][][].class);
    }

    @Test
    public void constructorInvocation03() {
        evaluateAndCheckError("new String[]", MISSING_ARRAY_DIMENSION);
    }
}

