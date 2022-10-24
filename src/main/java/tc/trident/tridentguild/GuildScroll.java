package tc.trident.tridentguild;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tc.trident.tridentguild.utils.YamlItem;

public class GuildScroll {

    public static ItemStack getItem(){
        return getItem(1);
    }
    public static ItemStack getItem(int amount){
        YamlItem item = new YamlItem("guild-scroll");
        item.setAmount(amount);
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item.complete());
        NBTTagCompound tags;
        if(nmsItem.hasTag())
            tags = nmsItem.getTag();
        else
            tags = new NBTTagCompound();
        tags.setBoolean("guild-scroll",true);
        nmsItem.setTag(tags);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static boolean hasItem(PlayerInventory inventory){
        for (ItemStack stack : inventory.getContents()) {
            if(stack == null) continue;
            net.minecraft.server.v1_16_R3.ItemStack itemNMS = CraftItemStack.asNMSCopy(stack);
            if(!itemNMS.hasTag()) continue;
            NBTTagCompound itemTags = itemNMS.getTag();
            if(itemTags.hasKey("guild-scroll")) return true;
        }
        return false;
    }
}
