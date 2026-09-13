// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Mob.java (26.1.2)
package net.vanillaoutsider.betterbats.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor("goalSelector")
    GoalSelector getGoalSelector();
}
