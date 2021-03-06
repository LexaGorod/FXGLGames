package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.control.ProjectileControl;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleControl extends Control {
    @Override
    public void onUpdate(Entity entity, double tpf) {
        ProjectileControl control = entity.getControl(ProjectileControl.class);
        control.setSpeed(control.getSpeed() * (1 - 3*tpf));
    }
}
