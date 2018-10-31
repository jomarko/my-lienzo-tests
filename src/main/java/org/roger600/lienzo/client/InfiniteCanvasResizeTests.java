package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.impl.LienzoScrollablePanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.*;

public class InfiniteCanvasResizeTests implements EntryPoint
{
    private VerticalPanel         mainPanel;

    private HorizontalPanel       buttonsPanel;

    private ResizeLayoutPanel     resizablePanel;

    private LienzoScrollablePanel scrollablePanel;

    private Layer                 layer;

    private WiresManager          wiresManager;

    private WiresShape            redShape;

    private WiresShape            blueShape;

    public void onModuleLoad()
    {
        build();
        draw();
    }

    private void testButton()
    {
        newResizeButton(300, 300);
        newResizeButton(600, 600);
        newResizeButton(1200, 900);
    }

    private void newResizeButton(final double width,
                                 final double height)
    {
        Button r600 = new Button("Resize " + width + "x" + height);
        r600.addClickHandler(new ClickHandler()
        {
            @Override public void onClick(ClickEvent event)
            {
                resize(width, height);
            }
        });
        buttonsPanel.add(r600);
    }

    private void resize(final double width,
                        final double height)
    {
        resizablePanel.getElement().getStyle().setWidth(width, Style.Unit.PX);
        resizablePanel.getElement().getStyle().setHeight(height, Style.Unit.PX);
    }

    private void build()
    {
        mainPanel = new VerticalPanel();
        buttonsPanel = new HorizontalPanel();

        resizablePanel = new ResizeLayoutPanel()
        {
            @Override protected void onAttach()
            {
                super.onAttach();
                getElement().getParentElement().getStyle().setHeight(100.0, Style.Unit.PCT);
                getElement().getParentElement().getStyle().setWidth(100.0, Style.Unit.PCT);
            }
        };
        resizablePanel.addResizeHandler(new ResizeHandler()
        {
            @Override public void onResize(ResizeEvent event)
            {
                GWT.log("ResizeLayoutPanel - RESIZE");
                scrollablePanel.onResize();
            }
        });
        scrollablePanel = LienzoScrollablePanel.newWiresPanel();
        layer = new Layer();
        wiresManager = newWiresManager(layer);

        RootPanel.get().add(mainPanel);
        testButton();
        mainPanel.add(buttonsPanel);
        mainPanel.add(resizablePanel);
        resizablePanel.add(scrollablePanel);
        scrollablePanel.add(layer);

        resizablePanel.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        resizablePanel.getElement().getStyle().setMargin(0, Style.Unit.PX);
        resizablePanel.getElement().getStyle().setOutlineStyle(Style.OutlineStyle.NONE);
        resizablePanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        resizablePanel.getElement().getStyle().setBorderColor("#F0F0F0");
        resizablePanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        resizablePanel.getElement().getStyle().setWidth(100, Style.Unit.PCT);
        resizablePanel.getElement().getStyle().setHeight(100, Style.Unit.PCT);
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

    private void draw()
    {
        // drawBounds();
        drawWiresThings();
        layer.draw();
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

    private void drawBounds(int width,
                            int height)
    {
        final Line h = newLinesDecorator(width, 0);
        final Line v = newLinesDecorator(0, height);
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
}
