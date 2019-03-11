package org.ansj.ansj_lucene5_plug;


import DicLibrary.DEFAULT;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.lucene5.AnsjAnalyzer.TYPE;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;


public class IndexAndTest {
    @Test
    public void test() throws Exception {
        DicLibrary.put(DEFAULT, "../../library/default.dic");
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new org.ansj.lucene5.AnsjAnalyzer(TYPE.index_ansj));
        Directory directory = null;
        IndexWriter iwriter = null;
        IndexWriterConfig ic = new IndexWriterConfig(analyzer);
        String text = "[\u5de5\u7a0b\u540d\u79f0]\u8d63\u5dde\u5e02\u5357\u5eb7\u533a\u7b2c\u56db\u4e2d\u5b66\u5b66\u751f\u516c\u5bd3\u3001\u98df\u5802\u53ca\u9644\u5c5e\u5de5\u7a0b\n" + ("[\u5173\u952e\u4fe1\u606f]\u8d63\u5dde\u5e02\u5357\u5eb7\u533a\u7b2c\u56db\u4e2d\u5b66.; \u623f\u5c4b\u5efa\u7b51\u5de5 \u7a0b\u65bd\u5de5\u603b\u627f\u5305\u53c1\u7ea7\u4ee5\u4e0a(\u542b\u53c1\u7ea7)\u8d44\u8d28;\u65e0;\u672c\u5de5\u7a0b\u6388\u6743\u59d4\u6258\u4eba(\u6ce8\u518c\u5efa\u9020\u5e08)\u987b\u63d0\u4f9b\u52b3\u52a8\u5408\u540c\u548c\u6295\u6807\u516c\u53f8\u4e3a\u5176\u7f34\u4ea4\u7684\u793e\u4fdd\u8bc1\u660e\uff08\u793e\u4fdd\u8bc1\u660e\u65f6\u95f4\u4e3a2015 \u5e74 11 \u6708\u81f3 2016 \u5e74 1 \u6708\uff09\u539f\u4ef6\uff0c\u987b\u63d0\u4f9b\u52a0\u76d6\u5f53\u5730\u793e\u4fdd\u5c40\u4e1a\u52a1\u7ae0\u7684\u793e\u4fdd\u624b\u518c\u6216\u82b1\u540d\u518c(\u542b\u59d3\u540d\u3001\u793e\u4fdd\u67e5\u8be2\u53f7\u6216\u8eab\u4efd\u8bc1\u53f7\u3001\u7f34\u8d39\u57fa\u6570\u548c\u7f34\u8d39\u51ed\u8bc1)\u6216\u57fa\u672c\u517b\u8001\u4fdd\u9669\u4e2a\u4eba\u5e10\u6237\u5bf9\u8d26\u5355\uff1b\u5982\u679c\u5efa\u9020\u5e08\u662f\u6cd5\u4eba\u4ee3\u8868\u7684\uff0c\u5219\u63d0\u4f9b\uff1a\u8eab\u4efd\u8bc1\u3001\u6cd5\u4eba\u4ee3\u8868\u8d44\u683c\u8bc1\u3001\u5efa\u9020\u5e08\u6ce8\u518c\u8bc1\u4e66\u53ca\u5176\u76f8\u5e94\u7684 B \u7c7b\u5b89\u5168\u751f\u4ea7\u8003\u6838\u5408\u683c\u8bc1\uff1b               \u987b\u63d0\u4ea4\u516c\u53f8\u6216\u6295\u6807\u9879\u76ee\u6240 \u5728\u5730\u7684\u68c0\u5bdf\u673a\u5173\u51fa\u5177\u7684\u6295\u6807\u516c\u53f8\u548c\u6295\u6807\u516c \u53f8\u62df\u6d3e\u9879\u76ee\u8d1f\u8d23\u4eba\u7684\u300a\u5173\u4e8e\u884c\u8d3f\u72af\u7f6a\u6863\u6848 \u67e5\u8be2\u901a\u77e5\u4e66\u300b \u3002;\u5c0f\u80e1\u5f00\u94f6\u8bda\u3001\u4e2d\u707f\u4e24\u5bb6\uff0c\u8d63\u5dde\u5e02\u5357\u5eb7\u533a\u7b2c\u56db\u4e2d\u5b66\u5b66\u751f\u516c\u5bd3\u3001\u98df\u5802\u53ca\u9644\u5c5e\u5de5\u7a0b\uff0c\u672c\u9879\u76ee\u6295\u8d44 857.9 \u4e07\u5143\uff0c\u5f00\u6807\u65f6\u95f4\uff1a2016 \u5e74 03 \u6708 01 \u65e5 10:00\uff0c\u6295\u6807\u4fdd\u8bc1\u91d1\u7684\u91d1\u989d\uff1a15 \u4e07\u5143\uff0c\u4fdd\u8bc1\u91d1\u5230\u8d26\u622a\u6b62\u65f6\u95f4\u4e3a 2016 \u5e74 2\u6708 26 \u65e5 17:00 \u65f6\u3002\u4ecb\u7ecd\u4fe12000\u5143/\u5bb6\uff0c\u62a5\u540d\u8d39600\u5143/\u5bb6\uff0c\u4fdd\u8bc1\u91d1\u8001\u677f\u81ea\u5df1\u6253\u3002\u5f00\u6807\u8001\u677f\uff1a\u9ec4\u601d\u5a77 134-0707-4912    \u59d4\u6258\u4eba\uff1a\u80e1\u7ae5\u79d1\u3002;2000.0;\n" + "[????]?????;?????;?????-20160226-1;3607821602050117-1.JXZF;;");
        System.out.println(IndexAnalysis.parse(text));
        // ????????
        directory = new RAMDirectory();
        iwriter = new IndexWriter(directory, ic);
        addContent(iwriter, text);
        iwriter.commit();
        iwriter.close();
        System.out.println("??????");
        Analyzer queryAnalyzer = new org.ansj.lucene5.AnsjAnalyzer(TYPE.index_ansj);
        System.out.println("index ok to search!");
        for (Term t : IndexAnalysis.parse(text)) {
            System.out.println(t.getName());
            search(queryAnalyzer, directory, (("\"" + (t.getName())) + "\""));
        }
    }
}

