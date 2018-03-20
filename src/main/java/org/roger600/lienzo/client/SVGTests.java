package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class SVGTests extends FlowPanel implements MyLienzoTest,
                                                   HasButtons,
                                                   NeedsThePanel {

    private LienzoPanel lienzoPanel;
    private Layer layer;

    @Override
    public void test(Layer layer) {
        this.layer = layer;
        gateway(false)
                .setX(100)
                .setY(100);

        gateway(true)
                .setX(100)
                .setY(500);

        task(false)
                .setX(400)
                .setY(100);

        task(true)
                .setX(400)
                .setY(500);

        event(false)
                .setX(800)
                .setY(100);

        event(true)
                .setX(800)
                .setY(500);

        // testDashes();
        // testShapes();
        // testWires();
    }

    private static final Shadow SHADOW_SELECTED = new Shadow(ColorName.BLACK.getColor().setA(0.70), 5, 2, 2);
    private static final Shadow SHADOW = new Shadow(ColorName.BLACK.getColor().setA(0.70), 75, 0, 0);

    private void applyHighlight(final Shape backgroundShape,
                                final Shape borderShape) {
        final double borderWidth = borderShape.getStrokeWidth() > 0 ?
                borderShape.getStrokeWidth() * 2 :
                2d;
        backgroundShape.setStrokeWidth(borderWidth).setStrokeColor(ColorName.WHITE.getColorString()).setStrokeAlpha(1);
        backgroundShape.setShadow(SHADOW);
    }

    /*
        <rect class="task_border" x="0" y="0" width="150px" height="98px" rx="2" ry="2"/>
        <rect class="task_fill" x="0" y="0" width="150px" height="98px" rx="2" ry="2"/>
     */
    private Group task(final boolean highlight) {
        Group container = new Group();

        Rectangle border = new Rectangle(150, 90)
                .setX(0)
                .setY(0)
                .setCornerRadius(2)
                .setStrokeAlpha(1)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeWidth(1.5d)
                .setFillAlpha(0);

        Rectangle background = new Rectangle(150, 90)
                .setX(0)
                .setY(0)
                .setCornerRadius(2)
                .setStrokeAlpha(0)
                .setFillColor("#f2f2f2")
                .setFillAlpha(1);


        container.add(border);
        container.add(background);

        if (highlight) {
            applyHighlight(background,
                           border);
        }

        layer.add(container);
        return container;
    }

    /*
        <path id="event-background" class="st0" d="M444,224c0,39.9-9.8,76.8-29.6,110.5c-19.7,33.7-46.4,60.4-80,80S263.9,444,224,444
        s-76.8-9.8-110.5-29.6c-33.7-19.7-60.4-46.4-80-80S4,263.9,4,224s9.8-76.8,29.6-110.5s46.5-60.4,80-80S184.1,4,224,4
        s76.8,9.8,110.5,29.6s60.4,46.5,80,80S444,184.1,444,224z"/>
        <g id="event-type">
            <path id="end" class="st0" stunner:shape-state="fill" d="M224,0C100.3,0,0,100.3,0,224s100.3,224,224,224s224-100.3,224-224S347.7,0,224,0z M224,400
                c-97.2,0-176-78.8-176-176S126.8,48,224,48s176,78.8,176,176S321.2,400,224,400z"/>
     */
    private Group event(final boolean highlight) {
        Group container = new Group();

        MultiPath background = new MultiPath("M444,224c0,39.9-9.8,76.8-29.6,110.5c-19.7,33.7-46.4,60.4-80,80S263.9,444,224,444\n" +
                                                     "    s-76.8-9.8-110.5-29.6c-33.7-19.7-60.4-46.4-80-80S4,263.9,4,224s9.8-76.8,29.6-110.5s46.5-60.4,80-80S184.1,4,224,4\n" +
                                                     "    s76.8,9.8,110.5,29.6s60.4,46.5,80,80S444,184.1,444,224z")
                .setFillColor("#fce7e7");

        Group group1 = new Group();

        MultiPath endPath = new MultiPath("M224,0C100.3,0,0,100.3,0,224s100.3,224,224,224s224-100.3,224-224S347.7,0,224,0z M224,400\n" +
                                                  "                c-97.2,0-176-78.8-176-176S126.8,48,224,48s176,78.8,176,176S321.2,400,224,400z")
                .setFillColor("#a30000");

        group1.add(endPath);

        container.add(background);
        container.add(group1);
        container.setScale(0.5, 0.5);

        if (highlight) {
            applyHighlight(background,
                           endPath);
        }

        layer.add(container);
        return container;
    }

    private Group gateway(final boolean highlight) {

        MultiPath background = new MultiPath("M224.4,4.3c-8,0-16,3-22.1,9.1L13.5,202.2c-12.2,12.2-12.2,32,0,44.2l188.7,188.7\n" +
                                                     "\t\tc6.1,6.1,14.2,9.1,22.1,9.1s16-3,22.1-9.1l188.7-188.7c12.2-12.2,12.2-32,0-44.2L246.5,13.4C240.4,7.3,232.4,4.3,224.4,4.3\n" +
                                                     "\t\tL224.4,4.3z");

        MultiPath gateway = new MultiPath("M20.8,212.7L213.3,20.2c6.3-6.3,16.4-6.3,22.6,0l192.5,192.5c6.3,6.3,6.3,16.4,0,22.6\n" +
                                                  "\t\tL235.9,427.8c-6.3,6.3-16.4,6.3-22.6,0L20.8,235.3C14.5,229.1,14.5,218.9,20.8,212.7L20.8,212.7z M0,224c0,8.1,3.1,16.3,9.3,22.5\n" +
                                                  "\t\tl192.2,192.2c12.4,12.4,32.6,12.4,45,0l192.2-192.2c6.2-6.2,9.3-14.4,9.3-22.5s-3.1-16.3-9.3-22.5L246.5,9.3\n" +
                                                  "\t\tc-12.4-12.4-32.6-12.4-45,0L9.3,201.5C3.1,207.7,0,215.9,0,224L0,224z");
        Group container = new Group();

        container.add(background);
        container.add(gateway);
        container.setScale(0.5, 0.5);

        background.setFillColor("#fef4ea");
        gateway.setFillColor("#ec7a08");

        if (highlight) {
            applyHighlight(background,
                           gateway);
        }

        layer.add(container);
        return container;
    }

    private void testWires() {
        WiresManager wires_manager = WiresManager.get(layer);

        final double startX = 300;
        final double startY = 300;
        final double w = 100;
        final double h = 100;

        wires_manager.setContainmentAcceptor(new IContainmentAcceptor() {
            @Override
            public boolean containmentAllowed(WiresContainer parent, WiresShape[] children) {
                if (null != parent &&
                        null != parent.getGroup() &&
                        null != parent.getGroup().getUserData() &&
                        parent.getGroup().getUserData().equals("green")) {
                    lienzoPanel.getElement().getStyle().setProperty("cursor", "not-allowed");
                    return false;
                }
                lienzoPanel.getElement().getStyle().setCursor(Style.Cursor.AUTO);
                return true;
            }

            @Override
            public boolean acceptContainment(WiresContainer parent, WiresShape[] children) {
                return true;
            }
        });

        WiresShape greenShape = new WiresShape(new MultiPath().rect(0, 0, w, h).setFillColor("#00CC00"));
        wires_manager.register(greenShape);
        greenShape.setLocation(new Point2D(startX + 200, startY));
        greenShape.setDraggable(true).getContainer().setUserData("green");

        WiresShape blueShape = new WiresShape(new MultiPath().rect(0, 0, w, h).setFillColor("#0000FF"));
        wires_manager.register(blueShape);
        blueShape.setLocation(new Point2D(startX + 400, startY));
        blueShape.setDraggable(true).getContainer().setUserData("blue");

        wires_manager.getMagnetManager().createMagnets(greenShape);
        wires_manager.getMagnetManager().createMagnets(blueShape);

        //connectPolyine(layer, greenShape.getMagnets(), 3, blueShape.getMagnets(), 7, wires_manager);

    }

    private void connect(Layer layer,
                         MagnetManager.Magnets magnets0,
                         int i0_1,
                         MagnetManager.Magnets magnets1,
                         int i1_1,
                         WiresManager wiresManager) {
        WiresMagnet m0_1 = (WiresMagnet) magnets0.getMagnet(i0_1);
        WiresMagnet m1_1 = (WiresMagnet) magnets1.getMagnet(i1_1);

        double x0, x1, y0, y1;

        MultiPath head = new MultiPath();
        head.M(15,
               20);
        head.L(0,
               20);
        head.L(15 / 2,
               0);
        head.Z();

        MultiPath tail = new MultiPath();
        tail.M(15,
               20);
        tail.L(0,
               20);
        tail.L(15 / 2,
               0);
        tail.Z();

        AbstractDirectionalMultiPointShape<?> line;
        x0 = m0_1.getControl().getX();
        y0 = m0_1.getControl().getY();
        x1 = m1_1.getControl().getX();
        y1 = m1_1.getControl().getY();
        // Orthogonal.
        line = createPolyLine(
                x0,
                y0,
                (x0 + ((x1 - x0) / 2)),
                (y0 + ((y1 - y0) / 2)),
                x1,
                y1);
        // Directional.
        /*line = createDirectionalLine(
                x0,
                y0,
                x1,
                y1);*/
        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());
        line.setSelectionStrokeOffset(25);

        WiresConnector connector = new WiresConnector(m0_1,
                                                      m1_1,
                                                      line,
                                                      new MultiPathDecorator(head),
                                                      new MultiPathDecorator(tail));
        wiresManager.register(connector);

        head.setStrokeWidth(1).setStrokeColor("#000000");
        tail.setStrokeWidth(1).setStrokeColor("#000000");
        line.setStrokeWidth(1).setStrokeColor("#000000");

        line.setShadow(SHADOW_SELECTED);
    }

    private void connectPolyine(Layer layer,
                                MagnetManager.Magnets magnets0,
                                int i0_1,
                                MagnetManager.Magnets magnets1,
                                int i1_1,
                                WiresManager wiresManager) {
        WiresMagnet m0_1 = (WiresMagnet) magnets0.getMagnet(i0_1);
        WiresMagnet m1_1 = (WiresMagnet) magnets1.getMagnet(i1_1);

        double x0, x1, y0, y1;

        MultiPath head = new MultiPath();
        head.M(15,
               20);
        head.L(0,
               20);
        head.L(15 / 2,
               0);
        head.Z();

        MultiPath tail = new MultiPath();
        tail.M(15,
               20);
        tail.L(0,
               20);
        tail.L(15 / 2,
               0);
        tail.Z();

        x0 = m0_1.getControl().getX();
        y0 = m0_1.getControl().getY();
        x1 = m1_1.getControl().getX();
        y1 = m1_1.getControl().getY();

        final AbstractDirectionalMultiPointShape<?> line =
                new PolyLine(Point2DArray.fromArrayOfDouble(new double[]{x0,
                        y0,
                        (x0 + ((x1 - x0) / 2)),
                        (y0 + ((y1 - y0) / 2)),
                        x1,
                        y1}));

        line.setDraggable(true);
        line.setSelectionStrokeOffset(25);
        line.setHeadOffset(head.getBoundingBox().getHeight());
        line.setTailOffset(tail.getBoundingBox().getHeight());

        WiresConnector connector = new WiresConnector(m0_1,
                                                      m1_1,
                                                      line,
                                                      new MultiPathDecorator(head),
                                                      new MultiPathDecorator(tail));
        wiresManager.register(connector);

        head.setStrokeWidth(1).setStrokeColor("#000000");
        tail.setStrokeWidth(1).setStrokeColor("#000000");
        line.setStrokeWidth(1).setStrokeColor("#000000");
    }

    private OrthogonalPolyLine createPolyLine(final double... points) {
        return new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
    }

    private final OrthogonalPolyLine createLine(final double... points) {
        return new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
    }

    @Override
    public void setButtonsPanel(Panel panel) {
        Button b1 = new Button("remove shadow");
        b1.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                layer.draw();
            }
        });
        panel.add(b1);
    }

    @Override
    public void setLienzoPanel(LienzoPanel lienzoPanel) {
        this.lienzoPanel = lienzoPanel;
    }
}
