/*
 * Copyright (C) 2016 by Herbert Roider <herbert.roider@utanet.at>
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.domain.PolyOrientedCurve2D;
import math.geom2d.line.Line2D;
import math.geom2d.line.LineSegment2D;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class Toolpath extends AnchorPane {

    private double pressedX, pressedY;

    private Text text = null;
    public double fact = 1.0;
    public double x_trans = 0.0;
    public double y_trans = 0.0;
    private double middle_x = 0.0;
    private double middle_y = 0.0;
    /**
     * The contour elements to display
     */
    public LinkedList<contourelement> c_elements = null;
    /**
     * holds only the Path elements
     */
    public LinkedList<javafx.scene.shape.Shape> shapes = new LinkedList<>();

    public Toolpath() {

        //setStyle("-fx-border-color: blue;");
    }

    /**
     * calculate the translation and the scale to move and zoom the contour to
     * the center of the screen
     *
     * @param elements
     */
    public void calcTransScale(LinkedList<contourelement> elements) {

        LinkedList<CirculinearElement2D> c_el = this.cleanup_contour(elements);
        PolyOrientedCurve2D closed = new PolyOrientedCurve2D(c_el);
        closed.add(new Line2D(closed.lastPoint(), closed.firstPoint()));
        math.geom2d.Box2D bb = closed.boundingBox();

        double max_x = bb.getMaxX();
        double min_x = bb.getMinX();
        double max_y = bb.getMaxY();
        double min_y = bb.getMinY();

        middle_x = (max_x + min_x) / 2.0;
        middle_y = (max_y + min_y) / 2.0;
        double width = bb.getWidth();
        double height = bb.getHeight();

        x_trans = (double) this.getWidth() / 2.0 - middle_x;
        y_trans = (double) this.getHeight() / 2.0 - middle_y;

        double x_fact = (double) this.getWidth() * 0.98 / width;
        double y_fact = (double) this.getHeight() * 0.98 / height;
        fact = Math.min(x_fact, y_fact);

    }

    /**
     * transform and cleanup the contour, remove empty and zero length elements
     *
     * @param contour
     * @return
     */
    private LinkedList<CirculinearElement2D> cleanup_contour(LinkedList<contourelement> contour) {
        LinkedList<CirculinearElement2D> elements = new LinkedList<>();
        for (contourelement current_ce : contour) {

            if (current_ce.curve == null) {
                continue;
            }
            if (current_ce.curve.length() == 0) {
                continue;
            }
            elements.add(current_ce.curve);

            if (current_ce.transition_curve != null) {
                elements.add(current_ce.transition_curve);
            }
        }
        return elements;

    }

    /**
     * draws the contour
     */
    private void draw() {
        for (javafx.scene.shape.Shape s : shapes) {
            this.getChildren().clear();
        }

        Scale scale = new Scale();
        Translate trans = new Translate();

        for (contourelement current_ce : c_elements) {

            if (current_ce.curve == null) {
                continue;
            }
            if (current_ce.curve.length() == 0) {
                continue;
            }

            /*
             Add the current element to the contour as multiple lines.
             */
            ToolpathElement path = new ToolpathElement();

            Shape l = null;
            if (current_ce.curve instanceof math.geom2d.conic.CircleArc2D) {
                CircleArc2D geo = (CircleArc2D) current_ce.curve;
                geo.supportingCircle().center().getX();

                MoveTo mt = new MoveTo();
                mt.setX(geo.asPolyline(1).firstPoint().getX());
                mt.setY(geo.asPolyline(1).firstPoint().getY());

                ArcTo at = new ArcTo();
                at.setLargeArcFlag(false);
                if (current_ce.ccw) {
                    at.setSweepFlag(true);
                } else {
                    at.setSweepFlag(false);
                }

                at.setRadiusX(geo.supportingCircle().radius());
                at.setRadiusY(geo.supportingCircle().radius());
                at.setX(geo.asPolyline(1).lastPoint().getX());
                at.setY(geo.asPolyline(1).lastPoint().getY());
                path.getElements().add(mt);
                path.getElements().add(at);

            } else {
                LineSegment2D geo = (LineSegment2D) current_ce.curve;
                MoveTo mt = new MoveTo();
                mt.setX(geo.firstPoint().getX());
                mt.setY(geo.firstPoint().getY());

                LineTo lt = new LineTo();
                lt.setX(geo.lastPoint().getX());
                lt.setY(geo.lastPoint().getY());
                path.getElements().add(mt);
                path.getElements().add(lt);

            }
            if (current_ce.transition_curve != null) {
                if (current_ce.transition_curve instanceof math.geom2d.conic.CircleArc2D) {
                    ArcTo at = new ArcTo();
                    at.setLargeArcFlag(false);
                    CircleArc2D geo = (CircleArc2D) current_ce.transition_curve;
                    if (geo.getAngleExtent() > Math.PI) {
                        at.setLargeArcFlag(true);
                    } else {
                        at.setLargeArcFlag(false);
                    }
                    if (geo.isDirect()) {
                        at.setSweepFlag(true);
                    } else {
                        at.setSweepFlag(false);
                    }

                    at.setRadiusX(geo.supportingCircle().radius());
                    at.setRadiusY(geo.supportingCircle().radius());
                    at.setX(geo.asPolyline(1).lastPoint().getX());
                    at.setY(geo.asPolyline(1).lastPoint().getY());
                    path.getElements().add(at);

                } else {
                    LineTo lt = new LineTo();
                    lt.setX(current_ce.transition_curve.lastPoint().getX());
                    lt.setY(current_ce.transition_curve.lastPoint().getY());
                    path.getElements().add(lt);

                }
            }

            path.setOnMouseEntered(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat df = (DecimalFormat) nf;
                    df.applyPattern("0.###");
                    contourelement current_ce = ((ToolpathElement) t.getSource()).element;

                    if (current_ce.shape == contourelement.Shape.ARC) {
                        CircleArc2D geo = (CircleArc2D) current_ce.curve;
                        double startAngle;
                        double endAngle;

                        if (current_ce.ccw == true) {
                            startAngle = 90.0 + geo.getStartAngle() * 180.0 / Math.PI;
                            endAngle = 90.0 + (geo.getStartAngle() + geo.getAngleExtent()) * 180.0 / Math.PI;
                        } else {
                            startAngle = -90.0 + geo.getStartAngle() * 180.0 / Math.PI;
                            endAngle = -90.0 + (geo.getStartAngle() + geo.getAngleExtent()) * 180.0 / Math.PI;
                        }

                        text.setText("Start:  " + "x " + df.format(current_ce.points.getFirst().y * 2.0) + ", z " + df.format(current_ce.points.getFirst().x)
                                + "\nEnd:    " + "x " + df.format(current_ce.points.getLast().y * 2.0) + ", z " + df.format(current_ce.points.getLast().x)
                                + "\nCenter: " + "x " + df.format(geo.supportingCircle().center().getY() * 2.0) + ", z " + df.format(geo.supportingCircle().center().getX())
                                + "\nRadius: " + df.format(current_ce.radius)
                                + "\nStart Angle: " + df.format(startAngle)
                                + "\nEnd Angle:   " + df.format(endAngle));
                    } else {
                        LineSegment2D geo = (LineSegment2D) current_ce.curve;
                        double angle = geo.direction().angle() * 180.0 / Math.PI;
                        text.setText("Start: " + "x " + df.format(current_ce.points.getFirst().y * 2.0) + ", z " + df.format(current_ce.points.getFirst().x)
                                + "\nEnd:   " + "x " + df.format(current_ce.points.getLast().y * 2.0) + ", z " + df.format(current_ce.points.getLast().x)
                                + "\nAngle: " + df.format(angle));
                    }

                    ((Shape) t.getSource()).setStroke(Color.RED);

                }
            });

            path.setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {

                    text.setText("");
                    ((Shape) t.getSource()).setStroke(Color.BLACK);

                }
            });
            path.element = current_ce;
            path.setStrokeWidth(1);
            path.setStroke(Color.BLACK);
            if (current_ce.feed == contourelement.Feed.RAPID) {
                path.setStyle("-fx-stroke-dash-array: 0.01 0.1 ; ");
            }

            path.getTransforms().add(trans);
            path.getTransforms().add(scale);

            shapes.add(path);
            getChildren().add(path);

        }

        setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                pressedX = event.getX();
                pressedY = event.getY();

            }
        });
        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                x_trans += (x - pressedX);
                y_trans += (y - pressedY);
                draw();
                pressedX = x;
                pressedY = y;
                //event.consume();
            }
        });

        setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double new_fact = fact;

                if (event.getDeltaY() < 0) {
                    new_fact *= 0.8;
                } else {
                    new_fact *= 1.2;
                }
                if (new_fact < 0.5) {
                    return;
                }
                if (new_fact > 1000.0) {
                    return;
                }
                fact = new_fact;

                draw();

                //event.consume();
            }
        });

        trans.setX(x_trans);
        trans.setY(y_trans);

        scale.setPivotX(middle_x);
        scale.setPivotY(middle_y);
        scale.setX(fact);
        scale.setY(-fact);

        for (javafx.scene.shape.Shape s : shapes) {
            s.setStrokeWidth(1.0 / (fact * 0.9));
        }

        text = new Text();
        double off = text.getBaselineOffset();
        text.setX(5);
        text.setY(off);
        //text.setCache(true);
        getChildren().add(0, text);

    }

    /**
     * draw the contour
     *
     * @param elements contour elements to draw
     */
    public void draw(LinkedList<contourelement> elements) {
        setMinSize(50, 50);
        this.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        c_elements = elements;
        this.calcTransScale(elements);

        this.draw();

    }

}
