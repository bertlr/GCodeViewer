/*
 * Copyright (C) 2014 by Herbert Roider <herbert.roider@utanet.at>
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

import java.util.LinkedList;
import math.geom2d.Point2D;

/**
 *
 * @author Herbert Roider <herbert.roider@utanet.at>
 */
   
public class contourelement {
        // Übergang Fase oder Rundung
        public enum Transition {
                CHAMFER, ROUND
        }
        public enum Shape {
                LINE, ARC
        }
        public enum Feed {
                RAPID, CUTTING
        }
       // holds the start and end point without transition element:
        public LinkedList<point> points = new LinkedList<>();
        // calculated start point with transition element:
        public Point2D start;
        // calculated end point with transition element:
        public Point2D end;
        // Transition Element between the current and the next element:
        public Transition transistion_elem;
        public Shape shape;
        public double transition_elem_size;
        public Feed feed;
        // the real linenumber, not N-number or Lable
        public int linenumber;
        /**
         * If the shape was a arc (circle), the radius and the rotation direction:
         */
        public double radius; 
        public boolean ccw = true;
        

}
