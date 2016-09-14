package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;


public class ConnectionAcceptorsTests extends FlowPanel {

    private Layer layer;
    private IControlHandleList m_ctrls;
    private WiresShape startEventShape;
    private Circle startEventCircle;

    public ConnectionAcceptorsTests( Layer layer) {
        this.layer = layer;
    }

    public void test() {

        WiresManager wires_manager = WiresManager.get(layer);

        final double startX = 300;
        final double startY = 300;
        final double radius = 50;
        final double w = 100;
        final double h = 100;

        // Blue start event.
        MultiPath startEventMultiPath = new MultiPath().rect(0, 0, w, h).setStrokeColor("#000000");
        WiresShape startEventShape = wires_manager.createShape(startEventMultiPath);
        Circle startEventCircle = new Circle(radius).setFillColor("#0000CC").setDraggable(false);
        startEventShape.setX(startX).setY(startY).getContainer().setUserData("event");
        startEventShape.addChild(startEventCircle, WiresLayoutContainer.Layout.TOP);
        wires_manager.createMagnets( startEventShape );
        startEventShape.setDraggable( true ).setResizable( true );

        // Green task node.
        WiresShape taskNodeShape = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setFillColor("#00CC00"));
        taskNodeShape.setDraggable( true ).setResizable( true );
        taskNodeShape.setX(startX + 200).setY(startY).getContainer().setUserData("task");
        wires_manager.createMagnets(taskNodeShape);

        // Yellow task node.
        WiresShape task2NodeShape = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setFillColor("#FFEB52"));
        task2NodeShape.setDraggable( true ).setResizable( true );
        task2NodeShape.setX(startX + 200).setY(startY + 300).getContainer().setUserData("task");
        wires_manager.createMagnets(task2NodeShape);

        // Red end event.
        WiresShape endEventShape = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setStrokeColor("#FFFFFF"));
        endEventShape.setDraggable( true ).setResizable( true );
        endEventShape.setX(startX + 400).setY(startY);
        endEventShape.getContainer().setUserData("event");
        wires_manager.createMagnets(endEventShape);

        // Connector from blue start event to green task node.
        connect(layer, startEventShape.getMagnets(), 3, taskNodeShape.getMagnets(), 7, wires_manager);
        // Connector from green task node to red end event
        connect(layer, taskNodeShape.getMagnets(), 3, endEventShape.getMagnets(), 7, wires_manager);
        // Connector from blue start event to yellow task node.
        connect(layer, startEventShape.getMagnets(), 3, task2NodeShape.getMagnets(), 7, wires_manager);

    }

    private void connect(Layer layer, MagnetManager.Magnets magnets0, int i0_1, MagnetManager.Magnets magnets1, int i1_1, WiresManager wiresManager)
    {

        WiresMagnet m0_1 = magnets0.getMagnet(i0_1);
        WiresMagnet m1_1 = magnets1.getMagnet(i1_1);

        double x0, x1, y0, y1;

        MultiPath head = new MultiPath();
        head.M(15, 20);
        head.L(0, 20);
        head.L(15/2, 0);
        head.Z();

        MultiPath tail = new MultiPath();
        tail.M(15, 20);
        tail.L(0, 20);
        tail.L(15/2, 0);
        tail.Z();

        OrthogonalPolyLine line;
        x0 = m0_1.getControl().getX();
        y0 = m0_1.getControl().getY();
        x1 = m1_1.getControl().getX();
        y1 = m1_1.getControl().getY();
        line = createLine(layer, 0, 0, x0, y0, (x0 + ((x1-x0)/2)), (y0 + ((y1-y0)/2)), x1, y1);
        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());

        WiresConnector connector = wiresManager.createConnector(m0_1, m1_1, line,
                new MultiPathDecorator(head),
                new MultiPathDecorator(tail));

        head.setStrokeWidth(5).setStrokeColor("#0000CC");
        tail.setStrokeWidth(5).setStrokeColor("#0000CC");
        line.setStrokeWidth(5).setStrokeColor("#0000CC");
    }

    private final OrthogonalPolyLine createLine( final Layer layer,
                                                 final double... points )
    {
        final OrthogonalPolyLine line = new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
        layer.add( line );
        return line;
    }
}