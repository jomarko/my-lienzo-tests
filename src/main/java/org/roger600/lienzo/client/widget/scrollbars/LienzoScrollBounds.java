/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.roger600.lienzo.client.widget.scrollbars;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.tooling.common.api.java.util.function.BiPredicate;
import com.ait.tooling.common.api.java.util.function.Function;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.roger600.lienzo.client.widget.Bounds;
import org.roger600.lienzo.client.widget.InfiniteLienzoLayer;

import java.util.LinkedList;
import java.util.List;

class LienzoScrollBounds
{
    private static final Double DEFAULT_VALUE = 0D;

    private final LienzoScrollHandler gridLienzoScrollHandler;

    private       Bounds              defaultBounds;

    LienzoScrollBounds(final LienzoScrollHandler GridLienzoScrollHandler)
    {
        this.gridLienzoScrollHandler = GridLienzoScrollHandler;
    }

    // TODO: Iterate always over all children??
    private List<Double> getBounds(Function<IPrimitive, Double> function)
    {
        List<Double>                  result = new LinkedList<>();
        NFastArrayList<IPrimitive<?>> shapes = getShapes();
        if (null != shapes)
        {
            for (IPrimitive<?> shape : shapes)
            {
                result.add(function.apply(shape));
            }
        }
        return result;
    }

    Double maxBoundX()
    {

        final List<Double> boundsValues = getBounds(new Function<IPrimitive, Double>()
        {
            @Override
            public Double apply(IPrimitive shape)
            {
                return shape.getX() + shape.getBoundingBox().getWidth();
            }
        });

        addExtraBounds(boundsValues,
                       new Function<Bounds, Double>()
                       {
                           @Override
                           public Double apply(Bounds bounds)
                           {
                               return bounds.getX() + bounds.getWidth();
                           }
                       });

        return maxValue(boundsValues);
    }

    Double maxBoundY()
    {

        final List<Double> boundsValues = getBounds(new Function<IPrimitive, Double>()
        {
            @Override
            public Double apply(IPrimitive shape)
            {
                return shape.getY() + shape.getBoundingBox().getHeight();
            }
        });

        addExtraBounds(boundsValues,
                       new Function<Bounds, Double>()
                       {
                           @Override
                           public Double apply(Bounds bounds)
                           {
                               return bounds.getY() + bounds.getHeight();
                           }
                       });

        return maxValue(boundsValues);
    }

    Double minBoundX()
    {

        final List<Double> boundsValues = getBounds(new Function<IPrimitive, Double>()
        {
            @Override
            public Double apply(IPrimitive shape)
            {
                return shape.getX();
            }
        });

        addExtraBounds(boundsValues,
                       new Function<Bounds, Double>()
                       {
                           @Override
                           public Double apply(Bounds bounds)
                           {
                               return bounds.getX();
                           }
                       });

        return minValue(boundsValues);
    }

    Double minBoundY()
    {

        final List<Double> boundsValues = getBounds(new Function<IPrimitive, Double>()
        {
            @Override
            public Double apply(IPrimitive shape)
            {
                return shape.getY();
            }
        });

        addExtraBounds(boundsValues,
                       new Function<Bounds, Double>()
                       {
                           @Override
                           public Double apply(Bounds bounds)
                           {
                               return bounds.getY();
                           }
                       });

        return minValue(boundsValues);
    }

    private void addExtraBounds(final List<Double> bounds,
                                final Function<Bounds, Double> function)
    {
        if (hasVisibleBounds())
        {
            bounds.add(function.apply(getVisibleBounds()));
        }

        if (hasDefaultBounds())
        {
            bounds.add(function.apply(getDefaultBounds()));
        }
    }

    Bounds getVisibleBounds()
    {
        return getLayer().getVisibleBounds();
    }

    Boolean hasDefaultBounds()
    {
        return null != getDefaultBounds();
    }

    Boolean hasVisibleBounds()
    {
        final Viewport viewport = getLayer().getViewport();
        return null != viewport;
    }

    Bounds getDefaultBounds()
    {
        return defaultBounds;
    }

    void setDefaultBounds(final Bounds defaultBounds)
    {
        this.defaultBounds = defaultBounds;
    }

    InfiniteLienzoLayer getLayer()
    {
        return gridLienzoScrollHandler.getLayer();
    }

    private NFastArrayList<IPrimitive<?>> getShapes()
    {
        return getLayer().getLayer().getChildNodes();
    }

    private static double obtainValue(final List<Double> boundsValues,
                                      final BiPredicate<Double, Double> predicate)
    {
        double result = DEFAULT_VALUE;
        for (Double value : boundsValues)
        {
            if (predicate.test(value, result))
            {
                result = value;
            }
        }
        return result;
    }

    private static double maxValue(final List<Double> boundsValues)
    {
        return obtainValue(boundsValues,
                           new BiPredicate<Double, Double>()
                           {
                               @Override
                               public boolean test(Double value, Double result)
                               {
                                   return value > result;
                               }
                           });
    }

    private static double minValue(final List<Double> boundsValues)
    {
        return obtainValue(boundsValues,
                           new BiPredicate<Double, Double>()
                           {
                               @Override
                               public boolean test(Double value, Double result)
                               {
                                   return value < result;
                               }
                           });
    }
}
