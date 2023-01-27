package tc.trident.tridentguild.utils;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.HashMap;
import java.util.UUID;

public class CustomBannerStand {

    ArmorStand stand;
    HashMap<PatternType, DyeColor> bannerPatterns;
    Material bannerMaterial;
    UUID guildUUID;

    public CustomBannerStand(Location location, HashMap<PatternType, DyeColor> bannerPatterns, UUID guildUUID, Material bannerMaterial) {
        stand = location.getWorld().spawn(location, ArmorStand.class);
        this.bannerPatterns = bannerPatterns;
        this.guildUUID = guildUUID;
        this.bannerMaterial = bannerMaterial;

        setupBannerStand();
    }

    public void setupBannerStand(){
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setSmall(true);
        stand.getEquipment().setHelmet(getBannerItem());
    }

    public ItemStack getBannerItem(){
        ItemStack banner = new ItemStack(bannerMaterial);
        if(bannerPatterns.size() == 0) return banner;
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        bannerPatterns.forEach((patternType, dyeColor) -> {
            meta.addPattern(new Pattern(dyeColor,patternType));
        });
        banner.setItemMeta(meta);
        return banner.clone();
    }
    public void kill(){
        stand.remove();
    }
    public UUID getGuildUUID() {
        return guildUUID;
    }
    public HashMap<PatternType, DyeColor> getBannerPatterns() {
        return bannerPatterns;
    }
    public ArmorStand getStand() {
        return stand;
    }
}
