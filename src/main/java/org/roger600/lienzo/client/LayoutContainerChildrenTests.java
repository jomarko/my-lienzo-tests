package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.layouts.impl.CardinalLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.layouts.base.FunctionalLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.layouts.base.FunctionalLayoutEntry;
import com.ait.lienzo.client.core.shape.wires.layouts.impl.StaticLayoutContainer;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class LayoutContainerChildrenTests extends FlowPanel implements MyLienzoTest, HasButtons, HasMediators {

    private Layer layer;
    private WiresShape parentShape;
    private Rectangle rectangle;
    private Circle circle;

    public void test(Layer _layer) {
        this.layer = _layer;

        final WiresManager wires_manager = WiresManager.get(layer);

        wires_manager.setContainmentAcceptor(new IContainmentAcceptor() {

            @Override
            public boolean containmentAllowed(WiresContainer parent, WiresShape[] children) {
                return true;
            }

            @Override
            public boolean acceptContainment(WiresContainer parent, WiresShape[] children) {
                return true;
            }
        });

        final MultiPath parentMultiPath = new MultiPath().rect(0, 0, 300, 300).setStrokeColor("#000000");
        parentShape = new WiresShape(parentMultiPath);

        TestsUtils.addResizeHandlers( parentShape );

        addRectangle();
        // addRCircle();

        wires_manager.register( parentShape );
        parentShape.setDraggable(true).setLocation(new Point2D(0, 0));
        wires_manager.getMagnetManager().createMagnets(parentShape);
    }

    private void addRectangle() {
        rectangle = new Rectangle( 100, 100).setFillColor("#0000CC").setDraggable(false);
        CardinalLayoutContainer container = (CardinalLayoutContainer) parentShape.getLayoutContainer();

        CardinalLayoutContainer childContainer = new CardinalLayoutContainer();
        childContainer.add(rectangle,
                           CardinalLayoutContainer.Cardinal.NORTHWEST);

        StaticLayoutContainer staticLayoutContainer = new StaticLayoutContainer();
        staticLayoutContainer.addAtCenter(rectangle);

        // container.add(rectangle, CardinalLayoutContainer.Cardinal.CENTER);
        // container.add(childContainer, CardinalLayoutContainer.Cardinal.CENTER);
        container.add(staticLayoutContainer, CardinalLayoutContainer.Cardinal.CENTER);

        batch();
    }


    private void addRCircle() {
        circle = new Circle( 50 ).setFillColor("#CCBB00").setDraggable(false);
        CardinalLayoutContainer container = (CardinalLayoutContainer) parentShape.getLayoutContainer();

        CardinalLayoutContainer childContainer = new CardinalLayoutContainer();
        childContainer.add(circle,
                           CardinalLayoutContainer.Cardinal.CENTER);

        StaticLayoutContainer staticLayoutContainer = new StaticLayoutContainer();
        staticLayoutContainer.addAtCenter(circle);

        // container.add(circle, CardinalLayoutContainer.Cardinal.CENTER);
        //container.add(childContainer, CardinalLayoutContainer.Cardinal.CENTER);
        container.add(staticLayoutContainer, CardinalLayoutContainer.Cardinal.CENTER);
        batch();

    }

    @Override
    public void setButtonsPanel( Panel panel ) {

        Button buttonRR = new Button( "Remove Rect" );
        buttonRR.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                removeRectangle();
            }
        } );

        panel.add( buttonRR );

        Button buttonAR = new Button( "Add Rect" );
        buttonAR.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                addRectangle();
            }
        } );

        panel.add( buttonAR );

        Button buttonRC = new Button( "Remove Circle" );
        buttonRC.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                removeCircle();
            }
        } );

        panel.add( buttonRC );

        Button buttonAC = new Button( "Add Circle" );
        buttonAC.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                addRCircle();
            }
        } );

        panel.add( buttonAC );
    }

    private void removeRectangle() {
        parentShape.remove( rectangle );
        batch();
    }

    private void removeCircle() {
        parentShape.remove( circle );
        batch();
    }

    private void batch() {
        layer.batch();
    }

}