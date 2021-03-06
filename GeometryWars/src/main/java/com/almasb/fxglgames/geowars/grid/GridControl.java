package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GridControl extends Control {

    // TODO: calculate capacity
    private Array<Control> controls = new Array<>(2000);

    @Override
    public void onUpdate(Entity entity, double tpf) {
        for (Control control : controls)
            control.onUpdate(entity, tpf);
    }

    public void addControl(Control control) {
        controls.add(control);
        control.onAdded(getEntity());
    }
}
