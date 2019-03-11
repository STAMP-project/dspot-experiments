package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_SpitFire extends TestCase {
    public void test_for_spitFire() throws Exception {
        Bug_for_SpitFire.GenericDTO<Bug_for_SpitFire.MyDTO> object = new Bug_for_SpitFire.GenericDTO<Bug_for_SpitFire.MyDTO>();
        object.setFiled(new Bug_for_SpitFire.MyDTO());
        String text = JSON.toJSONString(object, WriteClassName);
        Bug_for_SpitFire.GenericDTO<Bug_for_SpitFire.MyDTO> object2 = ((Bug_for_SpitFire.GenericDTO<Bug_for_SpitFire.MyDTO>) (JSON.parseObject(text, Bug_for_SpitFire.GenericDTO.class)));
        Assert.assertEquals(object.getName(), object2.getName());
        Assert.assertEquals(object.getFiled().getId(), object2.getFiled().getId());
    }

    public static class GenericDTO<T extends Bug_for_SpitFire.AbstractDTO> extends Bug_for_SpitFire.AbstractDTO {
        private String name;

        private T filed;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public T getFiled() {
            return filed;
        }

        public void setFiled(T filed) {
            this.filed = filed;
        }
    }

    public abstract static class AbstractDTO {}

    public static class MyDTO extends Bug_for_SpitFire.AbstractDTO {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

