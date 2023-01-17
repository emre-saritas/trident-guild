package tc.trident.tridentguild.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tc.trident.tridentguild.TridentGuild;

import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    public static String skytcprefix="&2SurvivalTC &8» ";
    public static final NumberFormat nf = NumberFormat.getInstance(new Locale("tr", "TR"));


    public static double getRandomNumber(int start, int end) {
        double random = new Random().nextDouble();
        double result = start + (random * (end - start));
        return result;
    }

    public static NBTTagCompound getNBT (Entity entity) {

        net.minecraft.server.v1_16_R3.Entity nmsEntity = ((CraftEntity)entity).getHandle();

        NBTTagCompound nbt = new NBTTagCompound();

        nmsEntity.d(nbt);


        return nbt;
    }

    public static void setNBT (Entity entity, NBTTagCompound nbt) {

        net.minecraft.server.v1_16_R3.Entity nmsEntity = ((CraftEntity)entity).getHandle();

        // One of these should work, there are no other methods
        nmsEntity.d(nbt.clone());

        ((CraftEntity)entity).setHandle(nmsEntity);

    }

    public static Map<UUID, Integer> sortByValue(Map<UUID, Integer> unsortMap, final boolean order)
    {
        List<Map.Entry<UUID, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    public static String addColors(String s) {
        if (!Bukkit.getVersion().contains("1.16")) {
            return colorize(s);
        }
        s = s.replace("{","").replace("}","");
        final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        for (Matcher match = pattern.matcher(s); match.find(); match = pattern.matcher(s)) {
            final String hexColor = s.substring(match.start(), match.end());
            s = s.replace(hexColor, ChatColor.of(hexColor).toString());
        }
        return colorize(s);
    }
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Location getLocationFromString( String locStr, World world){
        String[] locSTRarr = locStr.split(",");
        return new Location(world,Double.parseDouble(locSTRarr[0]),Double.parseDouble(locSTRarr[1]),Double.parseDouble(locSTRarr[2]));
    }

    public static void removeItems(PlayerInventory inventory, ItemStack item, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (is.isSimilar(item)) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

    public static int itemCountInInventory(PlayerInventory inventory, ItemStack item){
        int itemCountInPlayersInventory = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.isSimilar(item)) {
                itemCountInPlayersInventory += stack.getAmount();
            }
        }
        return itemCountInPlayersInventory;
    }

    public static void sendError(Player player, String errorID){
        player.sendMessage(Utils.addColors(Utils.getMessage(errorID,true)));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO,1,1);
    }

    public static String getMessage(String id,boolean prefix){
        if(prefix){
            return skytcprefix+ TridentGuild.messages.getString(id);
        }else{
            return TridentGuild.messages.getString(id);
        }
    }
    public static void sendHelpMessages(Player player){
        for(String message : TridentGuild.messages.getStringList("help")){
            player.sendMessage(Utils.addColors(message));
        }
    }

    public static Map<String,String> getJsonStringAsMap(String jsonString){
        try {
            if(jsonString == null){
                return new HashMap<>();
            }
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String,String>> typeRef
                    = new TypeReference<HashMap<String,String>>() {};

            HashMap<String,String> o = mapper.readValue(jsonString, typeRef);
            return o;
        }
        catch (JsonProcessingException ignored){
            return new HashMap<>();
        }
    }
    public static void debug(String debugMessage){
        Bukkit.getLogger().info("[DEBUG] "+debugMessage);
        if(TridentGuild.config.getBoolean("debug-mode")){
            Bukkit.broadcastMessage("[DEBUG] "+debugMessage);
        }
    }
    public static boolean isDebug(){
        return TridentGuild.config.getBoolean("debug-mode");
    }

}
