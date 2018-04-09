package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;

public class LanesTests implements MyLienzoTest,
                                   HasButtons {

    private static final String LABEL_CONTAINER = "container";
    private static final String LABEL_ACTIVITY = "activity";
    private static final double WIDTH = 850;
    private static final double HEIGHT = 150;

    private WiresShape lane1;
    private WiresShape lane2;
    private WiresShape task1;
    private WiresShape task2;
    private WiresManager wires_manager;

    public void test(final Layer layer) {

        wires_manager = WiresManager.get(layer);

        wires_manager.setDockingAcceptor(IDockingAcceptor.NONE);
        wires_manager.setContainmentAcceptor(new IContainmentAcceptor() {
            @Override
            public boolean containmentAllowed(WiresContainer parent, WiresShape[] children) {
                return acceptContainment(parent, children);
            }

            @Override
            public boolean acceptContainment(WiresContainer parent, WiresShape[] children) {
                boolean isLayer = parent == null || parent instanceof WiresLayer;
                if (isLayer) {
                    return true;
                }
                boolean isParentContainer = parent.getGroup().getUserData().equals(LABEL_CONTAINER);
                if (isParentContainer) {
                    for (WiresShape child : children) {
                        boolean isActivity = child.getGroup().getUserData().equals(LABEL_ACTIVITY);
                        if (!isActivity) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        wires_manager.setLocationAcceptor(new ILocationAcceptor() {
            @Override
            public boolean allow(WiresContainer[] shapes, Point2D[] locations) {
                return true;
            }

            @Override
            public boolean accept(WiresContainer[] shapes, Point2D[] locations) {
                return true;
            }

            @Override
            public boolean sibling(WiresContainer parent,
                                   WiresContainer shape) {
                Object pdata = parent.getGroup().getUserData();
                Object sdata = shape.getGroup().getUserData();
                return pdata.equals(LABEL_CONTAINER) && sdata.equals(LABEL_CONTAINER);
            }
        });

        final double x = 50;
        final double y = 250;

        lane1 = create(x,
                       y,
                       WIDTH,
                       HEIGHT,
                       ColorName.BLACK.getColorString(),
                       LABEL_CONTAINER);

        lane2 = create(x,
                       y + HEIGHT + 50,
                       WIDTH,
                       HEIGHT,
                       ColorName.RED.getColorString(),
                       LABEL_CONTAINER);

        task1 = create(x + WIDTH + 100,
                       y,
                       50,
                       50,
                       ColorName.BLACK.getColorString(),
                       LABEL_ACTIVITY);
        task1.getPath()
                .setFillColor(ColorName.GREY)
                .setFillAlpha(1);

        task2 = create(x + WIDTH + 100,
                       y + 150,
                       50,
                       50,
                       ColorName.BLUE.getColorString(),
                       LABEL_ACTIVITY);
        task2.getPath()
                .setFillColor(ColorName.GREY)
                .setFillAlpha(1);
    }

    private void addResizeHandlers(final WiresShape shape) {
        shape
                .setResizable(true)
                .getGroup()
                .addNodeMouseClickHandler(new NodeMouseClickHandler() {
                    @Override
                    public void onNodeMouseClick(NodeMouseClickEvent event) {
                        final IControlHandleList controlHandles = shape.loadControls(IControlHandle.ControlHandleStandardType.RESIZE);
                        if (null != controlHandles) {
                            if (event.isShiftKeyDown()) {
                                controlHandles.show();
                            } else {
                                controlHandles.hide();
                            }
                        }
                    }
                });

        shape.addWiresResizeStartHandler(new WiresResizeStartHandler() {
            @Override
            public void onShapeResizeStart(final WiresResizeStartEvent event) {
                onShapeResize(event.getWidth(), event.getHeight());
            }
        });

        shape.addWiresResizeStepHandler(new WiresResizeStepHandler() {
            @Override
            public void onShapeResizeStep(final WiresResizeStepEvent event) {
                onShapeResize(event.getWidth(), event.getHeight());
            }
        });

        shape.addWiresResizeEndHandler(new WiresResizeEndHandler() {
            @Override
            public void onShapeResizeEnd(final WiresResizeEndEvent event) {
                onShapeResize(event.getWidth(), event.getHeight());
            }
        });
    }

    private void onShapeResize(final double width, final double height) {
        final String s = "[" + width + ", " + height + "]";
        GWT.log("Resizing to " + s);
    }

    private WiresShape create(double x,
                              double y,
                              final double width,
                              final double height,
                              String color,
                              String label) {
        final MultiPath path = new MultiPath().rect(0, 0, width, height)
                .setStrokeWidth(1)
                .setStrokeColor(color);
        final WiresShape wiresShape0 = new WiresShape(path).setDraggable(true);
        wiresShape0.setLocation(new Point2D(x, y));
        wiresShape0.getGroup().setUserData(label);

        wires_manager.register(wiresShape0);
        wires_manager.getMagnetManager().createMagnets(wiresShape0);
        addResizeHandlers(wiresShape0);
        return wiresShape0;
    }

    @Override
    public void setButtonsPanel(Panel panel) {
        Button loc1 = new Button("Log location - Lane 1 (BLACK)");
        loc1.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                GWT.log("LANE1 = " + lane1.getLocation());
            }
        });
        panel.add(loc1);
        Button loc2 = new Button("Log location - Lane 2 (RED)");
        loc2.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                GWT.log("LANE2 = " + lane2.getLocation());
            }
        });
        panel.add(loc2);
    }
}
