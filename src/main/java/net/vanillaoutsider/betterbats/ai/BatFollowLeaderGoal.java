/*
 * Better Bats - Chiroptera Enhancements
 * Copyright (C) 2026 Dasik (Rifaditya)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

// Verified against: Bat.java (26.1.2)
package net.vanillaoutsider.betterbats.ai;

import net.dasik.social.ai.goal.FollowLeaderGoal;
import net.dasik.social.api.group.GroupMember;
import net.dasik.social.api.group.strategy.GroupParameters;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.dasik.social.core.group.FlockState;
import net.dasik.social.core.group.GroupManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.vanillaoutsider.betterbats.BetterBatsFabric;

/**
 * Custom FollowLeaderGoal for Bats that supports dynamic parameter tuning via GameRules.
 */
public class BatFollowLeaderGoal extends FollowLeaderGoal<Bat> {
    private int cachedAlignment = -1;
    private int cachedCohesion = -1;
    private int cachedSeparation = -1;
    
    public BatFollowLeaderGoal(Bat mob) {
        // Start with default aerial parameters
        super(mob, GroupParameters.DEFAULT_AERIAL, 16.0);
    }

    @Override
    public boolean canUse() {
        if (this.mob == null) {
            return false;
        }
        if (this.ticksSinceManagerCheck++ > 30 + this.mob.getRandom().nextInt(20)) {
            this.ticksSinceManagerCheck = 0;
            double receiveRange = this.mob.getAttributes().hasAttribute(Attributes.WAYPOINT_RECEIVE_RANGE)
                ? this.mob.getAttributeValue(Attributes.WAYPOINT_RECEIVE_RANGE)
                : this.searchRadius;
            GroupManager.findAndSetLeader(this.mob, receiveRange);
        }
        Bat leader = (Bat) ((GroupMember) this.mob).getLeader();
        return this.isValidLeader(leader);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob == null) {
            return false;
        }
        Bat leader = (Bat) ((GroupMember) this.mob).getLeader();
        return this.isValidLeader(leader);
    }

    @Override
    public void tick() {
        if (this.mob == null) {
            return;
        }

        // Periodically sync parameters with GameRules (every 20 ticks to avoid overhead)
        if (this.mob.tickCount % 20 == 0 && !this.mob.level().isClientSide()) {
            this.syncParameters();
        }

        Mob leader = (Mob) ((GroupMember) this.mob).getLeader();
        if (leader == null) {
            return;
        }

        // Staggered calculation of flock state (every 20 ticks on the server side)
        if (this.mob.tickCount % 20 == 0 && !this.mob.level().isClientSide()) {
            GroupMember leaderGM = (GroupMember) leader;
            FlockState state = leaderGM.getFlockState();
            if (state == null || this.mob.level().getGameTime() - state.getLastUpdateTime() > 20) {
                double transmitRange = this.searchRadius * 3.0;
                if (leader.getAttributes().hasAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE)) {
                    transmitRange = leader.getAttributeValue(Attributes.WAYPOINT_TRANSMIT_RANGE);
                }
                GroupManager.computeFlockState(leader, transmitRange);
            }
        }

        // Execute the flocking strategy every tick for smooth flying boids behavior
        this.defaultStrategy.execute(this.mob, leader, this.parameters);
        BatFlightHelper.applyFlightForces(this.mob);
    }

    private void syncParameters() {
        int alignment = DynamicGameRuleManager.getInt(this.mob.level(), BetterBatsFabric.BAT_ALIGNMENT);
        int cohesion = DynamicGameRuleManager.getInt(this.mob.level(), BetterBatsFabric.BAT_COHESION);
        int separation = DynamicGameRuleManager.getInt(this.mob.level(), BetterBatsFabric.BAT_SEPARATION);

        if (alignment != this.cachedAlignment || cohesion != this.cachedCohesion || separation != this.cachedSeparation) {
            this.cachedAlignment = alignment;
            this.cachedCohesion = cohesion;
            this.cachedSeparation = separation;

            // Update the goal's parameters with new Boids weights
            // Formula: RuleValue * 0.01f (e.g., 5 -> 0.05f)
            // teleportDistance increased from 144.0f (12 blocks) to 1024.0f (32 blocks) to allow smooth flight
            this.setParameters(new GroupParameters(
                3.0f,   // cohesionRadius
                1.0f,   // separationRadius
                0.4f,   // maxSpeed
                true,   // canTeleport
                1024.0f,// teleportDistance
                6.0f,   // startDistance
                2.0f,   // stopDistance
                alignment * 0.01f,
                cohesion * 0.01f,
                separation * 0.01f
            ));
        }
    }
}

