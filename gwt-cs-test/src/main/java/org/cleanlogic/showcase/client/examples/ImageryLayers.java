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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.cesiumjs.cs.Cesium;
import org.cesiumjs.cs.Configuration;
import org.cesiumjs.cs.core.Credit;
import org.cesiumjs.cs.core.Rectangle;
import org.cesiumjs.cs.core.providers.options.ArcGisImageServerTerrainProviderOptions;
import org.cesiumjs.cs.scene.ImageryLayer;
import org.cesiumjs.cs.scene.ImageryLayerCollection;
import org.cesiumjs.cs.scene.providers.ArcGisMapServerImageryProvider;
import org.cesiumjs.cs.scene.providers.SingleTileImageryProvider;
import org.cesiumjs.cs.scene.providers.options.ArcGisMapServerImageryProviderOptions;
import org.cesiumjs.cs.scene.providers.options.SingleTileImageryProviderOptions;
import org.cesiumjs.cs.scene.providers.options.TileMapServiceImageryProviderOptions;
import org.cesiumjs.cs.widgets.Viewer;
import org.cesiumjs.cs.widgets.ViewerPanelAbstract;
import org.cesiumjs.cs.widgets.options.ViewerOptions;
import org.cleanlogic.showcase.client.basic.AbstractExample;
import org.cleanlogic.showcase.client.components.store.ShowcaseExampleStore;

import javax.inject.Inject;

/**
 * @author Serge Silaev aka iSergio <s.serge.b@gmail.com>
 */
public class ImageryLayers extends AbstractExample {
    private class ViewerPanel implements IsWidget {
        private ViewerPanelAbstract _csPanelAbstract;

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
                        ViewerOptions viewerOptions = new ViewerOptions();
                        ArcGisMapServerImageryProviderOptions arcGisMapServerImageryProviderOptions = new ArcGisMapServerImageryProviderOptions();
                        arcGisMapServerImageryProviderOptions.url = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer";
                        viewerOptions.imageryProvider = new ArcGisMapServerImageryProvider(arcGisMapServerImageryProviderOptions);
                        viewerOptions.baseLayerPicker = false;
                        _viewer = new Viewer(element, viewerOptions);

                        ImageryLayerCollection layers = _viewer.imageryLayers();
                        TileMapServiceImageryProviderOptions tileMapServiceImageryProviderOptions = new TileMapServiceImageryProviderOptions();
                        tileMapServiceImageryProviderOptions.url = "https://cesiumjs.org/blackmarble";
                        tileMapServiceImageryProviderOptions.credit = new Credit("Black Marble imagery courtesy NASA Earth Observatory");
                        tileMapServiceImageryProviderOptions.flipXY = true;
                        ImageryLayer blackMarble = layers.addImageryProvider(Cesium.createTileMapServiceImageryProvider(tileMapServiceImageryProviderOptions));
                        blackMarble.alpha = 0.5f;
                        blackMarble.brightness = 2.0f;

                        SingleTileImageryProviderOptions singleTileImageryProviderOptions = new SingleTileImageryProviderOptions();
                        singleTileImageryProviderOptions.url = GWT.getModuleBaseURL() + "images/Cesium_Logo_overlay.png";
                        singleTileImageryProviderOptions.rectangle = Rectangle.fromDegrees(-75.0, 28.0, -67.0, 29.75);
                        layers.addImageryProvider(new SingleTileImageryProvider(singleTileImageryProviderOptions));
                        return _viewer;
                    }
                };
            }
            return _csPanelAbstract;
        }
    }

    @Inject
    public ImageryLayers(ShowcaseExampleStore store) {
        super("Imagery Layers", "Create imagery layers from multiple sources", new String[]{"Showcase", "Cesium", "3d", "Viewer"}, store);
    }

    @Override
    public void buildPanel() {
        ViewerPanel csVPanel = new ViewerPanel();

        contentPanel.add(new HTML("<p>Create imagery layers from multiple sources.</p>"));
        contentPanel.add(csVPanel);

        initWidget(contentPanel);
    }

    @Override
    public String[] getSourceCodeURLs() {
        String[] sourceCodeURLs = new String[1];
        sourceCodeURLs[0] = GWT.getModuleBaseURL() + "examples/" + "ImageryLayers.txt";
        return sourceCodeURLs;
    }
}