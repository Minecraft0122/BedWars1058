package com.andrei1058.bedwars.arena.feature;

import org.bukkit.event.block.Action;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelfRescuePlatformTest {

    @Test
    void acceptsLeftAndRightClickActivation() {
        assertTrue(SelfRescuePlatform.isActivationAction(Action.LEFT_CLICK_AIR));
        assertTrue(SelfRescuePlatform.isActivationAction(Action.LEFT_CLICK_BLOCK));
        assertTrue(SelfRescuePlatform.isActivationAction(Action.RIGHT_CLICK_AIR));
        assertTrue(SelfRescuePlatform.isActivationAction(Action.RIGHT_CLICK_BLOCK));
        assertFalse(SelfRescuePlatform.isActivationAction(Action.PHYSICAL));
    }
}
