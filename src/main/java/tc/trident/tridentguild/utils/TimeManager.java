package tc.trident.tridentguild.utils;

import java.text.SimpleDateFormat;

public class TimeManager {

    static SimpleDateFormat timeLeft = new SimpleDateFormat("HH:mm:ss");


    public static String getTimeLeft(long millis){
        int day = (int) (millis/(1000*60*60*24));
        int hours = (int) (millis%(1000*60*60*24)/(1000*60*60));
        int mins = (int) (millis%(1000*60*60)/(1000*60));
        int seconds = (int) (millis%(1000*60)/(1000));
        if(day==0) return hours+" saat "+mins+" dakika "+seconds+" saniye";
        else return day+" gün "+hours+" saat "+mins+" dakika "+seconds+" saniye";
    }
}
