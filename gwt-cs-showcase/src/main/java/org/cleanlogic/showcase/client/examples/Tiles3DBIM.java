/*
 * Copyright 2017 iserge.
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
import com.google.gwt.user.client.ui.HTML;
import org.cesiumjs.cs.core.BoundingSphere;
import org.cesiumjs.cs.core.Matrix4;
import org.cesiumjs.cs.promise.Fulfill;
import org.cesiumjs.cs.scene.Cesium3DTileset;
import org.cesiumjs.cs.widgets.ViewerPanel;
import org.cleanlogic.showcase.client.basic.AbstractExample;
import org.cleanlogic.showcase.client.components.store.ShowcaseExampleStore;

import javax.inject.Inject;

/**
 * @author Serge Silaev aka iSergio <s.serge.b@gmail.com>
 */
public class Tiles3DBIM extends AbstractExample {

    @Inject
    public Tiles3DBIM(ShowcaseExampleStore store) {
        super("3D Tiles BIM", "A sample BIM dataset rendered with 3D Tiles.", new String[]{"Showcase", "3D Tiles"}, store);
    }

    @Override
    public void buildPanel() {
        final ViewerPanel csVPanel = new ViewerPanel();

        Cesium3DTileset tileset = (Cesium3DTileset) csVPanel.getViewer().scene().primitives().add(Cesium3DTileset.create("https://beta.cesium.com/api/assets/1459?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIzNjUyM2I5Yy01YmRhLTQ0MjktOGI0Zi02MDdmYzBjMmY0MjYiLCJpZCI6NDQsImFzc2V0cyI6WzE0NTldLCJpYXQiOjE0OTkyNjQ3ODF9.SW_rwY-ic0TwQBeiweXNqFyywoxnnUBtcVjeCmDGef4"));

        tileset.readyPromise().then(new Fulfill<Cesium3DTileset>() {
            @Override
            public void onFulfilled(Cesium3DTileset value) {
                BoundingSphere boundingSphere = value.boundingSphere();
                csVPanel.getViewer().camera.viewBoundingSphere(boundingSphere, new org.cesiumjs.cs.core.HeadingPitchRange(0.5, -0.2, boundingSphere.radius * 4.0));
                csVPanel.getViewer().camera.lookAtTransform(Matrix4.IDENTITY());
            }
        });

        contentPanel.add(new HTML("<p>A sample BIM dataset rendered with 3D Tiles.</p>"));
        contentPanel.add(csVPanel);

        initWidget(contentPanel);
    }

    @Override
    public String[] getSourceCodeURLs() {
        String[] sourceCodeURLs = new String[1];
        sourceCodeURLs[0] = GWT.getModuleBaseURL() + "examples/" + "Tiles3DBIM.txt";
        return sourceCodeURLs;
    }
}