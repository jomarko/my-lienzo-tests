package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.mediator.*;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.panel.impl.LienzoResizablePanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoScrollablePanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class InfiniteCanvasTests implements EntryPoint
{
    private final        IEventFilter[] zommFilters  = new IEventFilter[]{EventFilter.CONTROL};

    private final        IEventFilter[] panFilters   = new IEventFilter[]{EventFilter.SHIFT};

    private static final int            PANEL_WIDTH  = 600;

    private static final int            PANEL_HEIGHT = 600;

    private static final boolean        IS_WIRES     = true;

    private LienzoScrollablePanel panel;

    private Layer                 layer;

    private LienzoResizablePanel  previewPanel;

    private Layer                 previewLayer;

    private WiresManager          wiresManager;

    private WiresManager          previewWiresManager;

    private WiresShape            redShape;

    private WiresShape            blueShape;

    private WiresShape            previewRedShape;

    private WiresShape            previewBlueShape;

    public void onModuleLoad()
    {
        build();
        draw();
    }

    private void build()
    {
        if (IS_WIRES)
        {
            panel = LienzoScrollablePanel.newWiresPanel(PANEL_WIDTH, PANEL_HEIGHT);
            previewPanel = LienzoResizablePanel.newWiresPanel(PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        }
        else
        {
            panel = LienzoScrollablePanel.newPrimitivePanel(PANEL_WIDTH, PANEL_HEIGHT);
            previewPanel = LienzoResizablePanel.newPrimitivePanel(PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        }

        panel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        panel.getElement().getStyle().setBorderColor("#000000");
        panel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        previewPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        previewPanel.getElement().getStyle().setBorderColor("#000000");
        previewPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        final HorizontalPanel v = new HorizontalPanel();
        RootPanel.get().add(v);
        v.add(panel);
        v.add(previewPanel);

        layer = new Layer();
        previewLayer = new Layer();

        panel.add(layer);
        previewPanel.add(previewLayer);

        wiresManager = newWiresManager(layer);
        previewWiresManager = newWiresManager(previewLayer);

        addMediators(layer);

        // Scale the preview panel.
        //scaleLienzoPanel();
        previewPanel.refresh();
    }

    private void scaleLienzoPanel()
    {
        int      toWidth     = 300;
        int      toHeight    = 300;
        double[] scaleFactor = getScaleFactor(PANEL_WIDTH, PANEL_HEIGHT, toWidth, toHeight);
        double   factor      = scaleFactor[0] >= scaleFactor[1] ? scaleFactor[1] : scaleFactor[0];
        previewPanel.setPixelSize(toWidth, toHeight);
        Transform transform = previewLayer.getViewport().getTransform();
        if (null == transform)
        {
            transform = new Transform();
            previewLayer.getViewport().setTransform(transform);
        }
        transform.scale(factor);
    }

    private static WiresManager newWiresManager(Layer layer)
    {
        WiresManager wiresManager = WiresManager.get(layer);
        wiresManager.setContainmentAcceptor(IContainmentAcceptor.ALL);
        wiresManager.setDockingAcceptor(IDockingAcceptor.ALL);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        wiresManager.setConnectionAcceptor(IConnectionAcceptor.ALL);
        wiresManager.setLocationAcceptor(ILocationAcceptor.ALL);
        return wiresManager;
    }

    private void addMediators(Layer layer)
    {
        final Mediators mediators = layer.getViewport().getMediators();
        mediators.push(new MouseWheelZoomMediator(zommFilters));
        mediators.push(new MousePanMediator(panFilters));
    }

    private void draw()
    {
        drawBounds();
        if (IS_WIRES)
        {
            drawWiresThings();
            drawPreviewWiresThings();
        }
        else
        {
            drawThings();
        }
        layer.draw();
        previewLayer.draw();
    }

    private static Line newLinesDecorator(int width,
                                          int height)
    {
        return new Line(0, 0, width, height)
                .setDraggable(false)
                .setListening(false)
                .setFillAlpha(0)
                .setStrokeAlpha(0)
                .setStrokeAlpha(0.8)
                .setStrokeWidth(1)
                .setStrokeColor("#d3d3d3")
                .setDashArray(5);
    }

    private void drawBounds()
    {
        final Line h = newLinesDecorator(PANEL_WIDTH, 0);
        final Line v = newLinesDecorator(0, PANEL_HEIGHT);
        layer.getScene().getTopLayer().add(h);
        layer.getScene().getTopLayer().add(v);

        /*final Rectangle bounds = new Rectangle(PANEL_WIDTH, PANEL_HEIGHT)
                .setX(0)
                .setY(0)
                .setStrokeAlpha(1)
                .setStrokeWidth(1)
                .setStrokeColor(ColorName.GREY)
                .setDashArray(5);

        getLayer().getScene().getTopLayer().add(bounds);*/
    }

    private void drawThings()
    {

        final Rectangle r1 = new Rectangle(50, 50);
        r1.setX(50).setY(50);
        r1.setFillColor("#FF0000");
        r1.setDraggable(true);

        final Rectangle r2 = new Rectangle(100, 100);
        r2.setX(150).setY(150);
        r2.setFillColor("#0000FF");
        r2.setDraggable(true);

        layer.add(r1);
        layer.add(r2);
    }

    private void drawWiresThings()
    {

        MultiPath redPath = new MultiPath().rect(0, 0, 100, 100)
                                           .setFillColor("#FF0000");
        redShape = new WiresShape(redPath);
        wiresManager.register(redShape);
        redShape.setLocation(new Point2D(300, 300));
        redShape.setDraggable(true).getContainer().setUserData("red");
        TestsUtils.addResizeHandlers(redShape);

        MultiPath bluePath = new MultiPath().rect(0, 0, 100, 100)
                                            .setFillColor("#0000FF");
        blueShape = new WiresShape(bluePath);
        wiresManager.register(blueShape);
        blueShape.setLocation(new Point2D(50, 50));
        blueShape.setDraggable(true).getContainer().setUserData("blue");
        TestsUtils.addResizeHandlers(blueShape);
    }

    private void drawPreviewWiresThings()
    {

        wiresManager.setLocationAcceptor(new ILocationAcceptor()
        {
            @Override public boolean allow(WiresContainer[] shapes, Point2D[] locations)
            {
                return true;
            }

            @Override public boolean accept(WiresContainer[] shapes, Point2D[] locations)
            {
                for (int i = 0; i < shapes.length; i++)
                {
                    WiresContainer shape    = shapes[i];
                    Point2D        location = locations[i];
                    if (shape.getGroup().getUserData().equals("red"))
                    {
                        GWT.log("Moving RED TO [" + location + "]");
                        previewRedShape.setLocation(location.copy());
                    }

                    if (shape.getGroup().getUserData().equals("blue"))
                    {
                        GWT.log("Moving BLUE TO [" + location + "]");
                        previewBlueShape.setLocation(location.copy());
                    }
                }

                previewPanel.refresh();

                return true;
            }
        });

        MultiPath redPath = new MultiPath().rect(0, 0, 100, 100)
                                           .setFillColor("#FF0000");
        previewRedShape = new WiresShape(redPath);
        previewWiresManager.register(previewRedShape);
        previewRedShape.setLocation(new Point2D(300, 300));
        previewRedShape.setDraggable(true).getContainer().setUserData("red");

        MultiPath bluePath = new MultiPath().rect(0, 0, 100, 100)
                                            .setFillColor("#0000FF");
        previewBlueShape = new WiresShape(bluePath);
        previewWiresManager.register(previewBlueShape);
        previewBlueShape.setLocation(new Point2D(50, 50));
        previewBlueShape.setDraggable(true).getContainer().setUserData("blue");

        previewPanel.refresh();
    }

    private static double[] getScaleFactor(final double width,
                                           final double height,
                                           final double targetWidth,
                                           final double targetHeight)
    {
        return new double[]{
                width > 0 ? targetWidth / width : 1,
                height > 0 ? targetHeight / height : 1};
    }
}
