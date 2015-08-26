/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.afkbungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.afkbungee.AFKBungee;
import de.bl4ckskull666.afkbungee.Afkler;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.net.InetSocketAddress;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class AllListeners implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginEvent(PostLoginEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        AFKBungee.getLastActiv().put(pp.getUniqueId(), System.currentTimeMillis());
        if(!Afkler._awayler.isEmpty()) {
            pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "current-afk.header", ChatColor.YELLOW + "Currently are %amount% players away from keyboard.", new String[] {"%amount%"}, new String[] {String.valueOf(Afkler._awayler.size())}));
            pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "current-afk.footer", ChatColor.YELLOW + "For a list of all Away Players do /afk list"));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerKickEvent(ServerKickEvent e) {
        Afkler._awayler.remove(e.getPlayer().getUniqueId());
        AFKBungee.getLastActiv().remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerDisconnectEvent(ServerDisconnectEvent e) {
        Afkler._awayler.remove(e.getPlayer().getUniqueId());
        AFKBungee.getLastActiv().remove(e.getPlayer().getUniqueId());
    }
    
    private ProxiedPlayer getPlayer(InetSocketAddress con) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().equals(con))
                return pp;
        }
        return null;
    }
    
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("BungeeCord"))
            return;
        
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String sub = in.readUTF();
        if(sub.equalsIgnoreCase("AFKBPlayer")) {
            ProxiedPlayer pp = getPlayer(e.getReceiver().getAddress());
            if(pp != null)
                AFKBungee.setPlayerUUIDActive(pp.getUniqueId());
        } else if(sub.equalsIgnoreCase("AFKBConfig")) {
            ProxiedPlayer pp = getPlayer(e.getReceiver().getAddress());
            if(pp != null && AFKBungee.getPlugin().getConfig().isConfigurationSection("bukkit-configs")) {
                for(String key: AFKBungee.getPlugin().getConfig().getConfigurationSection("bukkit-configs").getKeys(false)) {
                    for(String k: AFKBungee.getPlugin().getConfig().getConfigurationSection("bukkit-configs." + key).getKeys(false)) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("AFKBConfig");
                        out.writeUTF(key);
                        out.writeUTF(k);
                        out.writeUTF(AFKBungee.getPlugin().getConfig().getString("bukkit-configs." + key + "." + k));
                        pp.getServer().sendData("BungeeCord", out.toByteArray());
                    }
                }
            }
        }
        
    }
}
