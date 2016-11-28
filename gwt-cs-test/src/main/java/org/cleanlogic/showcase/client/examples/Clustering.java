/*
 * Copyright 2016 iserge.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cleanlogic.showcase.client.examples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import org.cesiumjs.cs.Cesium;
import org.cesiumjs.cs.Configuration;
import org.cesiumjs.cs.core.Color;
import org.cesiumjs.cs.core.Event;
import org.cesiumjs.cs.core.PinBuilder;
import org.cesiumjs.cs.datasources.Entity;
import org.cesiumjs.cs.datasources.EntityCluster;
import org.cesiumjs.cs.datasources.KmlDataSource;
import org.cesiumjs.cs.datasources.options.KmlDataSourceOptions;
import org.cesiumjs.cs.js.JsObject;
import org.cesiumjs.cs.promise.Fulfill;
import org.cesiumjs.cs.promise.Promise;
import org.cesiumjs.cs.scene.VerticalOrigin;
import org.cesiumjs.cs.widgets.Viewer;
import org.cesiumjs.cs.widgets.ViewerPanelAbstract;
import org.cleanlogic.showcase.client.basic.AbstractExample;
import org.cleanlogic.showcase.client.components.store.ShowcaseExampleStore;
import org.cleanlogic.showcase.client.examples.slider.Slider;
import org.cleanlogic.showcase.client.examples.slider.SliderEvent;
import org.cleanlogic.showcase.client.examples.slider.SliderListener;

import javax.inject.Inject;

/**
 * @author Serge Silaev aka iSergio <s.serge.b@gmail.com>
 */
public class Clustering extends AbstractExample {
    private ViewerPanel csVPanel;
    private Slider pixelRangeSlider;
    private TextBox pixelRangeTBox;
    private Slider minimumClusterSizeSlider;
    private TextBox minimumClusterSizeTBox;

    private class ViewerPanel implements IsWidget {
        private ViewerPanelAbstract _csPanelAbstract;
        private Event.RemoveCallback removeListener;
        private KmlDataSource _dataSource;

        private String pin50;
        private String pin40;
        private String pin30;
        private String pin20;
        private String pin10;
        private String[] singleDigitPins;
        private ViewerPanel() {
            super();
            asWidget();
        }

        @Override
        public Widget asWidget() {
            if (_csPanelAbstract == null) {
                final Configuration csConfiguration = new Configuration();
                csConfiguration.setPath(GWT.getModuleBaseURL() + "JavaScript/Cesium");
                _csPanelAbstract = new ViewerPanelAbstract(csConfiguration) {
                    @Override
                    public Viewer createViewer(Element element) {
                        _viewer = new Viewer(element);
                        KmlDataSourceOptions kmlDataSourceOptions = new KmlDataSourceOptions(_viewer.camera, _viewer.canvas());
                        Promise<KmlDataSource, Void> dataSourcePromise = _viewer.dataSources().add(KmlDataSource.load(GWT.getModuleBaseURL() + "SampleData/kml/facilities/facilities.kml", kmlDataSourceOptions));
                        dataSourcePromise.then(new Fulfill<KmlDataSource>() {
                            @Override
                            public void onFulfilled(KmlDataSource dataSource) {
                                int pixelRange = 25;
                                int minimumClusterSize = 3;
                                boolean enabled = true;

                                dataSource.clustering.enabled = enabled;
                                dataSource.clustering.pixelRange = pixelRange;
                                dataSource.clustering.minimumClusterSize = minimumClusterSize;

                                PinBuilder pinBuilder = new PinBuilder();
                                pin50 = pinBuilder.fromText("50+", Color.RED(), 48).toDataUrl();
                                pin40 = pinBuilder.fromText("40+", Color.ORANGE(), 48).toDataUrl();
                                pin30 = pinBuilder.fromText("30+", Color.YELLOW(), 48).toDataUrl();
                                pin20 = pinBuilder.fromText("20+", Color.GREEN(), 48).toDataUrl();
                                pin10 = pinBuilder.fromText("10+", Color.BLUE(), 48).toDataUrl();
                                singleDigitPins = new String[8];
                                for (int i = 0; i < singleDigitPins.length; ++i) {
                                    singleDigitPins[i] = pinBuilder.fromText("" + (i + 2), Color.VIOLET(), 48).toDataUrl();
                                }
                                // start with custom style
                                customStyle(dataSource);

                                _dataSource = dataSource;
                            }
                        });
                        return _viewer;
                    }
                };
            }
            return _csPanelAbstract;
        }

        public void customStyle(KmlDataSource dataSource) {
            if (Cesium.defined(removeListener)) {
                removeListener.function();
                removeListener = (Event.RemoveCallback) JsObject.undefined();
            } else {
                removeListener = dataSource.clustering.clusterEvent.addEventListener(new EntityCluster.newClusterCallback() {
                    @Override
                    public void function(Entity[] clusteredEntities, JsObject cluster) {
                        cluster.getObject("label").setBoolean("show", false);
                        cluster.getObject("billboard").setBoolean("show", true);
                        cluster.getObject("billboard").setNumber("verticalOrigin", VerticalOrigin.BOTTOM());

                        if (clusteredEntities.length >= 50) {
                            cluster.getObject("billboard").setObject("image", pin50);
                        }
                        else if (clusteredEntities.length >= 40) {
                            cluster.getObject("billboard").setObject("image", pin40);
                        }
                        else if (clusteredEntities.length >= 30) {
                            cluster.getObject("billboard").setObject("image", pin30);
                        }
                        else if (clusteredEntities.length >= 20) {
                            cluster.getObject("billboard").setObject("image", pin20);
                        }
                        else if (clusteredEntities.length >= 10) {
                            cluster.getObject("billboard").setObject("image", pin10);
                        }
                        else {
                            cluster.getObject("billboard").setObject("image", singleDigitPins[clusteredEntities.length - 2]);
                        }
                    }
                });
            }
            // force a re-cluster with the new styling
            int pixelRange = dataSource.clustering.pixelRange;
            dataSource.clustering.pixelRange = 0;
            dataSource.clustering.pixelRange = pixelRange;
        }
    }

