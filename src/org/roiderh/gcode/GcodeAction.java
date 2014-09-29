/*
 * Copyright (C) 2014 by by Herbert Roider <herbert.roider@utanet.at>
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

import org.openide.windows.TopComponent;
import java.util.Set;

import org.openide.cookies.LineCookie;
import java.io.File;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;
import java.lang.ProcessBuilder.Redirect;
import java.lang.Process;
import java.util.Map;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import org.roiderh.gcodeviewer.gcodereader;
import org.roiderh.gcodeviewer.point;
import math.geom2d.Point2D;
import org.roiderh.gcode.ContourTopComponent;

@ActionID(
        category = "Edit",
        id = "org.roiderh.gcode.GcodeAction"
)
@ActionRegistration(
        displayName = "#CTL_GcodeAction",
        iconBase = "org/roiderh/gcode/Drehwerkzeug_24x24.png"
)
@ActionReference(path = "Toolbars/File", position = 0)
@Messages("CTL_GcodeAction=Show Contour from selected G-Code")
public final class GcodeAction implements ActionListener {

        private LineCookie context;
        private JTextComponent editor;
        // private StyledDocument document;
        private String selectedText;
        private String stringToBeInserted;

        @Override
        public void actionPerformed(ActionEvent e) {

                JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
                if (ed == null) {
                        JOptionPane.showMessageDialog(null, "Error: no open editor");
                        return;
                }
//                Document doc = ed.getDocument();
//                if (doc == null) {
//                        JOptionPane.showMessageDialog(null, "No open Document");
//                        return;
//                }
//                System.out.println(doc.getLength());

                this.selectedText = ed.getSelectedText();

                System.out.println("Selected Text:");
                System.out.println(this.selectedText);
                if (selectedText == null) {
                        JOptionPane.showMessageDialog(null, "no selected G-Code");
                        return;
                }               // convert String into InputStream
                InputStream is = new ByteArrayInputStream(this.selectedText.getBytes());

                // parse the String
                //FileInputStream is;
                LinkedList<Point2D> disp = null;
                try {

                        //is = new FileInputStream(new File("/home/herbert/NetBeansProjects/gcodeviewer/src/org/roiderh/gcodeviewer/gcode.txt"));
                        gcodereader gr = new gcodereader();
                        disp = gr.read(is);

                } catch (Exception e1) {
                        System.out.println("Error " + e1.toString());
                        JOptionPane.showMessageDialog(null, "Error: " + e1.toString());
                        return;

                }

                System.out.println("ready calculated Contur:");
                for (Point2D p : disp) {
                        System.out.println("x=" + p.x() + ", y=" + p.y());

                }

                // Update the Contour
                Set<TopComponent> windows = org.openide.windows.TopComponent.getRegistry().getOpened();
                ContourTopComponent panel = null;
                for (TopComponent tc : windows) {
                        System.out.println("class name found: " + tc.getClass().getSimpleName());
                        if (tc.getClass().getSimpleName().equals("ContourTopComponent")) {
                                System.out.println("class name found: " + tc.getClass().getSimpleName());
                                panel = (ContourTopComponent) tc;

                        }
                }
                if (panel == null) {
                        return;
                }

                panel.disp = disp;
                panel.repaint();

//                JRootPane panel = 
//                Surface s = new Surface();
//                s.disp = disp;
//                add(s);
//
//                setSize(500, 500);
//                setLocationRelativeTo(null);
//                
//                ProcessBuilder pb
//                        = new ProcessBuilder("paraview", "myArg1", "myArg2");
//                Map<String, String> env = pb.environment();
////                env.put("VAR1", "myValue");
////                env.remove("OTHERVAR");
////                env.put("VAR2", env.get("VAR1") + "suffix");
//                pb.directory(new File("/home/herbert"));
//                File log = new File("log");
//                pb.redirectErrorStream(true);
//                pb.redirectOutput(Redirect.appendTo(log));
//                try {
//                        Process p = pb.start();
//                        assert pb.redirectInput() == Redirect.PIPE;
//                        assert pb.redirectOutput().file() == log;
//                        assert p.getInputStream().read() == -1;
//                } catch (IOException exception) {
//
//                }
//                NotifyDescriptor d
//                        = new NotifyDescriptor.Message("Hello...", NotifyDescriptor.INFORMATION_MESSAGE);
//                DialogDisplayer.getDefault().notify(d);
        }
}
