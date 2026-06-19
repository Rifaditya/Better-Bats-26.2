// Verified against: Bat.java (26.1.2)
package net.vanillaoutsider.betterbats;

import net.minecraft.world.phys.Vec3;

/**
 * Accessor interface implemented by BatMixin to share transient panic and guano states.
 */
public interface BatStateAccessor {
    void betterbats$resetGuanoTicks();
    void betterbats$panic(Vec3 source);
    boolean betterbats$isPanicked();
    int betterbats$getPanicTicks();
    void betterbats$setPanicTicks(int ticks);
    Vec3 betterbats$getPanicSource();
    boolean betterbats$isGoalActive();
    void betterbats$setGoalActive(boolean active);
}
