package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.mediator.*;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;
import com.ait.lienzo.client.widget.panel.impl.PreviewPanel;
import com.ait.lienzo.client.widget.panel.scrollbars.ScrollablePanel;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.tooling.common.api.java.util.function.Function;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.*;
import org.roger600.lienzo.client.panel.ResizeFlowPanel;

public class InfiniteCanvasTests implements EntryPoint
{
    private final        IEventFilter[] zommFilters  = new IEventFilter[]{EventFilter.CONTROL};

    private final        IEventFilter[] panFilters   = new IEventFilter[]{EventFilter.SHIFT};

    private static final int            PANEL_WIDTH  = 900; // 600 -> 900

    private static final int            PANEL_HEIGHT = 600;

    private static final double         ASPECT_RATIO = 300 / 150;

    private static final int            PREVIEWL_WIDTH  = 300;

    private static final boolean        IS_WIRES     = true;

    private ScrollablePanel panel;

    private HorizontalPanel previewContainer;

    private ScrollabelPanelPresenter panelPresenter;

    private Layer           layer;

    private PreviewPanel    previewPanel;

    private Layer           previewLayer;

    private WiresManager    wiresManager;

    private WiresManager    previewWiresManager;

    private WiresShape      redShape;

    private WiresShape      blueShape;

    private WiresShape      previewRedShape;

    private WiresShape      previewBlueShape;

    private class ScrollabelPanelPresenter implements IsWidget {
        @Override
        public Widget asWidget()
        {
            return panel;
        }
    }
    public void onModuleLoad()
    {
        build();
        draw();
    }

    private void build()
    {
        BoundsProviderFactory.FunctionalBoundsProvider boundsProvider = null;
        if (IS_WIRES)
        {
            boundsProvider = new BoundsProviderFactory.WiresBoundsProvider();
        }
        else
        {
            boundsProvider = new BoundsProviderFactory.PrimitivesBoundsProvider();
        }
        if (true)
        {
            boundsProvider.setBoundsBuilder(new Function<BoundingBox, Bounds>() {
                @Override public Bounds apply(BoundingBox boundingBox)
                {
                    return BoundsProviderFactory.computeBoundsAspectRatio(ASPECT_RATIO, boundingBox);
                }
            });
        }
        panel = new ScrollablePanel(boundsProvider,
                                    PANEL_WIDTH,
                                    PANEL_HEIGHT);
        panelPresenter = new ScrollabelPanelPresenter();


        panel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        panel.getElement().getStyle().setBorderColor("#000000");
        panel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);


        final VerticalPanel   v = new VerticalPanel();
        final HorizontalPanel b = new HorizontalPanel();
        previewContainer = new HorizontalPanel();

        addButtons(b);

        v.add(b);
        v.add(previewContainer);

        final ResizeFlowPanel resizeFlowPanel = new ResizeFlowPanel();
        resizeFlowPanel.addDomHandler(new ContextMenuHandler() {
            @Override public void onContextMenu(ContextMenuEvent event)
            {
                GWT.log("PREVENING CONTEXT MENU EVENT!!");
                event.preventDefault();
                event.stopPropagation();
            }
        }, ContextMenuEvent.getType());

        resizeFlowPanel.addAttachHandler(new AttachEvent.Handler()
        {
            @Override public void onAttachOrDetach(AttachEvent event)
            {
                if (event.isAttached())
                {
                    resizeFlowPanel.getElement().getParentElement().getStyle().setHeight(100.0, Style.Unit.PCT);
                    resizeFlowPanel.getElement().getParentElement().getStyle().setWidth(100.0, Style.Unit.PCT);
                }
            }
        });

        resizeFlowPanel.add(panelPresenter);
        previewContainer.add(resizeFlowPanel);

        layer = new Layer();

        panel.add(layer);

        wiresManager = newWiresManager(layer);

        applyGrid(panel);

        addMediators(layer);

        RootPanel.get().add(v);

