/**
 * ================================================================================
 */
/**
 * Copyright (c) 2011, David Yu
 */
/**
 * All rights reserved.
 */
/**
 * --------------------------------------------------------------------------------
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are met:
 */
/**
 * 1. Redistributions of source code must retain the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer.
 */
/**
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer in the documentation
 */
/**
 * and/or other materials provided with the distribution.
 */
/**
 * 3. Neither the name of protostuff nor the names of its contributors may be used
 */
/**
 * to endorse or promote products derived from this software without
 */
/**
 * specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 */
/**
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 */
/**
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 */
/**
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 */
/**
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 */
/**
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 */
/**
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 */
/**
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 */
/**
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 */
/**
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 */
/**
 * POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * ================================================================================
 */
package io.protostuff.runtime;


import RuntimeEnv.COLLECTION_SCHEMA_ON_REPEATED_FIELDS;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Test cyclic ser/deser on fields where the type is dynamic.
 *
 * @author David Yu
 * @unknown Oct 20, 2011
 */
public class ObjectSchemaTest extends TestCase {
    public void testGraph() {
        System.err.println(COLLECTION_SCHEMA_ON_REPEATED_FIELDS);
        ObjectSchemaTest.Bean bean = ObjectSchemaTest.fill(new ObjectSchemaTest.Bean());
        ObjectSchemaTest.verify(bean);
        Schema<ObjectSchemaTest.Bean> schema = RuntimeSchema.getSchema(ObjectSchemaTest.Bean.class);
        // print(schema);
        byte[] bytes = GraphIOUtil.toByteArray(bean, schema, LinkedBuffer.allocate(256));
        ObjectSchemaTest.Bean deBean = new ObjectSchemaTest.Bean();
        GraphIOUtil.mergeFrom(bytes, deBean, schema);
        ObjectSchemaTest.verify(deBean);
    }

    enum Order {

        ASCENDING,
        DESCENDING;}

    // explicitly without generics
    @SuppressWarnings("rawtypes")
    public static class Bean {
        public String name;

        public List firstList;

        public List secondList;

        public Object firstItem;

        public Object secondItem;

        public Map firstMap;

        public Map secondMap;

        public ObjectSchemaTest.HasName firstHasName;

        public ObjectSchemaTest.HasName secondHashname;

        public ObjectSchemaTest.Named firstNamed;

        public ObjectSchemaTest.Named secondNamed;

        public Object firstObject;

        public Object secondObject;

        Map<String, ?> firstStringMap;

        Map<String, ?> secondStringMap;

        Map<ObjectSchemaTest.HasName, ?> firstHasNameMap;

        Map<ObjectSchemaTest.HasName, ?> secondHasNameMap;

        Map<ObjectSchemaTest.Named, ?> firstNamedMap;

        Map<ObjectSchemaTest.Named, ?> secondNamedMap;

        int[] firstIntArray;

        int[] secondIntArray;

        Map<Set<String>, ?> firstSetMap;

        Map<Set<String>, ?> secondSetMap;

        Map<List<ObjectSchemaTest.Order>, ?> firstListMap;

        Map<List<ObjectSchemaTest.Order>, ?> secondListMap;

        Map<EnumSet<ObjectSchemaTest.Order>, ?> firstEnumSetMap;

        Map<EnumSet<ObjectSchemaTest.Order>, ?> secondEnumSetMap;

        List<Map<String, ?>> firstMapList;

        List<Map<String, ?>> secondMapList;

        List<EnumMap<ObjectSchemaTest.Order, ObjectSchemaTest.Item>> firstEnumMapList;

        List<EnumMap<ObjectSchemaTest.Order, ObjectSchemaTest.Item>> secondEnumMapList;

        ObjectSchemaTest.Item[] firstItemArray;

        ObjectSchemaTest.Item[] secondItemArray;

        Object itemArray;

        ObjectSchemaTest.Item[][] itemArray2d;

        Object[] itemArrayWrapper;

        public Set firstSet;

        public Set secondSet;

        public IdentityHashMap identityMap;

        public IdentityHashMap anotherIdentityMap;
    }

    public interface HasName {
        String getName();
    }

    public abstract static class Named {
        public abstract String getName();
    }

    public static class Item extends ObjectSchemaTest.Named implements ObjectSchemaTest.HasName {
        public String name;

        public Item() {
        }

        public Item(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public String toString() {
            return "name:" + (name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            if ((getClass()) != (obj.getClass()))
                return false;

            ObjectSchemaTest.Item other = ((ObjectSchemaTest.Item) (obj));
            if ((name) == null) {
                if ((other.name) != null)
                    return false;

            } else
                if (!(name.equals(other.name)))
                    return false;


            return true;
        }
    }
}

