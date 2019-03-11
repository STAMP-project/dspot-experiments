package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitch2 extends IntegrationTest {
    public static class TestCls {
        boolean isLongtouchable;

        boolean isMultiTouchZoom;

        boolean isCanZoomIn;

        boolean isCanZoomOut;

        boolean isScrolling;

        float multiTouchZoomOldDist;

        void test(int action) {
            switch (action & 255) {
                case 0 :
                    this.isLongtouchable = true;
                    break;
                case 1 :
                case 6 :
                    if (this.isMultiTouchZoom) {
                        this.isMultiTouchZoom = false;
                    }
                    break;
                case 2 :
                    if (this.isMultiTouchZoom) {
                        float dist = multiTouchZoomOldDist;
                        if ((Math.abs((dist - (this.multiTouchZoomOldDist)))) > 10.0F) {
                            float scale = dist / (this.multiTouchZoomOldDist);
                            if (((scale > 1.0F) && (this.isCanZoomIn)) || ((scale < 1.0F) && (this.isCanZoomOut))) {
                                this.multiTouchZoomOldDist = dist;
                            }
                        }
                        return;
                    }
                    break;
                case 5 :
                    this.multiTouchZoomOldDist = action;
                    if ((this.multiTouchZoomOldDist) > 10.0F) {
                        this.isMultiTouchZoom = true;
                        this.isLongtouchable = false;
                        return;
                    }
                    break;
            }
            if ((this.isScrolling) && (action == 1)) {
                this.isScrolling = false;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitch2.TestCls.class);
        String code = cls.getCode().toString();
        // assertThat(code, countString(4, "break;"));
        // assertThat(code, countString(2, "return;"));
        // TODO: remove redundant break and returns
        Assert.assertThat(code, JadxMatchers.countString(5, "break;"));
        Assert.assertThat(code, JadxMatchers.countString(4, "return;"));
    }
}

