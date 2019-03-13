package org.bukkit;


import org.bukkit.material.MaterialData;
import org.junit.Assert;
import org.junit.Test;


public class MaterialTest {
    @Test
    public void getByName() {
        for (Material material : Material.values()) {
            Assert.assertThat(Material.getMaterial(material.toString()), is(material));
        }
    }

    @Test
    public void getById() throws Throwable {
        for (Material material : Material.values()) {
            if ((material.getClass().getField(material.name()).getAnnotation(Deprecated.class)) != null) {
                continue;
            }
            Assert.assertThat(Material.getMaterial(material.getId()), is(material));
        }
    }

    @Test
    public void isBlock() {
        for (Material material : Material.values()) {
            if ((material.getId()) > 255)
                continue;

            Assert.assertTrue(String.format("[%d] %s", material.getId(), material.toString()), material.isBlock());
        }
    }

    @Test
    public void getByOutOfRangeId() {
        Assert.assertThat(Material.getMaterial(Integer.MAX_VALUE), is(nullValue()));
        Assert.assertThat(Material.getMaterial(Integer.MIN_VALUE), is(nullValue()));
    }

    @Test
    public void getByNameNull() {
        Assert.assertThat(Material.getMaterial(null), is(nullValue()));
    }

    @Test
    public void getData() {
        for (Material material : Material.values()) {
            Class<? extends MaterialData> clazz = material.getData();
            Assert.assertThat(material.getNewData(((byte) (0))), is(instanceOf(clazz)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void matchMaterialByNull() {
        Material.matchMaterial(null);
    }

    @Test
    public void matchMaterialById() throws Throwable {
        for (Material material : Material.values()) {
            if ((material.getClass().getField(material.name()).getAnnotation(Deprecated.class)) != null) {
                continue;
            }
            Assert.assertThat(Material.matchMaterial(String.valueOf(material.getId())), is(material));
        }
    }

    @Test
    public void matchMaterialByName() {
        for (Material material : Material.values()) {
            Assert.assertThat(Material.matchMaterial(material.toString()), is(material));
        }
    }

    @Test
    public void matchMaterialByLowerCaseAndSpaces() {
        for (Material material : Material.values()) {
            String name = material.toString().replaceAll("_", " ").toLowerCase();
            Assert.assertThat(Material.matchMaterial(name), is(material));
        }
    }
}

