package com.svgandroid;


import android.graphics.Path;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Created by Vlad Medvedev on 19.01.2016.
 * vladislav.medvedev@devfactory.com
 */
public class PathParserTest {
    private Path path;

    @Test
    public void testParseLineNext() throws Exception {
        PathParser.parse("l5,5+10,10", path);
        // line
        Mockito.verify(path).rLineTo(5.0F, 5.0F);
        Mockito.verify(path).rLineTo(10.0F, 10.0F);
    }

    @Test
    public void testParseCubicPlus() throws Exception {
        PathParser.parse("c3,3,3,3,3,3+10,10,10,10,10,10", path);
        // cubic bezier
        Mockito.verify(path).cubicTo(3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F);
        Mockito.verify(path).cubicTo(13.0F, 13.0F, 13.0F, 13.0F, 13.0F, 13.0F);
    }

    @Test
    public void testParseMoveNext() throws Exception {
        PathParser.parse("m1,2+10,23", path);
        // move
        Mockito.verify(path).rMoveTo(1.0F, 2.0F);
        Mockito.verify(path).rLineTo(10.0F, 23.0F);
    }

    @Test
    public void testParseMove() throws Exception {
        PathParser.parse("M10,10m10,10", path);
        // move
        Mockito.verify(path).moveTo(10.0F, 10.0F);
        Mockito.verify(path).rMoveTo(10.0F, 10.0F);
    }

    @Test
    public void testParseLine() throws Exception {
        PathParser.parse("ML10,10l0,10", path);
        // line
        Mockito.verify(path).lineTo(10.0F, 10.0F);
        Mockito.verify(path).rLineTo(0.0F, 10.0F);
    }

    @Test
    public void testParseHLine() throws Exception {
        PathParser.parse("H10h5", path);
        // horizontal line
        Mockito.verify(path).lineTo(10.0F, 0.0F);
        Mockito.verify(path).rLineTo(5.0F, 0.0F);
    }

    @Test
    public void testParseVLine() throws Exception {
        PathParser.parse("V10v5", path);
        // vertical line
        Mockito.verify(path).lineTo(0.0F, 10.0F);
        Mockito.verify(path).rLineTo(0.0F, 5.0F);
    }

    @Test
    public void testParseSquare() throws Exception {
        PathParser.parse("M10,10H90V90H10L10,10", path);
        // draw square
        Mockito.verify(path, Mockito.times(1)).moveTo(10.0F, 10.0F);
        Mockito.verify(path).lineTo(90.0F, 10.0F);
        Mockito.verify(path).lineTo(90.0F, 90.0F);
        Mockito.verify(path).lineTo(10.0F, 90.0F);
        Mockito.verify(path).lineTo(10.0F, 10.0F);
    }

    @Test
    public void testParseTriangle() throws Exception {
        PathParser.parse("M250,150L150,350L350,350Z", path);
        // draw triangle
        Mockito.verify(path, Mockito.times(2)).moveTo(250.0F, 150.0F);
        Mockito.verify(path).lineTo(150.0F, 350.0F);
        Mockito.verify(path).lineTo(350.0F, 350.0F);
        Mockito.verify(path).close();
    }

    @Test
    public void testParseCurve() throws Exception {
        PathParser.parse("C150,150,180,80,100,120c30,100,40,100,30,90", path);
        // draw curve
        Mockito.verify(path).cubicTo(150.0F, 150.0F, 180.0F, 80.0F, 100.0F, 120.0F);
        Mockito.verify(path).cubicTo(130.0F, 220.0F, 140.0F, 220.0F, 130.0F, 210.0F);
    }

    @Test
    public void testParseCurveS() throws Exception {
        PathParser.parse("S150,150,180,80s30,10,20,35", path);
        // draw curve
        Mockito.verify(path).cubicTo(0.0F, 0.0F, 150.0F, 150.0F, 180.0F, 80.0F);
        Mockito.verify(path).cubicTo(210.0F, 10.0F, 210.0F, 90.0F, 200.0F, 115.0F);
    }

    @Test
    public void testParseArc() throws Exception {
        // is not implemented yet. Just call it
        PathParser.parse("A5,5,20,20,30,10,10", path);
    }
}

