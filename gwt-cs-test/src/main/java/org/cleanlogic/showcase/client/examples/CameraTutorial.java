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
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import org.cesiumjs.cs.Configuration;
import org.cesiumjs.cs.core.Cartesian3;
import org.cesiumjs.cs.core.Clock;
import org.cesiumjs.cs.core.Event;
import org.cesiumjs.cs.core.ScreenSpaceEventHandler;
import org.cesiumjs.cs.core.enums.ScreenSpaceEventType;
import org.cesiumjs.cs.widgets.Viewer;
import org.cesiumjs.cs.widgets.ViewerPanelAbstract;
import org.cleanlogic.showcase.client.basic.AbstractExample;
import org.cleanlogic.showcase.client.components.store.ShowcaseExampleStore;

import javax.inject.Inject;

/**
 * @author Serge Silaev aka iSergio <s.serge.b@gmail.com>
 */
public class CameraTutorial extends AbstractExample {
    private class ViewerPanel implements IsWidget {
        private class Flags {
            public boolean looking = false;
            public boolean moveForward = false;
            public boolean moveBackward = false;
            public boolean moveUp = false;
            public boolean moveDown = false;
            public boolean moveLeft = false;
            public boolean moveRight = false;

            public Flags() {}
        }
        private Cartesian3 mousePosition;
        private Cartesian3 startMousePosition;

        private ViewerPanelAbstract _csPanelAbstract;
        private ScreenSpaceEventHandler handler;
        Flags flags = new Flags();

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
                        _viewer.canvas().setAttribute("tabindex", "0");

                        _viewer.scene().screenSpaceCameraController().enableRotate = false;
                        _viewer.scene().screenSpaceCameraController().enableTranslate = false;
                        _viewer.scene().screenSpaceCameraController().enableZoom = false;
                        _viewer.scene().screenSpaceCameraController().enableTilt = false;
                        _viewer.scene().screenSpaceCameraController().enableLook = false;

                        handler = new ScreenSpaceEventHandler(_viewer.canvas());
                        handler.setInputAction(new ScreenSpaceEventHandler.Listener<org.cesiumjs.cs.core.events.MouseDownEvent>() {
                            @Override
                            public void function(org.cesiumjs.cs.core.events.MouseDownEvent event) {
                                flags.looking = true;
                                mousePosition = startMousePosition = new Cartesian3(event.position.x, event.position.y, 0);
                            }
                        }, ScreenSpaceEventType.LEFT_DOWN());

                        handler.setInputAction(new ScreenSpaceEventHandler.Listener<org.cesiumjs.cs.core.events.MouseMoveEvent>() {
                            @Override
                            public void function(org.cesiumjs.cs.core.events.MouseMoveEvent event) {
                                mousePosition = new Cartesian3(event.endPosition.x, event.endPosition.y, 0);
                            }
                        }, ScreenSpaceEventType.MOUSE_MOVE());

                        handler.setInputAction(new ScreenSpaceEventHandler.Listener<org.cesiumjs.cs.core.events.MouseUpEvent>() {
                            @Override
                            public void function(org.cesiumjs.cs.core.events.MouseUpEvent event) {
                                flags.looking = false;
                            }
                        }, ScreenSpaceEventType.LEFT_UP());

                        RootPanel.get().addDomHandler(new KeyDownHandler() {
                            @Override
                            public void onKeyDown(KeyDownEvent keyDownEvent) {
                                setFlag(keyDownEvent.getNativeKeyCode(), true);
                            }
                        }, KeyDownEvent.getType());

                        RootPanel.get().addDomHandler(new KeyUpHandler() {
                            @Override
                            public void onKeyUp(KeyUpEvent keyUpEvent) {
                                setFlag(keyUpEvent.getNativeKeyCode(), false);
                            }
                        }, KeyUpEvent.getType());

