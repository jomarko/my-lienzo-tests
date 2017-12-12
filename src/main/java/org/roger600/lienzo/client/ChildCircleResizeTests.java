package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.shape.wires.layouts.cardinals.WiresCardinalLayoutContainer;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class ChildCircleResizeTests extends FlowPanel implements MyLienzoTest, HasButtons, HasMediators {

    WiresManager wires_manager;

    public void test(Layer layer) {

        this.wires_manager = WiresManager.get( layer );
        testAllLayouts( layer );
    }

    @Override
    public void setButtonsPanel( Panel panel ) {

        showLayoutButtons( panel );

    }

    private WiresShape currentShape;

    public void showLayoutButtons( Panel panel ) {

        WiresCardinalLayoutContainer.Cardinals[] layouts = new WiresCardinalLayoutContainer.Cardinals[] {
                WiresCardinalLayoutContainer.Cardinals.CENTER,
                WiresCardinalLayoutContainer.Cardinals.EAST,
                WiresCardinalLayoutContainer.Cardinals.WEST,
                WiresCardinalLayoutContainer.Cardinals.NORTH,
                WiresCardinalLayoutContainer.Cardinals.NORTHEAST,
                WiresCardinalLayoutContainer.Cardinals.NORTHWEST,
                WiresCardinalLayoutContainer.Cardinals.SOUTH,
                WiresCardinalLayoutContainer.Cardinals.SOUTHEAST,
                WiresCardinalLayoutContainer.Cardinals.SOUTHWEST
        };

        for ( final WiresCardinalLayoutContainer.Cardinals layout : layouts ) {

            Button button = new Button( layout.name() );

            button.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent clickEvent ) {

                    if ( null != currentShape ) {

                        wires_manager.getLayer().remove( currentShape );

                    }

                    currentShape = create( 800, 400, 200, 200, "#CC00EE", "#FFFFFF" );
                    WiresCardinalLayoutContainer container = (WiresCardinalLayoutContainer)currentShape.getLayoutContainer();
                    Circle circle1 = new Circle(50).setFillColor("#00CCCC").setDraggable(false);
                    container.add(circle1, layout);

                }
            } );

            panel.add( button );
        }

    }

    private void testAllLayouts(Layer layer) {
        WiresShape shapeTop = create( 400, 50, 100, 100, "#CC0000", "#FFFFFF" );
        WiresCardinalLayoutContainer topContainer = (WiresCardinalLayoutContainer)shapeTop.getLayoutContainer();
        Circle circle1 = new Circle(25).setFillColor("#0000CC").setDraggable(false);
        topContainer.add(circle1, WiresCardinalLayoutContainer.Cardinals.NORTH);

        WiresShape shapeWest = create( 50, 200, 100, 100, "#00CC00", "#FFFFFF" );
        WiresCardinalLayoutContainer westContainer = (WiresCardinalLayoutContainer)shapeTop.getLayoutContainer();
        Circle circle2 = new Circle(25).setFillColor("#0000CC").setDraggable(false);
        westContainer.add(circle2, WiresCardinalLayoutContainer.Cardinals.WEST);

        WiresShape shapeCenter = create( 400, 200, 100, 100, "#0000CC", "#FFFFFF" );
        WiresCardinalLayoutContainer centerContainer = (WiresCardinalLayoutContainer)shapeTop.getLayoutContainer();
        Circle circle3 = new Circle(25).setFillColor("#CCCCCC").setDraggable(false);
        centerContainer.add(circle3, WiresCardinalLayoutContainer.Cardinals.CENTER);

        WiresShape shapeEast = create( 800, 200, 100, 100, "#0000FF", "#FFFFFF" );
        WiresCardinalLayoutContainer eastContainer = (WiresCardinalLayoutContainer)shapeTop.getLayoutContainer();
        Circle circle4 = new Circle(25).setFillColor("#CC00CC").setDraggable(false);
        eastContainer.add(circle4, WiresCardinalLayoutContainer.Cardinals.EAST);

        WiresShape shapeSouth = create( 400, 400, 100, 100, "#FF00FF", "#FFFFFF" );
        WiresCardinalLayoutContainer southContainer = (WiresCardinalLayoutContainer)shapeTop.getLayoutContainer();
        Circle circle5 = new Circle(25).setFillColor("#0000CC").setDraggable(false);
        southContainer.add(circle5, WiresCardinalLayoutContainer.Cardinals.SOUTH);
    }

    private WiresShape create( final double x,
                               final double y,
                               final double w,
                               final double h,
                               final String fillColor,
                               final String strokeColor) {

        WiresShape endEventShape = new WiresShape( new MultiPath().rect( 0, 0, w, h )
                .setStrokeColor( strokeColor ).setFillColor( fillColor ), new WiresCardinalLayoutContainer());
        endEventShape.setLocation(new Point2D(x, y));
        wires_manager.register( endEventShape );
        wires_manager.getMagnetManager().createMagnets( endEventShape );

        TestsUtils.addResizeHandlers( endEventShape );

        endEventShape.addWiresResizeStartHandler( new WiresResizeStartHandler() {
            @Override
            public void onShapeResizeStart( final WiresResizeStartEvent event ) {
                log( "onShapeResizeStart [x=" + event.getX() + ", y=" + event.getY()
                        + ", width=" + event.getWidth()
                        + ", height=" + event.getHeight() + "]" );
            }
        } );

        endEventShape.addWiresResizeStepHandler( new WiresResizeStepHandler() {
            @Override
            public void onShapeResizeStep( WiresResizeStepEvent event ) {
                log( "onShapeResizeStep [x=" + event.getX() + ", y=" + event.getY()
                        + ", width=" + event.getWidth()
                        + ", height=" + event.getHeight() + "]" );
            }
        } );

        endEventShape.addWiresResizeEndHandler( new WiresResizeEndHandler() {
            @Override
            public void onShapeResizeEnd( WiresResizeEndEvent event ) {
                log( "onShapeResizeEnd [x=" + event.getX() + ", y=" + event.getY()
                        + ", width=" + event.getWidth()
                        + ", height=" + event.getHeight() + "]" );
            }
        } );

        return endEventShape;
    }


    private void log(String s) {
        // GWT.log( s );
    }


}
