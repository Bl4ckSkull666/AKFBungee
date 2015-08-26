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
import java.util.UUID;

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
        _awayler.put(uuid, this);
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
    public static final HashMap<UUID, Afkler> _awayler = new HashMap<>();
}
