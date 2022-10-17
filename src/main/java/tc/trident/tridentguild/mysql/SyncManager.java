package tc.trident.tridentguild.mysql;

import com.avaje.ebean.validation.NotNull;
import me.lucko.helper.messaging.ChannelAgent;
import me.lucko.helper.messaging.Messenger;
import me.lucko.helper.redis.Redis;
import me.lucko.helper.redis.RedisCredentials;
import me.lucko.helper.redis.RedisProvider;
import me.lucko.helper.redis.plugin.HelperRedis;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import tc.trident.sync.TridentSync;
import tc.trident.tridentguild.Guild;
import tc.trident.tridentguild.GuildMember;
import tc.trident.tridentguild.TridentGuild;
import tc.trident.tridentguild.utils.Utils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncManager implements RedisProvider{

    private RedisCredentials redisCredentials;
    private HelperRedis globalRedis;

    public SyncManager(){
        loadRedis();
    }


    private void loadRedis(){
        Bukkit.getLogger().info("TridentGuild - Redis connection initilazing...");
        Bukkit.getLogger().info("TridentGuild - Redis connecting on "+TridentGuild.redis.getString("redis.host"));
        redisCredentials = RedisCredentials.of(TridentGuild.redis.getString("redis.host"),6379,"9pV963JIYAk2");

        this.globalRedis = new HelperRedis(redisCredentials);
        this.globalRedis.bindWith(TridentGuild.getInstance());

        TridentGuild.getInstance().provideService(RedisProvider.class, this);
        TridentGuild.getInstance().provideService(RedisCredentials.class, this.redisCredentials);
        TridentGuild.getInstance().provideService(Redis.class, this.globalRedis);
        TridentGuild.getInstance().provideService(Messenger.class, this.globalRedis);
    }

    public void updateGuild(Guild guild){
        TridentGuild.getGuildManager().updateGuild(guild);
        Utils.debug("[TridentGuild] Guild updated - "+guild.getGuildUUID());
    }

    public void syncGuild(Guild guild, SyncType syncType){
        syncGuild(guild,syncType,null);
    }
    public void syncGuild(Guild guild, SyncType syncType, String playerName){
        TridentSync.getInstance().getRedis().getChannel("sGuild", GuildRedisData.class).sendMessage(new GuildRedisData(guild,syncType,playerName));
    }

    @NotNull
    @Override
    public Redis getRedis() {
        return globalRedis;
    }

    @NotNull
    @Override
    public Redis getRedis(@Nonnull RedisCredentials redisCredentials) {
        return new HelperRedis(redisCredentials);
    }

    @Nonnull
    @Override
    public RedisCredentials getGlobalCredentials() {
        return this.redisCredentials;
    }
}
