import org.json.JSONObject;
import util.Log;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by miller on 23.02.16.
 */
public class FollowAge {
    Log log = new Log(getClass().getSimpleName());
    int access;

    public FollowAge(int access) {
        this.access = access;
    }

    public String get(int a, String nick, String name) {
        if (a >= access) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                HttpsURLConnection connection;
                URL url = new URL(String.format("https://api.twitch.tv/kraken/users/%s/follows/channels/%s", nick, name));
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 200) {
//            StringBuilder postData = new StringBuilder();
//            postData.append(URLEncoder.encode("last_activity_date", "UTF-8"));
//            postData.append('=');
//            postData.append(URLEncoder.encode(String.valueOf("0"), "UTF-8"));
//            connection.setDoOutput(true);
//            connection.getOutputStream().write(postData.toString().getBytes());
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    JSONObject jsonObject = new JSONObject(response.toString());
                    Date date = sdf.parse(jsonObject.get("created_at").toString());
                    long now = System.currentTimeMillis();
                    long t = (now - (date.getTime() + 120000));
                    return String.format("%s has been following %s for %s", nick, name, printMilliseconds(t));
                } else {
                    return String.format("%s is not following %s", nick, name);
                }
            } catch (IOException ioe) {
                log.e(ioe.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String printMilliseconds(long ms) {
        StringBuilder stringBuilder = new StringBuilder();
        long seconds = (ms / 1000) % 60;
        long minutes = ((ms / (1000 * 60)) % 60);
        long hours = ((ms / (1000 * 60 * 60)) % 24);
        long days = (ms / (1000 * 60 * 60 * 24));
        if (days > 0) {
            stringBuilder.append(days);
            if (days == 1) {
                stringBuilder.append(" day");
            } else {
                stringBuilder.append(" days");
            }
        }
        if (hours > 0) {
            if (days > 0) {
                if (seconds <= 0 && minutes <= 0) {
                    stringBuilder.append(" and ");
                } else {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(hours);
            if (hours == 1) {
                stringBuilder.append(" hour");
            } else {
                stringBuilder.append(" hours");
            }
        }
        if (minutes > 0) {
            if (days > 0 || hours > 0) {
                if (seconds <= 0) {
                    stringBuilder.append(" and ");
                } else {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(minutes);
            if (minutes == 1) {
                stringBuilder.append(" minute");
            } else {
                stringBuilder.append(" minutes");
            }
        }
        if (seconds > 0) {
            if (days > 0 || hours > 0 || minutes > 0) {
                stringBuilder.append(" and ");
            }
            stringBuilder.append(seconds);
            if (seconds == 1) {
                stringBuilder.append(" second");
            } else {
                stringBuilder.append(" seconds");
            }
        }
        return stringBuilder.toString();
    }
}
