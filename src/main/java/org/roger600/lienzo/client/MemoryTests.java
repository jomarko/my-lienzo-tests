package org.roger600.lienzo.client;

import java.util.Stack;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;

public class MemoryTests implements MyLienzoTest,
                                    NeedsThePanel,
                                    HasButtons {

    private static final double startX = 50;
    private static final double startY = 50;
    private static final double size = 50;

    private Panel buttonsPanel;
    private LienzoPanel lienzoPanel;
    private Layer layer;
    private WiresManager wiresManager;
    private Stack<WiresShape> shapes;
    private Stack<HandlerRegistration> shapeHandlerRegs;
    private int current;
    private WiresShape source;
    private HandlerRegistration sourceReg;
    private WiresShape target;
    private HandlerRegistration targetReg;
    private WiresConnector connector;

    public void test(Layer layer) {
        this.layer = layer;
        current = 0;
        shapes = new Stack<>();
        shapeHandlerRegs = new Stack<>();

        wiresManager = WiresManager.get(layer);
        wiresManager.enableSelectionManager();

        wiresManager.setLocationAcceptor(ILocationAcceptor.ALL);
        wiresManager.setContainmentAcceptor(IContainmentAcceptor.ALL);
        wiresManager.setDockingAcceptor(IDockingAcceptor.ALL);
    }

    private void createParentShape() {
        WiresShape shape = new WiresShape(new MultiPath()
                                                  .rect(0, 0, size * 4, size * 4)
                                                  .setFillAlpha(1)
                                                  .setFillColor(ColorName.WHITE)
                                                  .setStrokeAlpha(1)
                                                  .setStrokeWidth(3)
                                                  .setStrokeColor(ColorName.BLACK));
        shape.setLocation(new Point2D(400, 200));
        registerShape(shape);
    }

    private void createShape() {
        WiresShape shape = new WiresShape(new MultiPath()
                                                  .rect(0, 0, size, size)
                                                  .setFillAlpha(1)
                                                  .setFillColor(ColorName.BLACK));
        double c = (current * size) + 20;
        shape.setLocation(new Point2D(startX + c, startY + c));
        registerShape(shape);
    }

    private void registerShape(WiresShape shape) {
        wiresManager.register(shape);
        shape.setDraggable(true);
        wiresManager.getMagnetManager().createMagnets(shape);
        shapeHandlerRegs.push(TestsUtils.addResizeHandlers(shape));
        shapes.push(shape);
        layer.draw();
        current++;
    }

    private void removeShape() {
        if (!shapes.isEmpty()) {
            final WiresShape shape = shapes.pop();
            final HandlerRegistration reg = shapeHandlerRegs.pop();
            wiresManager.deregister(shape);
            reg.removeHandler();
            layer.draw();
            current--;
        }
    }

    private void testConnector() {
        if (null == source) {

            source = new WiresShape(new MultiPath()
                                            .rect(0, 0, size, size)
                                            .setFillAlpha(1)
                                            .setFillColor(ColorName.RED));
            wiresManager.register(source);
            source.setLocation(new Point2D(50, 400));
            source.setDraggable(true);
            wiresManager.getMagnetManager().createMagnets(source);
            sourceReg = TestsUtils.addResizeHandlers(source);

            target = new WiresShape(new MultiPath()
                                            .rect(0, 0, size, size)
                                            .setFillAlpha(1)
                                            .setFillColor(ColorName.RED));
            wiresManager.register(target);
            target.setLocation(new Point2D(200, 400));
            target.setDraggable(true);
            wiresManager.getMagnetManager().createMagnets(target);
            targetReg = TestsUtils.addResizeHandlers(target);

            connect(layer, source.getMagnets(), 3, target.getMagnets(), 7, wiresManager);
            layer.draw();
        }
    }

    private void testRemoveConnector() {
        if (null != connector) {
            wiresManager.deregister(connector);
            connector = null;
        }
        if (null != source) {
            wiresManager.deregister(source);
            sourceReg.removeHandler();
            source = null;
            sourceReg = null;
        }
        if (null != target) {
            wiresManager.deregister(target);
            targetReg.removeHandler();
            target = null;
            targetReg = null;
        }
        layer.draw();
    }

    private void destroy() {
        while (!shapes.isEmpty()) {
            removeShape();
        }
        shapeHandlerRegs.clear();
        shapeHandlerRegs = null;
        shapes.clear();
        shapes = null;
        WiresManager.remove(wiresManager);
        lienzoPanel.removeAll();
        lienzoPanel.removeFromParent();
        lienzoPanel = null;
        wiresManager = null;
        layer = null;
        buttonsPanel.clear();
        buttonsPanel = null;
    }

    @Override
    public void setButtonsPanel(Panel panel) {
        this.buttonsPanel = panel;

        final Button addShape = new Button("Add shape");
        addShape.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                createShape();
            }
        });
        panel.add(addShape);

        final Button parentShape = new Button("Add parent");
        parentShape.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                createParentShape();
            }
        });
        panel.add(parentShape);

        final Button removeShape = new Button("Delete shape");
        removeShape.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                removeShape();
            }
        });
        panel.add(removeShape);

        final Button testRemoveConnector = new Button("Delete connector test");
        testRemoveConnector.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                testRemoveConnector();
            }
        });
        panel.add(testRemoveConnector);

        final Button testConnector = new Button("Add connector test");
        testConnector.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                testConnector();
            }
        });
        panel.add(testConnector);

        final Button destroy = new Button("Destroy");
        destroy.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                destroy();
            }
        });
        panel.add(destroy);
    }

    @Override
    public void setLienzoPanel(LienzoPanel lienzoPanel) {
        this.lienzoPanel = lienzoPanel;
    }

    private void connect(Layer layer, MagnetManager.Magnets magnets0, int i0_1, MagnetManager.Magnets magnets1, int i1_1, WiresManager wiresManager) {

        WiresMagnet m0_1 = magnets0.getMagnet(i0_1);
        WiresMagnet m1_1 = magnets1.getMagnet(i1_1);

        double x0, x1, y0, y1;

        MultiPath head = new MultiPath();
        head.M(15, 20);
        head.L(0, 20);
        head.L(15 / 2, 0);
        head.Z();

        MultiPath tail = new MultiPath();
        tail.M(15, 20);
        tail.L(0, 20);
        tail.L(15 / 2, 0);
        tail.Z();

        OrthogonalPolyLine line;
        x0 = m0_1.getControl().getX();
        y0 = m0_1.getControl().getY();
        x1 = m1_1.getControl().getX();
        y1 = m1_1.getControl().getY();
        line = createLine(layer, 0, 0, x0, y0, (x0 + ((x1 - x0) / 2)), (y0 + ((y1 - y0) / 2)), x1, y1);
        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());

        connector = new WiresConnector(m0_1, m1_1, line,
                                       new MultiPathDecorator(head),
                                       new MultiPathDecorator(tail));

        wiresManager.register(connector);

        head.setStrokeWidth(5).setStrokeColor("#0000CC");
        tail.setStrokeWidth(5).setStrokeColor("#0000CC");
        line.setStrokeWidth(5).setStrokeColor("#0000CC");
    }

    private final OrthogonalPolyLine createLine(final Layer layer,
                                                final double... points) {
        final OrthogonalPolyLine line = new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
        layer.add(line);
        return line;
    }
}