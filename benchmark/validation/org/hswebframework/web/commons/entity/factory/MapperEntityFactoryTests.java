package org.hswebframework.web.commons.entity.factory;


import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class MapperEntityFactoryTests {
    @Test
    public void testCreateEntity() {
        MapperEntityFactory entityFactory = new MapperEntityFactory();
        entityFactory.addMapping(TestEntity.class, entityFactory.initCache(NewTestEntity.class));
        TestEntity entity = entityFactory.newInstance(TestEntity.class);
        Assert.assertEquals(entity.getClass(), NewTestEntity.class);
        entity = entityFactory.copyProperties(new HashMap<String, Object>() {
            private static final long serialVersionUID = 6458422824954290386L;

            {
                put("name", "??");
                put("nickName", "??");
            }
        }, entity);
        Assert.assertEquals(getName(), "??");
        Assert.assertEquals(getNickName(), "??");
        entityFactory.addCopier(new MapperEntityFactoryTests.CustomPropertyCopier());
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "??");
        data.put("nickName", "??");
        entityFactory.copyProperties(data, entity);
        Assert.assertEquals(getName(), "??");
        Assert.assertEquals(getNickName(), "??");
    }

    class CustomPropertyCopier implements PropertyCopier<HashMap, NewTestEntity> {
        @Override
        public NewTestEntity copyProperties(HashMap source, NewTestEntity target) {
            setName(((String) (source.get("name"))));
            setNickName(((String) (source.get("nickName"))));
            return target;
        }
    }
}

