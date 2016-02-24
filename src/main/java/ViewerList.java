import org.json.JSONArray;
import org.json.JSONObject;
import util.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 24.02.16.
 */
public class ViewerList {

    public static List<String> getViewerList(String channel) {
        ArrayList<String> ret = new ArrayList<>();
        try {
            URL url = new URL("http://tmi.twitch.tv/group/user/" + channel + "/chatters");
            String line = Utils.createAndParseBufferedReader(url.openStream());
            JSONObject site = new JSONObject(line);
            JSONObject chatters = site.getJSONObject("chatters");
            JSONArray mods = chatters.getJSONArray("moderators");
            for (int i = 0; i < mods.length(); i++) {
                ret.add(mods.getString(i));
            }
            JSONArray staff = chatters.getJSONArray("staff");
            for (int i = 0; i < staff.length(); i++) {
                ret.add(staff.getString(i));
            }
            JSONArray admins = chatters.getJSONArray("admins");
            for (int i = 0; i < admins.length(); i++) {
                ret.add(admins.getString(i));
            }
            JSONArray global_mods = chatters.getJSONArray("global_mods");
            for (int i = 0; i < global_mods.length(); i++) {
                ret.add(global_mods.getString(i));
            }
            JSONArray viewers = chatters.getJSONArray("viewers");
            for (int i = 0; i < viewers.length(); i++) {
                ret.add(viewers.getString(i));
            }
        } catch (Exception e) {
//            log.e(e.toString());
        }
        return ret;
    }
}
