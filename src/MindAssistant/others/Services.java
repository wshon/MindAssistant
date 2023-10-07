package MindAssistant.others;

import arc.Core;
import arc.struct.Seq;
import arc.util.Http;
import arc.util.Log;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.io.versions.LegacyIO;
import mindustry.net.ServerGroup;
import mindustry.ui.dialogs.JoinDialog;

import static mindustry.Vars.becontrol;
import static mindustry.Vars.defaultServers;

public class Services {
    static String serverJsonBeURL = "https://cdn.jsdelivr.net/gh/Anuken/Mindustry@master/servers_be.json";
    static String serverJsonURL = "https://cdn.jsdelivr.net/gh/Anuken/Mindustry@master/servers_v7.json";

    public static void load() {

        var url = becontrol.active() ? serverJsonBeURL : serverJsonURL;
        Log.info("Fetching community servers at @", url);

        //get servers
        Http.get(url).error(t -> Log.err("Failed to fetch community servers", t)).submit(result -> {
            Jval val = Jval.read(result.getResultAsString());
            Seq<ServerGroup> servers = new Seq<>();
            val.asArray().each(child -> {
                String name = child.getString("name", "");
                boolean prioritized = child.getBool("prioritized", false);
                String[] addresses;
                if (child.has("addresses") || (child.has("address") && child.get("address").isArray())) {
                    addresses = (child.has("addresses") ? child.get("addresses") : child.get("address")).asArray().map(Jval::asString).toArray(String.class);
                } else {
                    addresses = new String[]{child.getString("address", "<invalid>")};
                }
                servers.add(new ServerGroup(name, addresses, prioritized));
            });
            //modify default servers on main thread
            Core.app.post(() -> {
                servers.sort(s -> s.name == null ? Integer.MAX_VALUE : s.name.hashCode());
                // defaultServers.addAll(servers);
                servers.each(child -> {
                    if (!defaultServers.contains(child)) {
                        defaultServers.add(child);
                    }
                });
                Log.info("Fetched @ community servers.", defaultServers.size);
            });
        });
    }
}
