package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.arena.Arena;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

import static com.andrei1058.bedwars.BedWars.config;

/**
 * Scales the vanilla explosion knockback that player TNT applies to arena
 * players. The TNT jump formula in {@link DamageDeathMove} is untouched.
 * <p>
 * A TNT explosion is resolved synchronously between {@link ExplosionPrimeEvent}
 * and {@link EntityExplodeEvent}, so the primed TNT is remembered for that
 * window and every explosion knockback inside it is attributed to the TNT.
 */
public class TntKnockbackListener implements Listener {

    static final double MAX_TNT_KNOCKBACK_MULTIPLIER = 3D;

    private final double knockbackMultiplier;
    private TNTPrimed explodingTnt;
    private int explodingTick = -1;

    public TntKnockbackListener() {
        this.knockbackMultiplier = normalizeMultiplier(
                config.getYml().getDouble(ConfigPath.GENERAL_TNT_KNOCKBACK_MULTIPLIER, 1D));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTntPrime(ExplosionPrimeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed tnt) || !(tnt.getSource() instanceof Player)) return;
        explodingTnt = tnt;
        explodingTick = Bukkit.getCurrentTick();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTntExploded(EntityExplodeEvent event) {
        if (explodingTnt != null && event.getEntity().equals(explodingTnt)) {
            explodingTnt = null;
            explodingTick = -1;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionKnockback(EntityKnockbackEvent event) {
        if (event.getCause() != EntityKnockbackEvent.Cause.EXPLOSION) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!isPlayerTntExploding(victim.getWorld(), Bukkit.getCurrentTick())) return;
        IArena arena = Arena.getArenaByPlayer(victim);
        if (arena == null || !arena.isPlayer(victim)) return;
        if (Double.compare(knockbackMultiplier, 1D) == 0) return;

        event.setKnockback(scaleKnockback(event.getKnockback(), knockbackMultiplier));
    }

    private boolean isPlayerTntExploding(World world, int currentTick) {
        return explodingTnt != null && explodingTick == currentTick && explodingTnt.getWorld().equals(world);
    }

    static Vector scaleKnockback(Vector rawKnockback, double multiplier) {
        return rawKnockback.clone().multiply(normalizeMultiplier(multiplier));
    }

    static double normalizeMultiplier(double value) {
        if (!Double.isFinite(value)) return 1D;
        return Math.min(MAX_TNT_KNOCKBACK_MULTIPLIER, Math.max(0D, value));
    }
}
