package de.bl4ckskull666.afkbungee;

import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author PapaHarni
 */
public final class Utils {
    public static String getTimeString(long t, UUID uuid) {
        String strTime = "";
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if((t/(60*60*24)) >= 1) {
            day = (int)(t/(60*60*24));
            t -= (day*(60*60*24));
        }
        
        if(day > 0) {
            if(day == 1)
                strTime = day + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.day", "day");
            else
                strTime = day + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.days", "days");
        }
        
        if((t/(60*60)) >= 1) {
            hour = (int)(t/(60*60));
            t -= (hour*(60*60));
        }
        
        if(!strTime.isEmpty() || hour > 0) {
            if(hour == 1)
                strTime += (strTime.isEmpty()?"":", ") + hour + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.hour", "hour");
            else
                strTime += (strTime.isEmpty()?"":", ") + hour + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.hours", "hours");
        }
        
        if((t/60) >= 1) {
            minute = (int)(t/60);
            t -= (minute*60);
        }
        
        if(!strTime.isEmpty() || minute > 0) {
            if(minute == 1)
                strTime += (strTime.isEmpty()?"":", ") + minute + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.minute", "minute");
            else
                strTime += (strTime.isEmpty()?"":", ") + minute + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.minutes", "minutes");
        }
        
        if(t >= 1) {
            second = (int)t;
        }
        
        if(!strTime.isEmpty() || second > 0) {
            if(second == 1)
                strTime += (strTime.isEmpty()?"":", ") + second + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.second", "second");
            else
                strTime += (strTime.isEmpty()?"":", ") + second + " " + Language.getMsg(AFKBungee.getPlugin(), uuid, "time.seconds", "seconds");
        }
        return strTime;
    }
}
