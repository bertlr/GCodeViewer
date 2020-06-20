/*
 * Copyright (C) 2020 by Herbert Roider <herbert@roider.at>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.roiderh.gcodeviewer;

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
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRead() throws Exception {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("G0 X0 Z2 R1=2.4;COMMENT");
        lines.add("LINEMARK: G3 X0 Z=(R1-2.4) CR=1\n");
        lines.add("M3 S1000");
        lines.add("N100 G1 RND=1 X=IC(4)");
        lines.add("G1 Z-2\n");

        gcodereader instance = new gcodereader();
        LinkedList<contourelement> result = instance.read(0, lines);
        instance.calc_contour(result);

        // calculate the transitions elements and the result vertexes and tangent points.
        // Also add points to the display contour, which is simple a chain of lines.
        for (int i = 0; i < result.size(); i++) {
            contourelement ce;
            ce = result.get(i);
            System.out.println("i: " + String.valueOf(i) + ", Zeile: " + ce.linenumber + ", line: " + ce.line);

            System.out.println(ce.start.toString());
            System.out.println(ce.end.toString());
            switch (i) {

                case 1:
                    test_point(new Point2D(2, 0), ce.start);
                    System.out.println(ce.curve.asPolyline(2).vertex(1).toString());
                    // point on the circle:
                    test_point(new Point2D(1, 1), ce.curve.asPolyline(2).vertex(1));

                    test_point(new Point2D(0, 0), ce.end);
                    break;

                case 2:
                    test_point(new Point2D(0, 0), ce.start);
                    test_point(new Point2D(0, 1), ce.end);
                    test_point(new Point2D(-1, 2), ce.transition_curve.lastPoint());

                    break;
                case 3:
                    test_point(new Point2D(-1, 2), ce.start);
                    test_point(new Point2D(-2, 2), ce.end);

                    break;
                case 4:
                    test_point(new Point2D(-2, 2), ce.start);
                    test_point(new Point2D(-2, 2), ce.end);

                    break;

            }

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
