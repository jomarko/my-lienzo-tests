package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;

public class SomeSimpleTest implements MyLienzoTest {

    public void test(Layer layer) {
        final Rectangle rectangle = new Rectangle(100, 100).setFillColor(ColorName.BLACK).setDraggable(true);
        layer.add(rectangle);

    }

}