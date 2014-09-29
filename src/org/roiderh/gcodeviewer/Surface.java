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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import javax.swing.JPanel;
import math.geom2d.Point2D;

/**
 *
 * @author Herbert Roider <herbert.roider@utanet.at>
 */
public class Surface extends JPanel {
        /**
         * The points to display
         */
        public LinkedList<Point2D> disp = null;

        private void doDrawing(Graphics g) {
                int canvas_width = this.getWidth();
                int canvas_height = this.getHeight();
                int[] x_points = new int[this.disp.size()];
                int[] y_points = new int[this.disp.size()];
                int x_max = 0;
                int x_min = 0;

                int y_max = 0;
                int y_min = 0;
                int x_middle = 0;
                int y_middle = 0;
                int x_size = 0;
                int y_size = 0;
                for (int i = 0; i < this.disp.size(); i++) {

                        x_points[i] = (int) (this.disp.get(i).x() * 1000.0);
                        y_points[i] = (int) (this.disp.get(i).y() * (-1000.0));
                        if (i == 0) {
                                x_max = x_points[i];
                                x_min = x_points[i];
                                y_max = y_points[i];
                                y_min = y_points[i];

                        }
                        if (x_points[i] > x_max) {
                                x_max = x_points[i];
                        }
                        if (x_points[i] < x_min) {
                                x_min = x_points[i];
                        }
                        if (y_points[i] > y_max) {
                                y_max = y_points[i];
                        }
                        if (y_points[i] < y_min) {
                                y_min = y_points[i];
                        }

                }
                x_middle = (x_max + x_min) / 2;
                y_middle = (y_max + y_min) / 2;

                x_size = Math.abs(x_max - x_min);
                y_size = Math.abs(y_max - y_min);
                if(x_size == 0){
                        x_size = 1;
                        
                }
                if(y_size == 0){
                        y_size = 1;
                }

                double x_fact = (double)canvas_width*0.9 / (double) x_size;
                double y_fact = (double)canvas_height*0.9 / (double) y_size;
                double fact = Math.min(x_fact, y_fact);

                // vergrössern:
                for (int i = 0; i < this.disp.size(); i++) {
                        x_points[i] *= fact;
                        y_points[i] *= fact;

                }

                Graphics2D g2d = (Graphics2D) g;

                AffineTransform at = new AffineTransform();
                //at.quadrantRotate(4);
                //at.setToRotation(Math.PI / 4.0);
                //g2d.transform(at);
                at.setToTranslation(((double)canvas_width)/2.0 - (double) x_middle * fact, ((double)canvas_height / 2.0) - (double) y_middle * fact);
                g2d.transform(at);

                g2d.drawPolyline(x_points, y_points, this.disp.size());

        }

        @Override
        public void paintComponent(Graphics g) {

                super.paintComponent(g);
                doDrawing(g);
        }
}
