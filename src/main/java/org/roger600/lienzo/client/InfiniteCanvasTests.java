package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import org.roger600.lienzo.client.widget.InfiniteLienzoLayer;
import org.roger600.lienzo.client.widget.InfiniteLienzoPanel;

public class InfiniteCanvasTests implements EntryPoint
{
    // private LienzoPanel panel = new LienzoPanel(600, 600);
    private InfiniteLienzoPanel panel = new InfiniteLienzoPanel(600, 600);

    // private Layer       layer = new Layer();
    private InfiniteLienzoLayer layer = new InfiniteLienzoLayer();

    public void onModuleLoad()
    {
        drawLayout();
        drawThings();
        getLayer().draw();
    }

    private void drawLayout()
    {
        final HorizontalPanel v = new HorizontalPanel();
        v.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        v.getElement().getStyle().setBorderColor("#000000");
        v.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        RootPanel.get().add(v);
        v.add(panel);

        // getLayer().setTransformable(true);
        panel.add(layer);
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

    private Layer getLayer()
    {
        return layer;
    }
}
