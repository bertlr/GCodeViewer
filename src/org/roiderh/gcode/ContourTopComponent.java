/*
 * Copyright (C) 2014 by herbert
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
package org.roiderh.gcode;

//import org.roiderh.gcode.Bundle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.roiderh.gcodeviewer.point;
import math.geom2d.Point2D;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.roiderh.gcode//Contour//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "ContourTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = true)
@ActionID(category = "Window", id = "org.roiderh.gcode.ContourTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ContourAction",
        preferredID = "ContourTopComponent"
)
@Messages({
        "CTL_ContourAction=G-Code Contour",
        "CTL_ContourTopComponent=G-Code Contour",
        "HINT_ContourTopComponent=Show the Contour parsed from G-Code"
})
public final class ContourTopComponent extends TopComponent {

        /**
         * The points to display
         */
        public LinkedList<Point2D> disp = new LinkedList<>();

        public ContourTopComponent() {
                initComponents();
                setName(Bundle.CTL_ContourTopComponent());
                setToolTipText(Bundle.HINT_ContourTopComponent());

        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                label1 = new java.awt.Label();
                label2 = new java.awt.Label();

                label1.setText(org.openide.util.NbBundle.getMessage(ContourTopComponent.class, "ContourTopComponent.label1.text")); // NOI18N

                label2.setText(org.openide.util.NbBundle.getMessage(ContourTopComponent.class, "ContourTopComponent.label2.text")); // NOI18N

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 281, Short.MAX_VALUE))
                );
        }// </editor-fold>//GEN-END:initComponents

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private java.awt.Label label1;
        private java.awt.Label label2;
        // End of variables declaration//GEN-END:variables
        @Override
        public void componentOpened() {
                // TODO add custom code on component opening
        }

        @Override
        public void componentClosed() {
                // TODO add custom code on component closing
        }

        void writeProperties(java.util.Properties p) {
                // better to version settings since initial version as advocated at
                // http://wiki.apidesign.org/wiki/PropertyFiles
                p.setProperty("version", "1.0");
                // TODO store your settings
        }

        void readProperties(java.util.Properties p) {
                String version = p.getProperty("version");
                // TODO read your settings according to their version
        }

        public void drawGraph() {
                //System.out.println("hallo ContourComponent");

        }

        @Override
        public void paintComponent(Graphics g) {

                super.paintComponent(g);
                doDrawing(g);
        }

        private void doDrawing(Graphics g) {
                if(this.disp == null){
                        return;
                }
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
                if (x_size == 0) {
                        x_size = 1;

                }
                if (y_size == 0) {
                        y_size = 1;
                }

                double x_fact = (double) canvas_width * 0.9 / (double) x_size;
                double y_fact = (double) canvas_height * 0.9 / (double) y_size;
                double fact = Math.min(x_fact, y_fact);

                // zoom to the max. size to show the complete contour:
                for (int i = 0; i < this.disp.size(); i++) {
                        x_points[i] *= fact;
                        y_points[i] *= fact;

                }

                Graphics2D g2d = (Graphics2D) g;

                AffineTransform at = new AffineTransform();
                // move the toolpath to the center of the canvas:
                at.setToTranslation(((double) canvas_width) / 2.0 - (double) x_middle * fact, ((double) canvas_height / 2.0) - (double) y_middle * fact);
                g2d.transform(at);

                g2d.drawPolyline(x_points, y_points, this.disp.size());

        }

}
