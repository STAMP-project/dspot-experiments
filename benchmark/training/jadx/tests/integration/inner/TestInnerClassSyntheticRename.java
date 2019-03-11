package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Issue: https://github.com/skylot/jadx/issues/336
 */
public class TestInnerClassSyntheticRename extends SmaliTest {
    // private class MyAsync extends AsyncTask<Uri, Uri, List<Uri>> {
    // @Override
    // protected List<Uri> doInBackground(Uri... uris) {
    // Log.i("MyAsync", "doInBackground");
    // return null;
    // }
    // 
    // @Override
    // protected void onPostExecute(List<Uri> uris) {
    // Log.i("MyAsync", "onPostExecute");
    // }
    // }
    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNodeFromSmali("inner/TestInnerClassSyntheticRename", "com.github.skylot.testasync.MyAsync");
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("List<Uri> doInBackground(Uri... uriArr) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("void onPostExecute(List<Uri> list) {"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

