package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.enums.CustomHeadCategory;
import com.cortezromeo.clansplus.util.MessageUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class CustomHeadSupport {

    public static void setupCustomHeadJsonFiles() {
        File customHeadsFolder = new File(ClansPlus.plugin.getDataFolder() + "/customheads");
        if (!customHeadsFolder.exists())
            customHeadsFolder.mkdirs();

        for (CustomHeadCategory customHeadCategory : CustomHeadCategory.values()) {
            String customHeadCategoryString = customHeadCategory.toString().toLowerCase().replace("_", "-");

            // json file already existed -> skip
            if (new File(ClansPlus.plugin.getDataFolder() + "/customheads/custom-head-" + customHeadCategoryString + ".json").exists())
                continue;

            try (FileWriter file = new FileWriter(ClansPlus.plugin.getDataFolder() + "/customheads/custom-head-" + customHeadCategoryString + ".json")) {
                file.write(fetchJsonFromApi("https://minecraft-heads.com/scripts/api.php?cat=" + customHeadCategoryString));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String fetchJsonFromApi(String apiUrl) throws IOException {
        StringBuilder jsonResponse = new StringBuilder();
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Check for a successful response
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
            }
        } else {
            throw new IOException("Failed to fetch data. HTTP Code: " + connection.getResponseCode());
        }
        connection.disconnect();
        return jsonResponse.toString();
    }


}
