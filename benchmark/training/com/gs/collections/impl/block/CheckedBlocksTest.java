/**
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.block;


import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ListIterate;
import com.gs.collections.impl.utility.MapIterate;
import java.io.IOException;
import org.junit.Test;


public class CheckedBlocksTest {
    @Test
    public void checkedFunction2CheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedFunction2<String, String, String> block = new CheckedFunction2<String, String, String>() {
                @Override
                public String safeValue(String argument1, String argument2) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.value("1", "2");
        });
    }

    @Test
    public void checkedFunction2RuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedFunction2<String, String, String> block = new CheckedFunction2<String, String, String>() {
                @Override
                public String safeValue(String argument1, String argument2) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.value("1", "2");
        });
    }

    @Test
    public void checkedCodeBlockCheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedFunction0<String> function = new CheckedFunction0<String>() {
                @Override
                public String safeValue() throws IOException {
                    throw new IOException("fail");
                }
            };
            function.value();
        });
    }

    @Test
    public void checkedCodeBlockRuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedFunction0<String> function = new CheckedFunction0<String>() {
                @Override
                public String safeValue() {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            function.value();
        });
    }

    @Test
    public void checkedProcedureCheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedProcedure<String> block = new CheckedProcedure<String>() {
                @Override
                public void safeValue(String object) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.value("1");
        });
    }

    @Test
    public void checkedProcedureRuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedProcedure<String> block = new CheckedProcedure<String>() {
                @Override
                public void safeValue(String object) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.value("1");
        });
    }

    @Test
    public void checkedObjectIntProcedureCheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedObjectIntProcedure<String> block = new CheckedObjectIntProcedure<String>() {
                @Override
                public void safeValue(String object, int index) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.value("1", 1);
        });
    }

    @Test
    public void checkedObjectIntProcedureRuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedObjectIntProcedure<String> block = new CheckedObjectIntProcedure<String>() {
                @Override
                public void safeValue(String object, int index) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.value("1", 1);
        });
    }

    @Test
    public void checkedFunctionCheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedFunction<String, String> block = new CheckedFunction<String, String>() {
                @Override
                public String safeValueOf(String object) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.valueOf("1");
        });
    }

    @Test
    public void checkedFunctionRuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedFunction<String, String> block = new CheckedFunction<String, String>() {
                @Override
                public String safeValueOf(String object) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.valueOf("1");
        });
    }

    @Test
    public void checkedPredicateCheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedPredicate<String> block = new CheckedPredicate<String>() {
                @Override
                public boolean safeAccept(String object) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.accept("1");
        });
    }

    @Test
    public void checkedPredicateRuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedPredicate<String> block = new CheckedPredicate<String>() {
                @Override
                public boolean safeAccept(String object) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.accept("1");
        });
    }

    @Test
    public void checkedPredicate2CheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedPredicate2<String, String> block = new CheckedPredicate2<String, String>() {
                @Override
                public boolean safeAccept(String object, String param) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.accept("1", "2");
        });
    }

    @Test
    public void checkedPredicate2RuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedPredicate2<String, String> block = new CheckedPredicate2<String, String>() {
                @Override
                public boolean safeAccept(String object, String param) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.accept("1", "2");
        });
    }

    @Test
    public void checkedProcedure2CheckedException() {
        Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, () -> {
            CheckedProcedure2<String, String> block = new CheckedProcedure2<String, String>() {
                @Override
                public void safeValue(String object, String parameter) throws IOException {
                    throw new IOException("fail");
                }
            };
            block.value("1", "2");
        });
    }

    @Test
    public void checkedProcedure2RuntimeException() {
        Verify.assertThrows(CheckedBlocksTest.LocalException.class, () -> {
            CheckedProcedure2<String, String> block = new CheckedProcedure2<String, String>() {
                @Override
                public void safeValue(String object, String parameter) {
                    throw new com.gs.collections.impl.block.LocalException();
                }
            };
            block.value("1", "2");
        });
    }

    private static final class LocalException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private LocalException() {
            super("fail");
        }
    }

    @Test(expected = RuntimeException.class)
    public void codeBlockFailure() {
        Function0<Object> function = new com.gs.collections.impl.block.function.checked.CheckedFunction0<Object>() {
            @Override
            public Object safeValue() throws InterruptedException {
                throw new InterruptedException();
            }
        };
        MutableMap<String, Object> values = UnifiedMap.newMap();
        MapIterate.getIfAbsentPut(values, "test", function);
    }

    @Test(expected = RuntimeException.class)
    public void codeBlockRuntimeException() {
        Function0<Object> function = new com.gs.collections.impl.block.function.checked.CheckedFunction0<Object>() {
            @Override
            public Object safeValue() {
                throw new RuntimeException();
            }
        };
        MutableMap<String, Object> values = UnifiedMap.newMap();
        MapIterate.getIfAbsentPut(values, "test", function);
    }

    @Test
    public void codeBlockSuccess() {
        Function0<Object> function = new com.gs.collections.impl.block.function.checked.CheckedFunction0<Object>() {
            @Override
            public Object safeValue() {
                return null;
            }
        };
        MutableMap<String, Object> values = UnifiedMap.newMap();
        MapIterate.getIfAbsentPut(values, "test", function);
    }

    @Test(expected = RuntimeException.class)
    public void procedureFailure() {
        Procedure<Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedProcedure<Object>() {
            @Override
            public void safeValue(Object object) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        iList("test").forEach(block);
    }

    @Test(expected = RuntimeException.class)
    public void procedureRuntimeException() {
        Procedure<Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedProcedure<Object>() {
            @Override
            public void safeValue(Object object) {
                throw new RuntimeException();
            }
        };
        iList("test").forEach(block);
    }

    @Test
    public void procedureSuccess() {
        Procedure<Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedProcedure<Object>() {
            @Override
            public void safeValue(Object object) {
            }
        };
        iList("test").forEach(block);
    }

    @Test(expected = RuntimeException.class)
    public void objectIntProcedureFailure() {
        ObjectIntProcedure<Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedObjectIntProcedure<Object>() {
            @Override
            public void safeValue(Object object, int index) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        iList("test").forEachWithIndex(block);
    }

    @Test(expected = RuntimeException.class)
    public void objectIntProcedureRuntimeException() {
        ObjectIntProcedure<Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedObjectIntProcedure<Object>() {
            @Override
            public void safeValue(Object object, int index) {
                throw new RuntimeException();
            }
        };
        iList("test").forEachWithIndex(block);
    }

    @Test
    public void objectIntProcedureSuccess() {
        ObjectIntProcedure<Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedObjectIntProcedure<Object>() {
            @Override
            public void safeValue(Object object, int index) {
            }
        };
        iList("test").forEachWithIndex(block);
    }

    @Test(expected = RuntimeException.class)
    public void functionFailure() {
        Function<Object, Object> block = new com.gs.collections.impl.block.function.checked.CheckedFunction<Object, Object>() {
            @Override
            public Object safeValueOf(Object object) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        iList("test").collect(block);
    }

    @Test(expected = RuntimeException.class)
    public void functionRuntimeException() {
        Function<Object, Object> block = new com.gs.collections.impl.block.function.checked.CheckedFunction<Object, Object>() {
            @Override
            public Object safeValueOf(Object object) {
                throw new RuntimeException();
            }
        };
        iList("test").collect(block);
    }

    @Test
    public void functionSuccess() {
        Function<Object, Object> block = new com.gs.collections.impl.block.function.checked.CheckedFunction<Object, Object>() {
            @Override
            public Object safeValueOf(Object object) {
                return null;
            }
        };
        iList("test").collect(block);
    }

    @Test(expected = RuntimeException.class)
    public void predicateFailure() {
        Predicate<Object> block = new com.gs.collections.impl.block.predicate.checked.CheckedPredicate<Object>() {
            @Override
            public boolean safeAccept(Object object) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        iList("test").select(block);
    }

    @Test(expected = RuntimeException.class)
    public void predicateRuntimeException() {
        Predicate<Object> block = new com.gs.collections.impl.block.predicate.checked.CheckedPredicate<Object>() {
            @Override
            public boolean safeAccept(Object object) {
                throw new RuntimeException();
            }
        };
        iList("test").select(block);
    }

    @Test
    public void predicateSuccess() {
        Predicate<Object> block = new com.gs.collections.impl.block.predicate.checked.CheckedPredicate<Object>() {
            @Override
            public boolean safeAccept(Object object) {
                return true;
            }
        };
        iList("test").select(block);
    }

    @Test(expected = RuntimeException.class)
    public void procedure2Failure() {
        Procedure2<Object, Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedProcedure2<Object, Object>() {
            @Override
            public void safeValue(Object argument1, Object argument2) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        ListIterate.forEachInBoth(mList("test"), mList("test"), block);
    }

    @Test(expected = RuntimeException.class)
    public void procedure2RuntimeException() {
        Procedure2<Object, Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedProcedure2<Object, Object>() {
            @Override
            public void safeValue(Object argument1, Object argument2) {
                throw new RuntimeException();
            }
        };
        ListIterate.forEachInBoth(mList("test"), mList("test"), block);
    }

    @Test
    public void procedure2Success() {
        Procedure2<Object, Object> block = new com.gs.collections.impl.block.procedure.checked.CheckedProcedure2<Object, Object>() {
            @Override
            public void safeValue(Object argument1, Object argument2) {
                // nop
            }
        };
        ListIterate.forEachInBoth(mList("test"), mList("test"), block);
    }

    @Test(expected = RuntimeException.class)
    public void predicate2Failure() {
        Predicate2<Object, Object> block = new com.gs.collections.impl.block.predicate.checked.CheckedPredicate2<Object, Object>() {
            @Override
            public boolean safeAccept(Object object, Object param) throws InterruptedException {
                throw new InterruptedException();
            }
        };
        mList("test").selectWith(block, null);
    }

    @Test(expected = RuntimeException.class)
    public void predicate2RuntimeException() {
        Predicate2<Object, Object> block = new com.gs.collections.impl.block.predicate.checked.CheckedPredicate2<Object, Object>() {
            @Override
            public boolean safeAccept(Object object, Object param) {
                throw new RuntimeException();
            }
        };
        mList("test").selectWith(block, null);
    }

    @Test
    public void predicate2Success() {
        Predicate2<Object, Object> block = new com.gs.collections.impl.block.predicate.checked.CheckedPredicate2<Object, Object>() {
            @Override
            public boolean safeAccept(Object object, Object param) {
                return true;
            }
        };
        mList("test").selectWith(block, null);
    }
}

