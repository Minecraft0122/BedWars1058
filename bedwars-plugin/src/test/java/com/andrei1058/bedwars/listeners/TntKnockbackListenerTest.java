package com.andrei1058.bedwars.listeners;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TntKnockbackListenerTest {

    @Test
    void scalesEveryComponentOfTheExplosionKnockback() {
        Vector raw = new Vector(1.0, 0.5, -0.25);

        Vector scaled = TntKnockbackListener.scaleKnockback(raw, 0.6);

        assertEquals(0.6, scaled.getX(), 1.0E-9);
        assertEquals(0.3, scaled.getY(), 1.0E-9);
        assertEquals(-0.15, scaled.getZ(), 1.0E-9);
        assertEquals(new Vector(1.0, 0.5, -0.25), raw, "raw knockback must not be mutated");
    }

    @Test
    void multiplierOfOneKeepsVanillaKnockback() {
        Vector raw = new Vector(0.8, 0.5, 0.25);

        assertEquals(raw, TntKnockbackListener.scaleKnockback(raw, 1.0));
    }

    @Test
    void zeroMultiplierRemovesTheExplosionKnockback() {
        assertEquals(new Vector(0, 0, 0),
                TntKnockbackListener.scaleKnockback(new Vector(1.0, 0.5, 0), 0));
    }

    @Test
    void boundsInvalidMultipliers() {
        assertEquals(1.0, TntKnockbackListener.normalizeMultiplier(Double.NaN));
        assertEquals(1.0, TntKnockbackListener.normalizeMultiplier(Double.POSITIVE_INFINITY));
        assertEquals(0.0, TntKnockbackListener.normalizeMultiplier(-2));
        assertEquals(3.0, TntKnockbackListener.normalizeMultiplier(50));
        assertEquals(0.6, TntKnockbackListener.normalizeMultiplier(0.6));
    }
}
