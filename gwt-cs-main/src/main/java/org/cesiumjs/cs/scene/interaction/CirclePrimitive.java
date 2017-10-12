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

package org.cesiumjs.cs.scene.interaction;

import org.cesiumjs.cs.core.Cartesian3;
import org.cesiumjs.cs.core.DeveloperError;
import org.cesiumjs.cs.core.Ellipsoid;
import org.cesiumjs.cs.core.geometry.CircleGeometry;
import org.cesiumjs.cs.core.geometry.Geometry;
import org.cesiumjs.cs.core.geometry.options.CircleGeometryOptions;
import org.cesiumjs.cs.js.JsObject;
import org.cesiumjs.cs.scene.Material;
import org.cesiumjs.cs.scene.apperances.EllipsoidSurfaceAppearance;
import org.cesiumjs.cs.scene.interaction.options.CirclePrimitiveOptions;

/**
 * @author Serge Silaev aka iSergio <s.serge.b@gmail.com>
 */
public class CirclePrimitive extends AbstractPrimitive {
    public CirclePrimitive(CirclePrimitiveOptions options) {
        super(options);
        super.options = options;

        if (options.center == null && options.radius == 0) {
            throw new DeveloperError("Center and radius is required option and must be defined.");
        }

        ellipsoid = Ellipsoid.WGS84();
        show = true;
        debugShowBoundingVolume = false;

        appearance = EllipsoidSurfaceAppearance.create(false);
//        material = Material.fromType(Material.ColorType());
//        JsObject.setProperty(material.uniforms, "color",  options.color);
        granularity = Math.PI / 180.0;

        setRadius(options.radius);
        setCenter(options.center);

        getGeometry = new GetGeometry() {
            @Override
            public Geometry function() {
                if (getCenter() == null && getRadius() == 0) {
                    return null;
                }
                CircleGeometryOptions options = new CircleGeometryOptions();
                options.center = getCenter();
                options.radius = getRadius();
                options.vertexFormat = EllipsoidSurfaceAppearance.VERTEX_FORMAT();
                options.ellipsoid = ellipsoid;
                options.granularity = granularity;
                return new CircleGeometry(options);
            }
        };

        super.initialize();
    }

    public Cartesian3 getCenter() {
        return (Cartesian3) getAttribute("center");
    }

    public void setCenter(Cartesian3 center) {
        setAttribute("center", center);
    }

    public void setRadius(double radius) {
        setAttribute("radius", radius);
    }

    public double getRadius() {
        return (double) getAttribute("radius");
    }
}
