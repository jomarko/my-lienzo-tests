package org.roger600.lienzo.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabPanel implements IsWidget {

    private HorizontalPanel  mainPanel = new HorizontalPanel ();
    private VerticalPanel buttonsPanel = new VerticalPanel();
    private FlowPanel canvasPanel = new FlowPanel();
    private Map<String, IsWidget> panels = new HashMap<>();

    public TabPanel() {
        mainPanel.add(buttonsPanel);
        mainPanel.add(canvasPanel);
    }

    public void add(IsWidget widget, final String text) {
        Button button = new Button(text);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                showPanel(text);
            }
        });
        panels.put(text, widget);
        buttonsPanel.add(button);
        canvasPanel.add(widget);
        widget.asWidget().getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    private void showPanel(String txt) {
        for (IsWidget widget : panels.values()) {
            widget.asWidget().getElement().getStyle().setDisplay(Style.Display.NONE);
        }
        IsWidget widget = panels.get(txt);
        widget.asWidget().getElement().getStyle().setDisplay(Style.Display.BLOCK);
    }

    @Override
    public Widget asWidget() {
        return mainPanel;
    }
}
