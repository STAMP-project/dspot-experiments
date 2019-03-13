package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_uin57 extends TestCase {
    public void test_multiArray() throws Exception {
        String jsonString = "{\"block\":{\"boxList\":[{\"dx\":1,\"dy\":1},{\"dx\":0,\"dy\":0},{\"dx\":0,\"dy\":2},{\"dx\":2,\"dy\":0},{\"dx\":2,\"dy\":2}],\"centerBox\":{\"dx\":1,\"dy\":1},\"offsetX\":0,\"offsetY\":0},\"boxs\":[[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null],[null,null,null,null,null,null,null,null,null,null,null,null]]}";
        Bug_for_uin57.GameSnapShot gs = JSON.parseObject(jsonString, Bug_for_uin57.GameSnapShot.class);
        Bug_for_uin57.Block block = gs.getBlock();
        Assert.assertEquals(5, block.getBoxList().size());
        Assert.assertEquals(1, block.getBoxList().get(0).getX());
        Assert.assertEquals(1, block.getBoxList().get(0).getY());
        Assert.assertEquals(0, block.getBoxList().get(2).getX());
        Assert.assertEquals(2, block.getBoxList().get(2).getY());
        Bug_for_uin57.Box[][] boxs = gs.getBoxs();
        Assert.assertEquals(20, boxs.length);
        Assert.assertEquals(12, boxs[0].length);
    }

    public static class GameSnapShot implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 8755961532274905269L;

        protected Bug_for_uin57.Box[][] boxs = null;

        private Bug_for_uin57.Block block;

        public GameSnapShot() {
            super();
        }

        public GameSnapShot(Bug_for_uin57.Box[][] boxs, Bug_for_uin57.Block block) {
            super();
            this.boxs = boxs;
            this.block = block;
        }

        public Bug_for_uin57.Box[][] getBoxs() {
            return boxs;
        }

        public void setBoxs(Bug_for_uin57.Box[][] boxs) {
            this.boxs = boxs;
        }

        public Bug_for_uin57.Block getBlock() {
            return block;
        }

        public void setBlock(Bug_for_uin57.Block block) {
            this.block = block;
        }
    }

    public static class Box {
        @JSONField(name = "dx")
        private int x;

        @JSONField(name = "dy")
        private int y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    public static class Block {
        private List<Bug_for_uin57.Box> boxList = new ArrayList<Bug_for_uin57.Box>();

        private Bug_for_uin57.Box centerBox;

        private int offsetX;

        private int offsetY;

        public int getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }

        public Bug_for_uin57.Box getCenterBox() {
            return centerBox;
        }

        public void setCenterBox(Bug_for_uin57.Box centerBox) {
            this.centerBox = centerBox;
        }

        public List<Bug_for_uin57.Box> getBoxList() {
            return boxList;
        }

        public void setBoxList(List<Bug_for_uin57.Box> boxList) {
            this.boxList = boxList;
        }
    }
}

