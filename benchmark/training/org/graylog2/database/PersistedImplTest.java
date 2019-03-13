/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.database;


import com.google.common.collect.Maps;
import java.util.Map;
import org.bson.types.ObjectId;
import org.graylog2.plugin.database.Persisted;
import org.graylog2.plugin.database.validators.Validator;
import org.junit.Assert;
import org.junit.Test;


public class PersistedImplTest {
    private static class PersistedImplSUT extends PersistedImpl {
        PersistedImplSUT(Map<String, Object> fields) {
            super(fields);
        }

        PersistedImplSUT(ObjectId id, Map<String, Object> fields) {
            super(id, fields);
        }

        @Override
        public Map<String, Validator> getValidations() {
            return null;
        }

        @Override
        public Map<String, Validator> getEmbeddedValidations(String key) {
            return null;
        }
    }

    @Test
    public void testConstructorWithFieldsOnly() throws Exception {
        Map<String, Object> fields = Maps.newHashMap();
        Persisted persisted = new PersistedImplTest.PersistedImplSUT(fields);
        Assert.assertNotNull(persisted);
        Assert.assertNotNull(persisted.getId());
        Assert.assertFalse(persisted.getId().isEmpty());
    }

    @Test
    public void testConstructorWithFieldsAndId() throws Exception {
        Map<String, Object> fields = Maps.newHashMap();
        ObjectId id = new ObjectId();
        Persisted persisted = new PersistedImplTest.PersistedImplSUT(id, fields);
        Assert.assertNotNull(persisted);
        Assert.assertNotNull(persisted.getId());
        Assert.assertFalse(persisted.getId().isEmpty());
        Assert.assertEquals(id.toString(), persisted.getId());
    }

    @Test
    public void testEqualityForSameRecord() throws Exception {
        Map<String, Object> fields = Maps.newHashMap();
        fields.put("foo", "bar");
        fields.put("bar", 42);
        ObjectId id = new ObjectId();
        Persisted persisted1 = new PersistedImplTest.PersistedImplSUT(id, fields);
        Persisted persisted2 = new PersistedImplTest.PersistedImplSUT(id, fields);
        Assert.assertEquals(persisted1, persisted2);
    }
}