    @Inject
    public Clustering(ShowcaseExampleStore store) {
        super("Clustering", "Simple Cesium hello world application", new String[]{"Showcase", "Cesium", "3d", "Viewer"}, store);
    }

    @Override
    public void buildPanel() {
        csVPanel = new ViewerPanel();

        pixelRangeSlider = new Slider("pixelRange", 1, 200, 15);
        pixelRangeSlider.setStep(1);
        pixelRangeSlider.setWidth("150px");
        pixelRangeSlider.addListener(new MSliderListener());
        pixelRangeTBox = new TextBox();
        pixelRangeTBox.addChangeHandler(new MChangeHandler());
        pixelRangeTBox.setText("" + 15);
        pixelRangeTBox.setSize("30px", "12px");

        HorizontalPanel pixelRangeHPanel = new HorizontalPanel();
        pixelRangeHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        pixelRangeHPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        pixelRangeHPanel.setSpacing(10);
        pixelRangeHPanel.add(pixelRangeSlider);
        pixelRangeHPanel.add(pixelRangeTBox);

        minimumClusterSizeSlider = new Slider("minimumClusterSize", 1, 20, 3);
        minimumClusterSizeSlider.setStep(1);
        minimumClusterSizeSlider.setWidth("150px");
        minimumClusterSizeSlider.addListener(new MSliderListener());
        minimumClusterSizeTBox = new TextBox();
        pixelRangeTBox.addChangeHandler(new MChangeHandler());
        minimumClusterSizeTBox.setText("" + 3);
        minimumClusterSizeTBox.setSize("30px", "12px");

        HorizontalPanel minimumClusterSizeHPanel = new HorizontalPanel();
        minimumClusterSizeHPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        minimumClusterSizeHPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        minimumClusterSizeHPanel.setSpacing(10);
        minimumClusterSizeHPanel.add(minimumClusterSizeSlider);
        minimumClusterSizeHPanel.add(minimumClusterSizeTBox);

        CheckBox enabledCBox = new CheckBox();
        enabledCBox.setValue(true);
        enabledCBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                csVPanel._dataSource.clustering.enabled = valueChangeEvent.getValue();
            }
        });

        CheckBox customStyleCBox = new CheckBox();
        customStyleCBox.setValue(true);
        customStyleCBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                csVPanel.customStyle(csVPanel._dataSource);
            }
        });

        FlexTable flexTable = new FlexTable();
        flexTable.setHTML(1, 0, "<font color=\"white\">Pixel Range</font>");
        flexTable.setWidget(1, 1, pixelRangeHPanel);
        flexTable.setHTML(2, 0, "<font color=\"white\">Minimum Cluster Size</font>");
        flexTable.setWidget(2, 1, minimumClusterSizeHPanel);
        flexTable.setHTML(3, 0, "<font color=\"white\">Enabled</font>");
        flexTable.setWidget(3, 1, enabledCBox);
        flexTable.setHTML(4, 0, "<font color=\"white\">Custom Style</font>");
        flexTable.setWidget(4, 1, customStyleCBox);

        AbsolutePanel aPanel = new AbsolutePanel();
        aPanel.add(csVPanel);
        aPanel.add(flexTable, 20, 20);


        contentPanel.add(new HTML("<p>This example shows simple Cesium application</p>"));
        contentPanel.add(aPanel);

        initWidget(contentPanel);
    }

    @Override
    public String[] getSourceCodeURLs() {
        String[] sourceCodeURLs = new String[1];
        sourceCodeURLs[0] = GWT.getModuleBaseURL() + "examples/" + "Clustering.txt";
        return sourceCodeURLs;
    }

    private class MSliderListener implements SliderListener {

        @Override
        public void onStart(SliderEvent e) {

        }

        @Override
        public boolean onSlide(SliderEvent e) {
            Slider source = e.getSource();
            int value = source.getValue();
            if (source.getElement().getId().equalsIgnoreCase("pixelRange")) {
                csVPanel._dataSource.clustering.pixelRange = value;
                pixelRangeTBox.setValue("" + value);
            }
            else if (source.getElement().getId().equalsIgnoreCase("minimumClusterSize")) {
                csVPanel._dataSource.clustering.minimumClusterSize = value;
                minimumClusterSizeTBox.setValue("" + value);
            }
            return true;
        }

        @Override
        public void onChange(SliderEvent e) {

        }

        @Override
        public void onStop(SliderEvent e) {

        }
    }

    private class MChangeHandler implements ChangeHandler {

        @Override
        public void onChange(ChangeEvent changeEvent) {
            TextBox source = (TextBox) changeEvent.getSource();
            int value = Integer.parseInt(source.getText());
            if (source.equals(pixelRangeTBox)) {
                pixelRangeSlider.setValue(value);
            }
            else if (source.equals(minimumClusterSizeTBox)) {
                minimumClusterSizeSlider.setValue(value);
            }
        }
    }
}