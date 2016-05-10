/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.afkbungee;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author PapaHarni
 */
public final class Afkler {
    private final String _msg;
    private final Calendar _cal;
    
    public Afkler(UUID uuid, String msg) {
        _msg = msg;
        _cal = Calendar.getInstance(Locale.GERMAN);
        _cal.setTimeInMillis(System.currentTimeMillis());
        Afkler put = _awayler.put(uuid, this);
    }
    
    public String getMessage() {
        return _msg;
    }
    
    public String getDateFormat(String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(_cal.getTime());
        } catch(IllegalArgumentException ex) {
            return "Error with format " + format;
        }
    }
    
    public long getDifferenceSeconds() {
        return ((System.currentTimeMillis()-_cal.getTimeInMillis())/1000);
    }
    
    //Statics
    private static final HashMap<UUID, Afkler> _awayler = new HashMap<>();
    public static int getAFKSize() {
        return _awayler.size();
    }
    
    public static void removeAFK(UUID uuid) {
        _awayler.remove(uuid);
    }
    
    public static boolean isAFK(UUID uuid) {
        return _awayler.containsKey(uuid);
    }
    
    public static Afkler getAFK(UUID uuid) {
        if(!isAFK(uuid))
            return null;
        return _awayler.get(uuid);
    }
    
    public static HashMap<UUID, Afkler> getAFK() {
        return _awayler;
    }
    
    public static void checkAFK() {
        HashMap<UUID, Afkler> tmp = new HashMap<>();
        tmp.putAll(_awayler);
        _awayler.clear();
        for(Map.Entry<UUID, Afkler> me: tmp.entrySet()) {
            if(ProxyServer.getInstance().getPlayer(me.getKey()) != null)
                _awayler.put(me.getKey(), me.getValue());
        }
    }
}
