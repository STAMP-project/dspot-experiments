/**
 * *****************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 * ****************************************************************************
 */
package com.eclipsesource.v8;


import com.eclipsesource.v8.utils.V8ObjectUtils;
import com.eclipsesource.v8.utils.V8Runnable;
import com.eclipsesource.v8.utils.V8Thread;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class V8MultiThreadTest {
    private List<Object> mergeSortResults = new ArrayList<Object>();

    private static final String sortAlgorithm = "" + (((((((((((((((((((((((((((("function merge(left, right){\n" + "  var result  = [],\n") + "  il      = 0,\n") + "  ir      = 0;\n") + "  while (il < left.length && ir < right.length){\n") + "    if (left[il] < right[ir]){\n") + "      result.push(left[il++]);\n") + "    } else {\n") + "      result.push(right[ir++]);\n") + "    }\n") + "  }\n") + "  return result.concat(left.slice(il)).concat(right.slice(ir));\n") + "};\n") + "\n") + "function sort(data) {\n") + "  if ( data.length === 1 ) {\n") + "    return [data[0]];\n") + "  } else if (data.length === 2 ) {\n") + "    if ( data[1] < data[0] ) {\n") + "      return [data[1],data[0]];\n") + "    } else {\n") + "      return data;\n") + "    }\n") + "  }\n") + "  var mid = Math.floor(data.length / 2);\n") + "  var first = data.slice(0, mid);\n") + "  var second = data.slice(mid);\n") + "  return merge(_sort( first ), _sort( second ) );\n") + "}\n");

    public class Sort implements JavaCallback {
        List<Object> result = null;

        @Override
        public Object invoke(final V8Object receiver, final V8Array parameters) {
            final List<Object> data = V8ObjectUtils.toList(parameters);
            V8Thread t = new V8Thread(new V8Runnable() {
                @Override
                public void run(final V8 runtime) {
                    runtime.registerJavaMethod(new V8MultiThreadTest.Sort(), "_sort");
                    runtime.executeVoidScript(V8MultiThreadTest.sortAlgorithm);
                    V8Array parameters = V8ObjectUtils.toV8Array(runtime, data);
                    V8Array _result = runtime.executeArrayFunction("sort", parameters);
                    result = V8ObjectUtils.toList(_result);
                    _result.close();
                    parameters.close();
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return V8ObjectUtils.toV8Array(parameters.getRuntime(), result);
        }
    }

    V8 v8TempRuntime = null;

    @Test
    public void testLosesCurrentIsolate() {
        final V8 v8 = V8.createV8Runtime();
        v8.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(final V8Object receiver, final V8Array parameters) {
                v8TempRuntime = V8.createV8Runtime();
                v8TempRuntime.getLocker().release();
                throw new RuntimeException();
            }
        }, "foo");
        try {
            v8.executeFunction("foo", null);
        } catch (RuntimeException e) {
            // doNothing
        }
        v8.release(false);
        v8TempRuntime.getLocker().acquire();
        v8TempRuntime.close();
    }

    @Test(expected = Exception.class)
    public void testReleaseLockInCallback() {
        final V8 v8 = V8.createV8Runtime();
        try {
            v8.registerJavaMethod(new JavaCallback() {
                @Override
                public Object invoke(final V8Object receiver, final V8Array parameters) {
                    v8.getLocker().release();
                    v8.getLocker().acquire();
                    return null;
                }
            }, "foo");
            v8.executeFunction("foo", null);
        } finally {
            v8.close();
        }
    }
}

