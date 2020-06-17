/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.roiderh.gcodeviewer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import math.geom2d.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author herbert
 */
public class gcodereaderTest {

    public gcodereaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class gcodereader.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        String gcode = "G0 X0 Z2\nG3 X0 Z0 CR=1\nG1 X4 RND=1\nG1 Z-2\n";
        ArrayList<String> lines = new ArrayList<>();
        lines.add("G0 X0 Z2");
        lines.add("G3 X0 Z0 CR=1");
        lines.add("G1 X4 RND=1");
        lines.add("G1 Z-2");
        InputStream is = new ByteArrayInputStream(gcode.getBytes(StandardCharsets.UTF_8));
        gcodereader instance = new gcodereader();
        LinkedList<contourelement> expResult = null;
        LinkedList<contourelement> result = instance.read(0, lines);
                //contourelement current_ce = null;

        // calculate the transitions elements and the result vertexes and tangent points.
        // Also add points to the display contour, which is simple a chain of lines.
        for (int i = 0; i < result.size(); i++) {
            System.out.println("Zeile " + String.valueOf(i));
            System.out.println(result.get(i).start.toString());
            System.out.println(result.get(i).end.toString());
            switch (i) {

                case 1:
                    System.out.println(result.get(i).curve.asPolyline(2).vertex(1).toString());
                    // point on the circle:
                    test_point(new Point2D(1, 1), result.get(i).curve.asPolyline(2).vertex(1));
                    break;



                case 2:
                    test_point(new Point2D(0, 0), result.get(i).start);
                    test_point(new Point2D(0, 1), result.get(i).end);

                    break;
                case 3:
                    test_point(new Point2D(-1, 2), result.get(i).start);
                    test_point(new Point2D(-2, 2), result.get(i).end);

                    break;
                case 4:
                    test_point(new Point2D(-2, 2), result.get(i).start);
                    test_point(new Point2D(-2, 2), result.get(i).end);

                    break;


            }
//            Point2D start = result.get(i).start.getX();
//            Point2D end;
//            start = ce.start
//            

        }

        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

//    /**
//     * Test of create_display_points method, of class gcodereader.
//     */
//    @Test
//    public void testCreate_display_points() {
//        System.out.println("create_display_points");
////        LinkedList<contourelement> contour = null;
////        gcodereader instance = new gcodereader();
////        LinkedList<Point2D> expResult = null;
////        LinkedList<Point2D> result = instance.create_display_points(contour);
////        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    void test_point(Point2D ref, Point2D p) {
        assertEquals(ref.getX(), p.getX(), 1.0e-5);
        assertEquals(ref.getY(), p.getY(), 1.0e-5);

    }

}
