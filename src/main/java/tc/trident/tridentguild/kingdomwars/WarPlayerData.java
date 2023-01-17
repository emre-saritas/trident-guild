package tc.trident.tridentguild.kingdomwars;

public class WarPlayerData {

    int kills = 0;
    int deaths = 0;

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public void incDeaths() {
        this.deaths += 1;
    }

    public void incKills() {
        this.kills += 1;
    }
}
