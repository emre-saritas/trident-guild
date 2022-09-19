package tc.trident.tridentguild.utils;

import java.util.HashMap;

public class CooldownManager {

    HashMap<String, Long> cooldowns = new HashMap<>();

    public void addCooldown(String key){
        if(cooldowns.containsKey(key)){
            cooldowns.replace(key, System.currentTimeMillis());
        }else{
            cooldowns.put(key,System.currentTimeMillis());
        }
    }

    public long getSecondsLeft(String key, long resetPeriod){
        if(!cooldowns.containsKey(key)){
            return 0;
        }else{
            return ((cooldowns.get(key)/1000)+ resetPeriod - (System.currentTimeMillis()/1000));
        }
    }
}