                        _viewer.clock().onTick.addEventListener(new Event.Listener() {
                            @Override
                            public void function(Object ...o) {
                                org.cesiumjs.cs.scene.Camera camera = _viewer.camera;
                                // First element in array is Clock. Check this.
                                if (o[0] instanceof Clock) {
                                    if (flags.looking) {
                                        int width = _viewer.canvas().getClientWidth();
                                        int height = _viewer.canvas().getClientHeight();

                                        // Coordinate (0.0, 0.0) will be where the mouse was clicked.
                                        double x = (mousePosition.x - startMousePosition.x) / width;
                                        double y = -(mousePosition.y - startMousePosition.y) / height;

                                        double lookFactor = 0.05;
                                        camera.lookRight(x * lookFactor);
                                        camera.lookUp(y * lookFactor);
                                    }

                                    // Change movement speed based on the distance of the camera to the surface of the ellipsoid.
                                    double cameraHeight = _viewer.scene().globe.ellipsoid.cartesianToCartographic(camera.position).height;
                                    double moveRate = cameraHeight / 100.0;

                                    if (flags.moveForward) {
                                        camera.moveForward(moveRate);
                                    }
                                    if (flags.moveBackward) {
                                        camera.moveBackward(moveRate);
                                    }
                                    if (flags.moveUp) {
                                        camera.moveUp(moveRate);
                                    }
                                    if (flags.moveDown) {
                                        camera.moveDown(moveRate);
                                    }
                                    if (flags.moveLeft) {
                                        camera.moveLeft(moveRate);
                                    }
                                    if (flags.moveRight) {
                                        camera.moveRight(moveRate);
                                    }
                                }
                            }
                        });

                        return _viewer;
                    }
                };

                _csPanelAbstract.addHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        _csPanelAbstract.getViewer().canvas().focus();
                    }
                }, ClickEvent.getType());
            }
            return _csPanelAbstract;
        }

        private String getFlagForKeyCode(int keyCode) {
            if ("W".codePointAt(0) == keyCode) {
                return "moveForward";
            }
            else if ("S".codePointAt(0) == keyCode) {
                return "moveBackward";
            }
            else if ("Q".codePointAt(0) == keyCode) {
                return "moveUp";
            }
            else if ("E".codePointAt(0) == keyCode) {
                return "moveDown";
            }
            else if ("D".codePointAt(0) == keyCode) {
                return "moveRight";
            }
            else if ("A".codePointAt(0) == keyCode) {
                return "moveLeft";
            } else {
                return null;
            }
        }

        private void setFlag(int keyCode, boolean flag) {
            String flagName = getFlagForKeyCode(keyCode);
            if (flagName != null) {
                if (flagName.equalsIgnoreCase("moveForward")) {
                    flags.moveForward = flag;
                }
                else if (flagName.equalsIgnoreCase("moveBackward")) {
                    flags.moveBackward = flag;
                }
                else if (flagName.equalsIgnoreCase("moveUp")) {
                    flags.moveUp = flag;
                }
                else if (flagName.equalsIgnoreCase("moveDown")) {
                    flags.moveDown = flag;
                }
                else if (flagName.equalsIgnoreCase("moveLeft")) {
                    flags.moveLeft = flag;
                }
                else if (flagName.equalsIgnoreCase("moveRight")) {
                    flags.moveRight = flag;
                }
            }
        }
    }

    @Inject
    public CameraTutorial(ShowcaseExampleStore store) {
        super("Camera Tutorial", "Camera Tutorial", new String[]{"Showcase", "Cesium", "3d", "Viewer", "Camera"}, store);
    }

    @Override
    public void buildPanel() {
        ViewerPanel csVPanel = new ViewerPanel();

        HTML html = new HTML();
        html.setHTML("<div id=\"toolbar\">\n" +
                "<div>Click on the Cesium display to start.</div>\n" +
                "<div>w/s - move forward/backward</div>\n" +
                "<div>a/d - move left/right</div>\n" +
                "<div>q/e - move up/down</div>\n" +
                "<div>left mouse button down plus mouse move changes the look direction</div>\n" +
                "</div>");
        html.getElement().getStyle().setColor("white");

        AbsolutePanel aPanel = new AbsolutePanel();
        aPanel.add(csVPanel);
        aPanel.add(html, 20, 20);

        contentPanel.add(new HTML("<p>This example shows how control camera from keyboard</p>"));
        contentPanel.add(aPanel);

        initWidget(contentPanel);
    }

    @Override
    public String[] getSourceCodeURLs() {
        String[] sourceCodeURLs = new String[1];
        sourceCodeURLs[0] = GWT.getModuleBaseURL() + "examples/" + "Camera Tutorial.txt";
        return sourceCodeURLs;
    }
}
