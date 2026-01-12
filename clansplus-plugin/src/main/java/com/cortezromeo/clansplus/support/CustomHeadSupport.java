package com.cortezromeo.clansplus.support;

import com.cortezromeo.clansplus.ClansPlus;
import com.cortezromeo.clansplus.Settings;
import com.cortezromeo.clansplus.enums.CustomHeadCategory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CustomHeadSupport {

    public static void setupCustomHeadJsonFiles() {
        String customHeadsFolderName = "customheads" + (Settings.CUSTOM_HEADS_API_V2_ENABLED ? "V2" : "");
        String appUUID = "20510075-3fbd-446a-8b59-2218b51a0959";

        File customHeadsFolder = new File(ClansPlus.plugin.getDataFolder() + "/" + customHeadsFolderName);
        if (!customHeadsFolder.exists()) customHeadsFolder.mkdirs();

        for (CustomHeadCategory customHeadCategory : CustomHeadCategory.values()) {

            // api v1 does not support helmets
            if (!Settings.CUSTOM_HEADS_API_V2_ENABLED)
                if (customHeadCategory.equals(CustomHeadCategory.HELMETS))
                    continue;

            String customHeadCategoryString = customHeadCategory.toString().toLowerCase().replace("_", "-");

            // json file already existed -> skip
            if (new File(ClansPlus.plugin.getDataFolder() + "/" + customHeadsFolderName + "/custom-head-" + customHeadCategoryString + ".json").exists())
                continue;

            try (FileWriter file = new FileWriter(ClansPlus.plugin.getDataFolder() + "/" + customHeadsFolderName + "/custom-head-" + customHeadCategoryString + ".json")) {
                if (!Settings.CUSTOM_HEADS_API_V2_ENABLED)
                    file.write(fetchJsonFromApi("https://minecraft-heads.com/scripts/api.php?cat=" + customHeadCategoryString));
                else {
                    String url =
                            "https://minecraft-heads.com/api/heads/custom-heads" +
                                    "?app_uuid=" + appUUID +
                                    "&category_id=" + customHeadCategory.getId();

                    HttpClient client = HttpClient.newHttpClient();

                    String apiKey = "TSfX00qvqRHcGXnOT9KQ0+utvGPAO2EtYbTDhmrUXjO+HM7jMJY3pBJ97I4n8qGlIQ/7ZNbtXUit8OCCV3HWMg==";
                    if (!Settings.CUSTOM_HEADS_API_V2_CUSTOM_KEY.isEmpty())
                        apiKey = Settings.CUSTOM_HEADS_API_V2_CUSTOM_KEY;

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("accept", "application/json")
                            .header("api-key", apiKey)
                            .GET()
                            .build();

                    HttpResponse<String> response =
                            client.send(request, HttpResponse.BodyHandlers.ofString());

                    file.write(response.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
