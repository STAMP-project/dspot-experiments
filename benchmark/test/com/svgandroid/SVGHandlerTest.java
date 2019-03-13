package com.svgandroid;


import Paint.Cap.BUTT;
import Paint.Cap.ROUND;
import Paint.Cap.SQUARE;
import Paint.Join;
import Paint.Join.MITER;
import Paint.Style.FILL;
import Paint.Style.STROKE;
import SVGParser.Properties;
import SVGParser.SVGHandler;
import Shader.TileMode.CLAMP;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import java.util.HashMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;


/**
 * Created by Vlad Medvedev on 22.01.2016.
 * vladislav.medvedev@devfactory.com
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RectF.class })
public class SVGHandlerTest extends SVGHandlerTestSupport {
    @Test
    public void testScale() throws SAXException {
        // given
        Mockito.when(canvas.getWidth()).thenReturn(2000);
        Mockito.when(canvas.getHeight()).thenReturn(500);
        // when
        parserHandler.startElement("", "svg", "svg", new AttributesMock(attr("width", "100px"), attr("height", "50px")));
        // then
        Mockito.verify(canvas).translate(500.0F, 0.0F);
        Mockito.verify(canvas).scale(10.0F, 10.0F);
    }

    @Test
    public void testParseGroup() throws SAXException {
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(), "g");
        startElement(parserHandler, attributes(attr("width", "100"), attr("height", "200"), attr("style", "stroke:#ff0000")), "rect");
        endElement(parserHandler, "rect");
        endElement(parserHandler, "g");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas).drawRect(0.0F, 0.0F, 100.0F, 200.0F, paint);
    }

    @Test
    public void testParseGroup_boundsMode() throws Exception {
        // given
        RectF createdBounds = Mockito.mock(RectF.class);
        PowerMockito.whenNew(RectF.class).withArguments(ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(100.0F), ArgumentMatchers.eq(100.0F)).thenReturn(createdBounds);
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("id", "bounds"), attr("x", "0"), attr("y", "0"), attr("width", "400"), attr("height", "400")), "g");
        startElement(parserHandler, attributes(attr("width", "100"), attr("height", "200"), attr("style", "stroke:#ff0000")), "rect");
        endElement(parserHandler, "rect");
        endElement(parserHandler, "g");
        endSVG(parserHandler);
        // then
        MatcherAssert.assertThat(parserHandler.bounds, Is.is(createdBounds));
        Mockito.verify(canvas, Mockito.never()).drawRect(0.0F, 0.0F, 100.0F, 200.0F, paint);
    }

    @Test
    public void testParseGroup_hidden() throws SAXException {
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(), "g");
        startElement(parserHandler, attributes(attr("width", "50"), attr("height", "50"), attr("style", "stroke:#ff0000")), "rect");
        endElement(parserHandler, "rect");
        // hidden group
        startElement(parserHandler, attributes(attr("display", "none")), "g");
        startElement(parserHandler, attributes(attr("width", "400"), attr("height", "400"), attr("style", "stroke:#ff0000")), "rect");
        endElement(parserHandler, "rect");
        endElement(parserHandler, "g");
        endElement(parserHandler, "g");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas, Mockito.never()).drawRect(0.0F, 0.0F, 400.0F, 400.0F, paint);
        Mockito.verify(canvas).drawRect(0.0F, 0.0F, 50.0F, 50.0F, paint);
    }

    @Test
    public void testParseLine() throws SAXException {
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("x1", "0.0"), attr("y1", "0.0"), attr("x2", "10.0"), attr("y2", "10.0"), attr("style", "stroke:#ff0000")), "line");
        endElement(parserHandler, "line");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas).drawLine(0.0F, 0.0F, 10.0F, 10.0F, paint);
    }

    @Test
    public void testParseCircle() throws SAXException {
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("cx", "10.0"), attr("cy", "10.0"), attr("r", "5"), attr("style", "fill:#ff0000;stroke:#ff0000")), "circle");
        endElement(parserHandler, "circle");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas, Mockito.times(2)).drawCircle(10.0F, 10.0F, 5.0F, paint);
    }

    @Test
    public void testParseEllipse() throws SAXException {
        // when
        startSVG(parserHandler);
        RectF rectF = Mockito.mock(RectF.class);
        parserHandler.rect = rectF;
        startElement(parserHandler, attributes(attr("cx", "10.0"), attr("cy", "10.0"), attr("rx", "5.0"), attr("ry", "5.0"), attr("style", "fill:#ff0000;stroke:#ff0000")), "ellipse");
        endElement(parserHandler, "ellipse");
        endSVG(parserHandler);
        // then
        Mockito.verify(rectF).set(5.0F, 5.0F, 15.0F, 15.0F);
        Mockito.verify(canvas, Mockito.times(2)).drawOval(rectF, paint);
    }

    @Test
    public void testParseRect() throws SAXException {
        // when
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("width", "100"), attr("height", "200"), attr("style", "fill:#ff0000;stroke:#ff0000")), "rect");
        endElement(parserHandler, "rect");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas, Mockito.times(2)).drawRect(0.0F, 0.0F, 100.0F, 200.0F, paint);
    }

    @Test
    public void testPushTransofrm() throws SAXException {
        // given
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        // Matrix matrix = mock(Matrix.class);
        // doReturn(matrix).when(parserHandler).createMatrix();
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("width", "100"), attr("height", "200"), attr("transform", "skewY(50)")), "rect");
        endElement(parserHandler, "rect");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas).drawRect(0.0F, 0.0F, 100.0F, 200.0F, paint);
        Mockito.verify(canvas).save();
        Mockito.verify(canvas).concat(matrix);
        Mockito.verify(canvas).restore();
    }

    @Test
    public void testParsePolygon() throws SAXException {
        // given
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        // Path path = mock(Path.class);
        // doReturn(path).when(parserHandler).createPath();
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("points", "220,10 300,210 170,250 123,234"), attr("style", "fill:#ff0000;stroke:#ff0000;stroke-width:1")), "polygon");
        endElement(parserHandler, "polygon");
        endSVG(parserHandler);
        // then
        Mockito.verify(path).moveTo(220.0F, 10.0F);
        Mockito.verify(path).lineTo(300.0F, 210.0F);
        Mockito.verify(path).lineTo(170.0F, 250.0F);
        Mockito.verify(path).lineTo(123.0F, 234.0F);
        Mockito.verify(path).close();
        Mockito.verify(canvas, Mockito.times(2)).drawPath(path, paint);
    }

    @Test
    public void testParsePath() throws SAXException {
        // given
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        // Path path = mock(Path.class);
        // doReturn(path).when(parserHandler).createPath();
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("d", "M150 0 L75 200 L225 200 Z"), attr("style", "fill:#ff0000;stroke:#ff0000;stroke-width:1")), "path");
        endElement(parserHandler, "path");
        endSVG(parserHandler);
        // then
        Mockito.verify(canvas, Mockito.times(2)).drawPath(path, paint);
    }

    @Test
    public void testParseLinearGradient() throws Exception {
        // given
        Mockito.when(picture.beginRecording(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(canvas);
        LinearGradient linearGradient = Mockito.mock(LinearGradient.class);
        PowerMockito.whenNew(LinearGradient.class).withArguments(ArgumentMatchers.eq(10.1F), ArgumentMatchers.eq(4.1F), ArgumentMatchers.eq(11.1F), ArgumentMatchers.eq(12.2F), ArgumentMatchers.eq(new int[]{ -2130771968 }), ArgumentMatchers.eq(new float[]{ 10.1F }), ArgumentMatchers.eq(CLAMP)).thenReturn(linearGradient);
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("id", "g1"), attr("x1", "10.1"), attr("y1", "4.1"), attr("x2", "11.1"), attr("y2", "12.2")), "linearGradient");
        startElement(parserHandler, attributes(attr("offset", "10.1"), attr("style", "stop-color:#ff0000;stop-opacity:0.5")), "stop");
        endElement(parserHandler, "stop");
        endElement(parserHandler, "linearGradient");
        startElement(parserHandler, attributes(attr("width", "100"), attr("height", "100"), attr("fill", "url(#g1)")), "rect");
        endElement(parserHandler, "rect");
        endSVG(parserHandler);
        // then
        Mockito.verify(paint).setShader(linearGradient);
    }

    @Test
    public void testParseLinearGradient_xlink() throws Exception {
        // given
        Mockito.when(picture.beginRecording(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(canvas);
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        LinearGradient gr1 = Mockito.mock(LinearGradient.class);
        PowerMockito.whenNew(LinearGradient.class).withArguments(ArgumentMatchers.eq(10.1F), ArgumentMatchers.eq(4.1F), ArgumentMatchers.eq(11.1F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(new int[0]), ArgumentMatchers.eq(new float[0]), ArgumentMatchers.eq(CLAMP)).thenReturn(gr1);
        LinearGradient gr2 = Mockito.mock(LinearGradient.class);
        PowerMockito.whenNew(LinearGradient.class).withArguments(ArgumentMatchers.eq(5.1F), ArgumentMatchers.eq(1.1F), ArgumentMatchers.eq(20.1F), ArgumentMatchers.eq(25.0F), ArgumentMatchers.eq(new int[0]), ArgumentMatchers.eq(new float[0]), ArgumentMatchers.eq(CLAMP)).thenReturn(gr2);
        // when
        startSVG(parserHandler);
        // parent
        startElement(parserHandler, attributes(attr("id", "gr1"), attr("x1", "10.1"), attr("y1", "4.1"), attr("x2", "11.1"), attr("gradientTransform", "matrix(0.2883 0 0 0.2865 153.3307 265.0264)")), "linearGradient");
        endElement(parserHandler, "linearGradient");
        // child
        startElement(parserHandler, attributes(attr("id", "gr2"), attr("x1", "5.1"), attr("y1", "1.1"), attr("x2", "20.1"), attr("y2", "25.0"), attr("href", "#gr1")), "linearGradient");
        endElement(parserHandler, "linearGradient");
        endSVG(parserHandler);
        // then
        Mockito.verify(gr2).setLocalMatrix(matrix);
    }

    @Test
    public void testParseRadialGradient() throws Exception {
        // given
        Mockito.when(picture.beginRecording(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(canvas);
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        RadialGradient gradient = Mockito.mock(RadialGradient.class);
        PowerMockito.whenNew(RadialGradient.class).withArguments(ArgumentMatchers.eq(10.1F), ArgumentMatchers.eq(4.1F), ArgumentMatchers.eq(5.0F), ArgumentMatchers.eq(new int[]{ -65536 }), ArgumentMatchers.eq(new float[]{ 10.1F }), ArgumentMatchers.eq(CLAMP)).thenReturn(gradient);
        // when
        startSVG(parserHandler);
        startElement(parserHandler, attributes(attr("id", "g1"), attr("cx", "10.1"), attr("cy", "4.1"), attr("r", "5.0")), "radialGradient");
        startElement(parserHandler, attributes(attr("offset", "10.1"), attr("style", "stop-color:ff0000")), "stop");
        endElement(parserHandler, "stop");
        endElement(parserHandler, "radialGradient");
        startElement(parserHandler, attributes(attr("width", "100"), attr("height", "100"), attr("fill", "url(#g1)")), "rect");
        endElement(parserHandler, "rect");
        endSVG(parserHandler);
        // then
        Mockito.verify(paint).setShader(gradient);
    }

    @Test
    public void testParseRadialGradient_xlink() throws Exception {
        // given
        Mockito.when(picture.beginRecording(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(canvas);
        SVGParser.SVGHandler parserHandler = Mockito.spy(this.parserHandler);
        RadialGradient gr1 = Mockito.mock(RadialGradient.class);
        PowerMockito.whenNew(RadialGradient.class).withArguments(ArgumentMatchers.eq(10.1F), ArgumentMatchers.eq(4.1F), ArgumentMatchers.eq(5.0F), ArgumentMatchers.eq(new int[0]), ArgumentMatchers.eq(new float[0]), ArgumentMatchers.eq(CLAMP)).thenReturn(gr1);
        RadialGradient gr2 = Mockito.mock(RadialGradient.class);
        PowerMockito.whenNew(RadialGradient.class).withArguments(ArgumentMatchers.eq(5.0F), ArgumentMatchers.eq(5.0F), ArgumentMatchers.eq(2.0F), ArgumentMatchers.eq(new int[0]), ArgumentMatchers.eq(new float[0]), ArgumentMatchers.eq(CLAMP)).thenReturn(gr2);
        Matrix subMatrix = Mockito.mock(Matrix.class);
        PowerMockito.whenNew(Matrix.class).withArguments(matrix).thenReturn(subMatrix);
        // when
        startSVG(parserHandler);
        // parent gradient
        startElement(parserHandler, attributes(attr("id", "gr1"), attr("cx", "10.1"), attr("cy", "4.1"), attr("r", "5.0"), attr("gradientTransform", "matrix(0.2883 0 0 0.2865 153.3307 265.0264)")), "radialGradient");
        endElement(parserHandler, "radialGradient");
        // child gradient
        startElement(parserHandler, attributes(attr("id", "gr2"), attr("cx", "5.0"), attr("cy", "5.0"), attr("r", "2.0"), attr("href", "#gr1")), "radialGradient");
        endElement(parserHandler, "radialGradient");
        endSVG(parserHandler);
        // then
        Mockito.verify(gr1, Mockito.times(1)).setLocalMatrix(matrix);
        Mockito.verify(gr2, Mockito.times(1)).setLocalMatrix(subMatrix);
        Mockito.verify(matrix).setValues(new float[]{ 0.2883F, 0.0F, 153.3307F, 0.0F, 0.2865F, 265.0264F, 0.0F, 0.0F, 1.0F });
    }

    @Test
    public void testDoFill_none() {
        boolean res = parserHandler.doFill(new SVGParser.Properties(attributes(attr("display", "none"))), new HashMap<String, Shader>());
        MatcherAssert.assertThat(res, Is.is(false));
    }

    @Test
    public void testDoFill_whiteMode() {
        parserHandler.setWhiteMode(true);
        boolean res = parserHandler.doFill(new SVGParser.Properties(attributes()), new HashMap<String, Shader>());
        Mockito.verify(paint).setStyle(FILL);
        Mockito.verify(paint).setColor(-1);
        MatcherAssert.assertThat(res, Is.is(true));
    }

    @Test
    public void testDoFill_byURL() {
        // given
        HashMap<String, Shader> gradients = new HashMap<>();
        Shader shader = Mockito.mock(Shader.class);
        gradients.put("gr1", shader);
        // when
        boolean res = parserHandler.doFill(new SVGParser.Properties(attributes(attr("fill", "url(#gr1)"))), gradients);
        // then
        MatcherAssert.assertThat(res, Is.is(true));
        Mockito.verify(paint).setShader(shader);
        Mockito.verify(paint).setStyle(FILL);
    }

    @Test
    public void testDoFill_byURL_shaderNotFound() {
        // given
        HashMap<String, Shader> gradients = new HashMap<>();
        Shader shader = Mockito.mock(Shader.class);
        gradients.put("gr1", shader);
        // when
        boolean res = parserHandler.doFill(new SVGParser.Properties(attributes(attr("fill", "#gr2)"))), gradients);
        // then
        MatcherAssert.assertThat(res, Is.is(false));
    }

    @Test
    public void testDoFill_byHexColor() {
        // given
        SVGParser.Properties properties = new SVGParser.Properties(attributes(attr("fill", "#ff0000")));
        // when
        boolean res = parserHandler.doFill(properties, new HashMap<String, Shader>());
        // then
        Mockito.verify(paint).setStyle(FILL);
        Integer hexColor = properties.getHex("fill");
        Mockito.verify(paint).setColor(((16777215 & hexColor) | -16777216));
        MatcherAssert.assertThat(res, Is.is(true));
    }

    @Test
    public void testDoFill_default() {
        boolean res = parserHandler.doFill(new SVGParser.Properties(attributes()), new HashMap<String, Shader>());
        Mockito.verify(paint).setStyle(FILL);
        Mockito.verify(paint).setColor(-16777216);
        MatcherAssert.assertThat(res, Is.is(true));
    }

    @Test
    public void testDoStroke_whiteModeTrue() {
        parserHandler.setWhiteMode(true);
        MatcherAssert.assertThat(parserHandler.doStroke(new SVGParser.Properties(attributes())), Is.is(false));
    }

    @Test
    public void testDoStroke_displayNone() {
        MatcherAssert.assertThat(parserHandler.doStroke(new SVGParser.Properties(attributes())), Is.is(false));
    }

    @Test
    public void testDoStroke() {
        MatcherAssert.assertThat(parserHandler.doStroke(new SVGParser.Properties(attributes(attr("stroke", "#ff0000")))), Is.is(true));
        Mockito.verify(paint).setStyle(STROKE);
    }

    @Test
    public void testDoStroke_strokeLinecapRound() {
        testDoStroke_strokeLinecap("round");
        Mockito.verify(paint).setStrokeCap(ROUND);
    }

    @Test
    public void testDoStroke_strokeLinecapSquare() {
        testDoStroke_strokeLinecap("square");
        Mockito.verify(paint).setStrokeCap(SQUARE);
    }

    @Test
    public void testDoStroke_strokeLinecapButt() {
        testDoStroke_strokeLinecap("butt");
        Mockito.verify(paint).setStrokeCap(BUTT);
    }

    @Test
    public void testDoStroke_linejoinMiter() {
        testDoStroke_linejoin("miter");
        Mockito.verify(paint).setStrokeJoin(MITER);
    }

    @Test
    public void testDoStroke_linejoinRound() {
        testDoStroke_linejoin("round");
        Mockito.verify(paint).setStrokeJoin(Paint.Join.ROUND);
    }

    @Test
    public void testDoStroke_linejoinBevel() {
        testDoStroke_linejoin("bevel");
        Mockito.verify(paint).setStrokeJoin(ArgumentMatchers.any(Join.class));
    }
}

