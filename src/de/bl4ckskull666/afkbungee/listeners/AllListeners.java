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
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
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
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPostLoginEvent(PostLoginEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        AFKBungee.setPlayerUUIDActive(pp.getUniqueId());
        Afkler.checkAFK();
        if(Afkler.getAFKSize() > 0) {
            pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "current-afk.header", ChatColor.YELLOW + "Currently are %amount% players away from keyboard.", new String[] {"%amount%"}, new String[] {String.valueOf(Afkler.getAFKSize())}));
            pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "current-afk.footer", ChatColor.YELLOW + "For a list of all Away Players do /afk list"));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerKickEvent(ServerKickEvent e) {
        Afkler.removeAFK(e.getPlayer().getUniqueId());
        AFKBungee.getLastActiv().remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerDisconnectEvent(ServerDisconnectEvent e) {
        Afkler.removeAFK(e.getPlayer().getUniqueId());
        AFKBungee.getLastActiv().remove(e.getPlayer().getUniqueId());
    }
    
    private ProxiedPlayer getPlayer(InetSocketAddress con) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().equals(con))
                return pp;
        }
        return null;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("BungeeCord"))
            return;
        
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String sub = in.readUTF();
        if(!sub.equalsIgnoreCase("AFKB"))
            return;
        
        String cat = in.readUTF();
        if(cat.equalsIgnoreCase("Player")) {
            UUID uuid = UUID.fromString(in.readUTF());
            AFKBungee.debugMe("Player activity " + uuid.toString());
            AFKBungee.setPlayerUUIDActive(uuid);
        } else if(cat.equalsIgnoreCase("Config")) {
            AFKBungee.debugMe("Configuration requested. Send it now.");
            ProxiedPlayer pp = getPlayer(e.getReceiver().getAddress());
            if(pp == null)
                pp = getPlayer(e.getSender().getAddress());
            if(pp != null && AFKBungee.getPlugin().getConfig().isConfigurationSection("bukkit-configs")) {
                ProxyServer.getInstance().getScheduler().runAsync(AFKBungee.getPlugin(), new SendConfiguration(pp.getServer().getInfo()));
            }
        }
    }
    
    public static class SendConfiguration implements Runnable {
        private ServerInfo _si;
        public SendConfiguration(ServerInfo si) {
            _si = si;
        }
        
        @Override
        public void run() {
            for(String key: AFKBungee.getPlugin().getConfig().getConfigurationSection("bukkit-configs").getKeys(true)) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AFKB");
                out.writeUTF("Config");
                out.writeUTF(key.replace("bukkit-configs.", ""));
                out.writeUTF(AFKBungee.getPlugin().getConfig().get("bukkit-configs." + key).toString().replace(" ", "__--__"));
                _si.sendData("BungeeCord", out.toByteArray());
                AFKBungee.debugMe("Config send: " + out.toString());
            }
            AFKBungee.debugMe("Configuration sended to " + _si.getName());
        }
    }
}
