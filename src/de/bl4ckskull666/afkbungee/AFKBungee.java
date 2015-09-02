/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.afkbungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.afkbungee.commands.afk;
import de.bl4ckskull666.afkbungee.listeners.AllListeners;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import yamlapi.file.FileConfiguration;

/**
 *
 * @author PapaHarni
 */
public class AFKBungee extends Plugin {
    private static ScheduledTask _task = null;
    private FileConfiguration _config;
    
    @Override
    public void onEnable() {
        _plugin = this;
        
        _config = Mu1ti1ingu41.loadConfig(this);
        _task = ProxyServer.getInstance().getScheduler().schedule(_plugin, new checkActivity(), getConfig().getInt("check-every-x-second", 10), getConfig().getInt("check-every-x-second", 10), TimeUnit.SECONDS);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new AllListeners());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new afk());
        Mu1ti1ingu41.loadExternalDefaultLanguage(this, "languages");
    }
    
    @Override
    public void onDisable() {
        if(_task != null)
            _task.cancel();
    }
    
    public FileConfiguration getConfig() {
        return _config;
    }

    public void saveConfig() {
        Mu1ti1ingu41.saveConfig(_config, _plugin);
    }
    
    public void reloadConfig() {
        _config = Mu1ti1ingu41.loadConfig(_plugin);
    }
    
    //static
    private static AFKBungee _plugin;
    private static final HashMap<UUID, Long> _lastActiv = new HashMap<>();
    
    public static AFKBungee getPlugin() {
        return _plugin;
    }
    
    public static HashMap<UUID, Long> getLastActiv() {
        return _lastActiv;
    }
    
    public static void setPlayerUUIDActive(UUID uuid) {
        if(Afkler._awayler.containsKey(uuid)) {
            Afkler._awayler.remove(uuid);
            informPlayers(uuid, false);
        }
        _lastActiv.put(uuid, System.currentTimeMillis());
    }
    
    public static void informPlayers(UUID uuid, boolean isNowAfk) {
        ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(uuid);
        if(pp == null)
            return;
        
        debugMe("Set Player Status and send it to Bukkit.");
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("AFKB");
        out.writeUTF("Player");
        out.writeUTF(pp.getUniqueId().toString());
        out.writeBoolean(isNowAfk);
        pp.getServer().sendData("BungeeCord", out.toByteArray());
        
        String name = pp.getDisplayName().isEmpty()?pp.getName():pp.getDisplayName();
        for(ProxiedPlayer ipp: ProxyServer.getInstance().getPlayers()) {
            if(ipp.getUniqueId() == pp.getUniqueId()) {
                if(isNowAfk)
                    ipp.sendMessage(Language.getMessage(_plugin, ipp.getUniqueId(), "you.now-away", ChatColor.YELLOW + "You are now mark as Away from Keyboard."));
                else
                    ipp.sendMessage(Language.getMessage(_plugin, ipp.getUniqueId(), "you.no-more-away", ChatColor.YELLOW + "You are no longer mark as Away from Keyboard"));
            } else {
                if(isNowAfk)
                    ipp.sendMessage(Language.getMessage(_plugin, ipp.getUniqueId(), "other.now-away", ChatColor.GOLD + "%name%" + ChatColor.YELLOW + " is now mark as Away from Keyboard.", new String[] {"%name%"}, new String[] {name}));
                else
                    ipp.sendMessage(Language.getMessage(_plugin, ipp.getUniqueId(), "other.no-more-away", ChatColor.GOLD + "%name%" + ChatColor.YELLOW + " is no more longer mark as Away from Keyboard", new String[] {"%name%"}, new String[] {name}));

            }
        }
    }
    
    public static BaseComponent[] convert(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    private static class checkActivity implements Runnable {
        @Override
        public void run() {
            HashMap<UUID, Long> temp = new HashMap<>();
            temp.putAll(_lastActiv);
            for(Map.Entry<UUID, Long> me: temp.entrySet()) {
                if(Math.round((System.currentTimeMillis()-me.getValue())/1000) >= _plugin.getConfig().getLong("auto-away", 180)) {
                    Afkler t = new Afkler(me.getKey(), "%auto-message%");
                    _lastActiv.remove(me.getKey());
                    informPlayers(me.getKey(), true);
                }
            }
        }
    }
    
    public static void debugMe(String msg) {
        if(_plugin.getConfig().getBoolean("debug", false))
            _plugin.getLogger().log(Level.INFO, "[DEBUG] {0}", msg);
    }
}
