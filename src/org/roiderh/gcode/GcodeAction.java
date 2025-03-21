/*
 * Copyright (C) 2016 by by Herbert Roider <herbert.roider@utanet.at>
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
import org.openide.windows.TopComponent;
import java.util.Set;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.roiderh.gcodeviewer.gcodereader;
import org.openide.awt.ActionReferences;
import org.roiderh.gcodeviewer.contourelement;

@ActionID(
        category = "Edit",
        id = "org.roiderh.gcode.GcodeAction"
)
@ActionRegistration(
        displayName = "#CTL_GcodeAction",
        iconBase = "org/roiderh/gcode/Drehwerkzeug_24x24.png"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 0),
    @ActionReference(path = "Editors/text/plain/Popup"),
    @ActionReference(path = "Editors/text/x-nc/Popup")
})

public final class GcodeAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        JTextComponent ed = org.netbeans.api.editor.EditorRegistry.lastFocusedComponent();
        if (ed == null) {
            JOptionPane.showMessageDialog(null, "Error: no open editor");
            return;
        }

        LinkedList<contourelement> contour;
        try {

            gcodereader gr = new gcodereader();
            int start_offset = ed.getSelectionStart();
            int end_offset = ed.getSelectionEnd();
            Document doc = ed.getDocument();
            Element root = doc.getDefaultRootElement();
            int start_index = root.getElementIndex(start_offset);
            int end_index = root.getElementIndex(end_offset);
            int lineIndex = 0;
            ArrayList<String> lines = new ArrayList<>();

            for (lineIndex = start_index; lineIndex <= end_index; lineIndex++) {
                Element contentEl = root.getElement(lineIndex);
                int start = contentEl.getStartOffset();
                int end = contentEl.getEndOffset();
                String line = doc.getText(start, end - start - 1);
                lines.add(line);

            }
            contour = gr.read(start_index, lines);
            gr.calc_contour(contour);
        } catch (Exception e1) {
            System.out.println("Error " + e1.toString());
            JOptionPane.showMessageDialog(null, "Error: " + e1.toString());
            return;

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

        panel.c_elements = contour;
        panel.drawGraph();
    }
}
