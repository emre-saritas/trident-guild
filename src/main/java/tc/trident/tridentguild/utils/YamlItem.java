package tc.trident.tridentguild.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import tc.trident.tridentguild.TridentGuild;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YamlItem {
    private Material type;
    private String name;
    private String headValue;
    private List<String> lore;
    private int amount;
    private short dataValue;
    private boolean glow;
    private String nbt;
    private String path;
    private int slot;

    public YamlItem(final String path) {
        this.path = path;
        this.setType(((TridentGuild.config.get(path + ".type") instanceof String)) ? Material.valueOf(TridentGuild.config.getString(path + ".type").toUpperCase()) : Material.AIR);
        this.setName(((TridentGuild.config.get(path + ".name") instanceof String)) ? TridentGuild.config.getString(path + ".name") : null);
        this.setLore(TridentGuild.config.getStringList(path + ".lore"));
        this.setSlot(((TridentGuild.config.get(path + ".slot") instanceof Integer)) ? TridentGuild.config.getInt(path + ".slot") : 0);
        this.setAmount(((TridentGuild.config.get(path + ".amount") instanceof Integer)) ? TridentGuild.config.getInt(path + ".amount") : 1);
        this.setGlow((TridentGuild.config.get(path + ".glow") instanceof Boolean) ? TridentGuild.config.getBoolean(path + ".glow") : false);
        this.setHeadValue(((TridentGuild.config.get(path + ".head-value") instanceof String)) ? TridentGuild.config.getString(path + ".head-value") : null);
    }
    public YamlItem(final String path, Yaml config) {
        this.path = path;
        this.setType(((config.get(path + ".type") instanceof String)) ? Material.valueOf(config.getString(path + ".type").toUpperCase()) : Material.AIR);
        this.setName(((config.get(path + ".name") instanceof String)) ? config.getString(path + ".name") : null);
        this.setLore(config.getStringList(path + ".lore"));
        this.setSlot(((config.get(path + ".slot") instanceof Integer)) ? config.getInt(path + ".slot") : 0);
        this.setAmount(((config.get(path + ".amount") instanceof Integer)) ? config.getInt(path + ".amount") : 1);
        this.setGlow((config.get(path + ".glow") instanceof Boolean) ? config.getBoolean(path + ".glow") : false);
        this.setHeadValue(((config.get(path + ".head-value") instanceof String)) ? config.getString(path + ".head-value") : null);
    }

    public String getKey(String key) {
        return TridentGuild.config.getString(this.path + "."+key);
    }
    public Material getType() {
        return this.type;
    }
    public YamlItem setType(final Material type) {
        this.type = type;
        return this;
    }

    public Boolean getGlow() {
        return this.glow;
    }
    public YamlItem setGlow(final Boolean type) {
        this.glow = type;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }
    public YamlItem setAmount(final Integer amount) {
        this.amount = amount;
        return this;
    }

    public String getName() {
        return this.name;
    }
    public YamlItem setName(final String name) {
        this.name = name;
        return this;
    }
    public YamlItem setHeadValue(final String value) {
        this.headValue = value;
        return this;
    }

    // slot = 10
    // 10/9 = 1
    // 1 * 9 - 1 = 8 / 10 - 8 = 2
    public int getRow(){ return (int)Math.floor(this.slot/9);}
    public int getColumn(){ return (int)Math.floor(this.slot%9);}
    public int getSlot() {
        return this.slot;
    }
    public YamlItem setSlot(final int slot) {
        this.slot = slot;
        return this;
    }

    public List<String> getLore() {
        return this.lore;
    }
    public YamlItem setLore(final List<String> lore) {
        this.lore = lore;
        return this;
    }
    public YamlItem addLore(String lore) {
        this.lore.add(lore);
        return this;
    }
    public YamlItem removeLore() {
        this.lore.remove(lore.size()-1);
        return this;
    }
    public YamlItem renewLore() {
        this.setLore(TridentGuild.config.getStringList(this.path + ".lore"));
        return this;
    }
    public YamlItem replaceLore(final String search,final String replace){
        final List<String> list = new ArrayList<String>();
        for (final String lores : this.getLore()) {
            list.add(lores.replace(search,(replace.equals("")) ? "-" : replace));
        }
        this.setLore(list);
        return this;
    }

    public ItemStack complete(){

        ItemStack item = null;
        if(this.headValue == null){
            item = new ItemStack(this.getType(), this.getAmount());
            item.setAmount(this.getAmount());
            ItemMeta imeta = item.getItemMeta();
            if(this.getName() != null) imeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getName()));
            List<String> list = new ArrayList<String>();
            for (String lores : this.getLore()) {
                list.add(ChatColor.translateAlternateColorCodes('&', lores));
            }
            imeta.setLore((List)list);
            if (this.getGlow()) {
                imeta.addEnchant(Enchantment.LUCK, 1, false);
            }
            imeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_DYE,ItemFlag.HIDE_ENCHANTS});
            item.setItemMeta(imeta);
            if(((TridentGuild.config.get(path + ".model"))) instanceof Integer){
                net.minecraft.server.v1_16_R3.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
                NBTTagCompound nbtset = new NBTTagCompound();
                if (nmsitem.hasTag()) {
                    nbtset.a(nmsitem.getTag());
                }
                nbtset.setInt("CustomModelData", TridentGuild.config.getInt(path + ".model"));
                nmsitem.setTag(nbtset);
                return CraftItemStack.asBukkitCopy(nmsitem);
            }
        }else{
            item = new ItemStack(Material.PLAYER_HEAD);

            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            if(this.getName() != null) skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getName()));
            List<String> list = new ArrayList<String>();
            for (String lores : this.getLore()) {
                list.add(ChatColor.translateAlternateColorCodes('&', lores));
            }
            skullMeta.setLore((List)list);
            if (this.getGlow()) {
                skullMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS });
                skullMeta.addEnchant(Enchantment.LUCK, 1, false);
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);

            profile.getProperties().put("textures", new Property("textures", this.headValue));

            try {
                Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(skullMeta, profile);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }

            item.setItemMeta(skullMeta);
        }
        return item;
    }
}


