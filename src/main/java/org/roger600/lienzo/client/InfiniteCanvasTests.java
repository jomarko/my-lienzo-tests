package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class InfiniteCanvasTests implements EntryPoint
{
    private LienzoPanel panel1 = new LienzoPanel(600, 600);

    private Layer       layer1 = new Layer();

    public void onModuleLoad()
    {
        drawLayout();
        drawThings();
        layer1.draw();
    }

    private void drawLayout()
    {
        final HorizontalPanel v = new HorizontalPanel();
        v.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        v.getElement().getStyle().setBorderColor("#000000");
        v.getElement().getStyle().setBorderWidth(5, Style.Unit.PX);

        RootPanel.get().add(v);
        v.add(panel1);

        layer1.setTransformable(true);
        panel1.add(layer1);
    }

    private void drawThings()
    {

        final Rectangle r1 = new Rectangle(50, 50);
        r1.setX(50).setY(50);

        final Rectangle r2 = new Rectangle(100, 100);
        r2.setX(150).setY(150);

        layer1.add(r1);
        layer1.add(r2);
    }
}