        createPreview();

    }

    private void createPreview() {

        previewPanel = new PreviewPanel(PREVIEWL_WIDTH,
                                        BoundsProviderFactory.computeHeight(ASPECT_RATIO, PREVIEWL_WIDTH));
        previewPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        previewPanel.getElement().getStyle().setBorderColor("#000000");
        previewPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        previewLayer = new Layer();
        previewPanel.add(previewLayer);
        previewWiresManager = newWiresManager(previewLayer);

        previewPanel.observe(panel);

        previewContainer.add(previewPanel);

        drawPreviewWiresThings();

    }

    private void addButtons(final Panel container) {
        final Button resizePlus = new Button("Resize +");
        resizePlus.addClickHandler(new ClickHandler()
        {
            @Override public void onClick(ClickEvent event)
            {
                resizePanel(50);
            }
        });
        container.add(resizePlus);

        final Button resizeLess = new Button("Resize -");
        resizeLess.addClickHandler(new ClickHandler()
        {
            @Override public void onClick(ClickEvent event)
            {
                resizePanel(-50);
            }
        });
        container.add(resizeLess);

        Button resetViewport = new Button("Reset viewport");
        resetViewport.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                panel.getLayer().getViewport().setTransform(new Transform());
                panel.refresh();
            }
        });
        container.add(resetViewport);

        Button showPreview = new Button("Show preview");
        showPreview.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                createPreview();
            }
        });
        container.add(showPreview);

        Button toImageData = new Button("To image data");
        toImageData.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                toImageData();
            }
        });
        container.add(toImageData);

    }

    private Image exportedImage;

    private void toImageData2() {
        final Bounds           bounds     = new BoundsProviderFactory.WiresBoundsProvider().get(layer);
        double x = bounds.getX();
        double y = bounds.getY();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double boxx = x >= 0 ? 0 : x;
        double boxy = y >= 0 ? 0 : y;
        GWT.log("BOUNDS [" + x + ", " + y + ", " + width + ", " + height + "]");
        Transform transform = layer.getViewport().getTransform();
        Transform newTransform = new Transform();
        newTransform.translate(-boxx, -boxy);
        layer.getViewport().setTransform(newTransform);
        layer.draw();
        String data = layer.toDataURL(DataURLType.JPG);
        layer.getViewport().setTransform(transform);
        layer.draw();
        setExportedImage(data);
    }

    private void toImageData() {
        final ScratchPad scratchPad = layer.getScratchPad();
        final Bounds           bounds     = new BoundsProviderFactory.WiresBoundsProvider().get(layer);
        double x = bounds.getX();
        double y = bounds.getY();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double boxx = x >= 0 ? 0 : -x;
        double boxy = y >= 0 ? 0 : -y;
        double boxwidth = width + Math.abs(x);
        double boxheight = height  + Math.abs(y);
        GWT.log("BOUNDS [" + x + ", " + y + ", " + width + ", " + height + "]");
        GWT.log("BOX [" + boxx + ", " + boxy + ", " + boxwidth + ", " + boxheight + "]");
        scratchPad.setPixelSize((int) boxwidth, (int) boxheight);
        scratchPad.getContext().translate(x, y);
        scratchPad.getContext().setFillColor("#FFFFFF");
        scratchPad.getContext().fillRect(boxx,
                                         boxy,
                                         boxwidth,
                                         boxheight);
        layer.drawWithTransforms(scratchPad.getContext(),
                                 1,
                                 new BoundingBox(boxx,
                                                 boxy,
                                                 boxwidth,
                                                 boxheight));
        final String data = scratchPad.toDataURL(DataURLType.JPG,
                                                 1);
        scratchPad.clear();
      setExportedImage(data);
    }

    private void setExportedImage(String data) {
        if (null != exportedImage) {
            exportedImage.removeFromParent();
        }
        exportedImage = new Image(data);
        RootPanel.get().add(exportedImage);
    }

    private void resizePanel(int factor) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        panel.updateSize(width  + factor,
                         height + factor);
    }

    private void applyGrid( final LienzoPanel panel) {

        // Grid.
        Line line1 = new Line( 0, 0, 0, 0 )
                .setStrokeColor( "#0000FF" )
                .setAlpha( 0.2 );
        Line line2 = new Line( 0, 0, 0, 0 )
                .setStrokeColor( "#00FF00"  )
                .setAlpha( 0.2 );

        line2.setDashArray( 2,
                            2 );

        GridLayer gridLayer = new GridLayer(100, line1, 25, line2 );

        panel.setBackgroundLayer( gridLayer );
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
        mediators.push(new MouseWheelZoomMediator(zommFilters).setScaleAboutPoint(false));
        mediators.push(new MousePanMediator(panFilters));
    }

    private void draw()
    {
        drawBounds();
        if (IS_WIRES)
        {
            drawWiresThings();
            // drawPreviewWiresThings();
        }
        else
        {
            drawThings();
        }
        layer.draw();
        if (null != previewLayer) {
            previewLayer.draw();
        }
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

    private static final double RED_SIZE = 100;
    private static final double BLUE_SIZE = 100;
    private static final double RED_X = 500;
    private static final double RED_Y = 500;
    private static final double BLUE_X = 50;
    private static final double BLUE_Y = 50;

    private void drawWiresThings()
    {

        MultiPath redPath = new MultiPath().rect(0, 0, RED_SIZE, RED_SIZE)
                                           .setFillColor("#FF0000");
        redShape = new WiresShape(redPath);
        wiresManager.register(redShape);
        redShape.setLocation(new Point2D(RED_X, RED_Y));
        redShape.setDraggable(true).getContainer().setUserData("red");
        wiresManager.getMagnetManager().createMagnets( redShape );
        TestsUtils.addResizeHandlers(redShape);

        MultiPath bluePath = new MultiPath().rect(0, 0, BLUE_SIZE, BLUE_SIZE)
                                            .setFillColor("#0000FF");
        blueShape = new WiresShape(bluePath);
        wiresManager.register(blueShape);
        blueShape.setLocation(new Point2D(BLUE_X, BLUE_Y));
        blueShape.setDraggable(true).getContainer().setUserData("blue");
        wiresManager.getMagnetManager().createMagnets( blueShape );
        TestsUtils.addResizeHandlers(blueShape);

        TestsUtils.connect(blueShape.getMagnets(), 3, redShape.getMagnets(), 7, wiresManager);

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
                        // GWT.log("Moving RED TO [" + location + "]");
                        previewRedShape.setLocation(location.copy());
                    }

                    if (shape.getGroup().getUserData().equals("blue"))
                    {
                        // GWT.log("Moving BLUE TO [" + location + "]");
                        previewBlueShape.setLocation(location.copy());
                    }
                }

                // previewPanel.refresh();

                return true;
            }
        });

        MultiPath redPath = new MultiPath().rect(0, 0, RED_SIZE, RED_SIZE)
                                           .setFillColor("#FF0000");
        previewRedShape = new WiresShape(redPath);
        previewWiresManager.register(previewRedShape);
        previewRedShape.setLocation(new Point2D(RED_X, RED_Y));
        previewRedShape.setDraggable(true).getContainer().setUserData("red");
        previewWiresManager.getMagnetManager().createMagnets( previewRedShape );

        MultiPath bluePath = new MultiPath().rect(0, 0, BLUE_SIZE, BLUE_SIZE)
                                            .setFillColor("#0000FF");
        previewBlueShape = new WiresShape(bluePath);
        previewWiresManager.register(previewBlueShape);
        previewBlueShape.setLocation(new Point2D(BLUE_X, BLUE_Y));
        previewBlueShape.setDraggable(true).getContainer().setUserData("blue");
        previewWiresManager.getMagnetManager().createMagnets( previewBlueShape );

        TestsUtils.connect(previewBlueShape.getMagnets(), 3, previewRedShape.getMagnets(), 7, previewWiresManager);

        previewPanel.refresh();
        previewLayer.draw();
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
