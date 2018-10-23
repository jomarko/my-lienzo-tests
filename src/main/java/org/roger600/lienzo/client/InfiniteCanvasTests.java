package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import org.roger600.lienzo.client.widget.InfiniteLayer;
import org.roger600.lienzo.client.widget.InfiniteLienzoLayer;
import org.roger600.lienzo.client.widget.InfiniteLienzoPanel;
import org.roger600.lienzo.client.widget.InfiniteWiresLayer;

public class InfiniteCanvasTests implements EntryPoint
{
    private InfiniteLienzoPanel panel;

    private InfiniteLayer       layer;

    private WiresManager        wiresManager;

    private static final boolean IS_WIRES = true;

    public void onModuleLoad()
    {
        build();
        draw();
    }

    private void build()
    {
        panel = new InfiniteLienzoPanel(600, 600);
        if (IS_WIRES)
        {
            layer = new InfiniteWiresLayer();
        }
        else
        {
            layer = new InfiniteLienzoLayer();
        }

        final HorizontalPanel v = new HorizontalPanel();
        v.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        v.getElement().getStyle().setBorderColor("#000000");
        v.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        RootPanel.get().add(v);
        v.add(panel);

        panel.add(layer);
    }

    private void draw()
    {
        if (IS_WIRES)
        {
            drawWiresThings();
        }
        else
        {
            drawThings();
        }
        getLayer().draw();
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

        getLayer().add(r1);
        getLayer().add(r2);
    }

    private void drawWiresThings()
    {
        wiresManager = WiresManager.get(layer);
        wiresManager.setContainmentAcceptor(IContainmentAcceptor.ALL);
        wiresManager.setDockingAcceptor(IDockingAcceptor.ALL);
        wiresManager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        wiresManager.setConnectionAcceptor(IConnectionAcceptor.ALL);
        wiresManager.setLocationAcceptor(ILocationAcceptor.ALL);

        MultiPath redPath = new MultiPath().rect(0, 0, 100, 100)
                                           .setFillColor("#FF0000");
        WiresShape redShape = new WiresShape(redPath);
        wiresManager.register(redShape);
        redShape.setLocation(new Point2D(100, 100));
        redShape.setDraggable(true).getContainer().setUserData("red");
        TestsUtils.addResizeHandlers(redShape);
    }

    private Layer getLayer()
    {
        return layer;
    }
}
