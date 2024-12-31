package wethinkcode.spa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.javalin.http.Context;


public  class spotifyapp {


    public static JsonObject searchForSongs(String songName, String accessToken) {

        JsonObject result = new JsonObject();
        JsonArray topTracksArray = new JsonArray();

        try {
            String url = "https://api.spotify.com/v1/search?q=" + songName + "&type=track&limit=5";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
            JsonArray tracks = jsonObject.getAsJsonObject("tracks").getAsJsonArray("items");
            System.out.println("Songs found:");
            for (JsonElement track : tracks) {
                JsonObject trackObj = track.getAsJsonObject();
                JsonObject trackInfo = new JsonObject(); // Create a JSON object for track info

                // Add the track details to the JSON object
                trackInfo.addProperty("name", trackObj.get("name").getAsString());
                trackInfo.addProperty("artist",trackObj.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString());

                System.out.println("Name: " + trackObj.get("name").getAsString());
                System.out.println("Artist: " + trackObj.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString());
                JsonArray images = trackObj.getAsJsonObject("album").getAsJsonArray("images");
                if (images.size() > 0) {
                    JsonObject imageObj = images.get(0).getAsJsonObject();
                    trackInfo.addProperty("imageUrl", imageObj.get("url").getAsString());
                    System.out.println("Image URL: " + imageObj.get("url").getAsString());

                }

                String albumName = trackObj.getAsJsonObject("album").get("name").getAsString();
                trackInfo.addProperty("albumName", albumName);

                topTracksArray.add(trackInfo);
            }

            result.add("Tracks", topTracksArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }

    public static JsonObject getTopTracks(String artistId, String accessToken) {
        JsonObject result = new JsonObject(); // Create a JSON object to hold the result
        JsonArray topTracksArray = new JsonArray();
        int number = 6;

        try {
            // First API call to get artist's profile (to fetch profile image)
            String artistUrl = "https://api.spotify.com/v1/artists/" + artistId;
            HttpURLConnection artistConnection = (HttpURLConnection) new URL(artistUrl).openConnection();
            artistConnection.setRequestMethod("GET");
            artistConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader artistReader = new BufferedReader(new InputStreamReader(artistConnection.getInputStream()));
            StringBuilder artistResponse = new StringBuilder();
            String artistLine;
            while ((artistLine = artistReader.readLine()) != null) {
                artistResponse.append(artistLine);
            }
            artistReader.close();

            JsonObject artistJsonObject = new Gson().fromJson(artistResponse.toString(), JsonObject.class);
            JsonArray artistImages = artistJsonObject.getAsJsonArray("images");

            // Add the artist's profile image to the result (if available)
            if (artistImages.size() > 0) {
                JsonObject artistImageObj = artistImages.get(0).getAsJsonObject(); // Get the first image
                result.addProperty("artistProfileImageUrl", artistImageObj.get("url").getAsString());
            }

            // Second API call to get the artist's top trackss
            String topTracksUrl = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country=US";
            HttpURLConnection topTracksConnection = (HttpURLConnection) new URL(topTracksUrl).openConnection();
            topTracksConnection.setRequestMethod("GET");
            topTracksConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader topTracksReader = new BufferedReader(new InputStreamReader(topTracksConnection.getInputStream()));
            StringBuilder topTracksResponse = new StringBuilder();
            String topTrackLine;
            while ((topTrackLine = topTracksReader.readLine()) != null) {
                topTracksResponse.append(topTrackLine);
            }
            topTracksReader.close();

            JsonObject topTracksJsonObject = new Gson().fromJson(topTracksResponse.toString(), JsonObject.class);
            JsonArray tracks = topTracksJsonObject.getAsJsonArray("tracks");

            // Extract top track information
            for (JsonElement track : tracks) {
                JsonObject trackObj = track.getAsJsonObject();
                JsonObject trackInfo = new JsonObject(); // Create a JSON object for track info

                // Add the track details to the JSON object
                trackInfo.addProperty("name", trackObj.get("name").getAsString());
                trackInfo.addProperty("id", trackObj.get("id").getAsString());
                trackInfo.addProperty("popularity", trackObj.get("popularity").getAsInt());

                JsonArray images = trackObj.getAsJsonObject("album").getAsJsonArray("images");
                if (images.size() > 0) {
                    JsonObject imageObj = images.get(0).getAsJsonObject(); // Get the first image
                    trackInfo.addProperty("imageUrl", imageObj.get("url").getAsString());
                }

                // Add album name
                String albumName = trackObj.getAsJsonObject("album").get("name").getAsString();
                trackInfo.addProperty("albumName", albumName);

                topTracksArray.add(trackInfo); // Add the track info to the array
            }

            // Add the top tracks array to the result JSON object
            result.add("topTracks", topTracksArray);
            // Optionally, add other metadata about the artist or request
            result.addProperty("artistId", artistId);
            result.addProperty("trackCount", topTracksArray.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result; // Return the JSON object containing artist's profile image and top tracks
    }



    public static JsonObject searchForMostPopularArtist(String searchTerm, String accessToken) {
        JsonObject resultObject = new JsonObject();
        try {
            // Limit search results to 1 artist
            String url = "https://api.spotify.com/v1/search?q=" + searchTerm + "&type=artist&limit=1";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
            JsonArray artists = jsonObject.getAsJsonObject("artists").getAsJsonArray("items");

            if (artists.size() > 0) {
                JsonObject artistObj = artists.get(0).getAsJsonObject(); // Get the most popular artist (first one)

                // Construct the result JSON object with artist's details
                resultObject.addProperty("name", artistObj.get("name").getAsString());
                resultObject.addProperty("id", artistObj.get("id").getAsString());
                resultObject.addProperty("popularity", artistObj.get("popularity").getAsInt());

                // Extract the first image URL (if available)
                JsonArray images = artistObj.getAsJsonArray("images");
                if (images.size() > 0) {
                    JsonObject imageObj = images.get(0).getAsJsonObject(); // Get the first image
                    resultObject.addProperty("image_url", imageObj.get("url").getAsString());
                }

                // Extract external Spotify URL
                JsonObject externalUrls = artistObj.getAsJsonObject("external_urls");
                resultObject.addProperty("spotify_url", externalUrls.get("spotify").getAsString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultObject; // Return the JsonObject with the artist's information
    }

    public static String searchForArtistId(String artistName, String accessToken) {
        try {
            String url = "https://api.spotify.com/v1/search?q=" + artistName + "&type=artist&limit=1";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
            JsonArray artists = jsonObject.getAsJsonObject("artists").getAsJsonArray("items");

            if (artists.size() > 0) {
                JsonObject artistObj = artists.get(0).getAsJsonObject();
                String artistId = artistObj.get("id").getAsString();
                System.out.println("Artist found: " + artistObj.get("name").getAsString());
                return artistId;
            } else {
                System.out.println("No artist found with the name: " + artistName);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getAccessToken(String clientId, String clientSecret) {
        try {
            URL url = new URL("https://accounts.spotify.com/api/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.getOutputStream().write("grant_type=client_credentials".getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            return jsonObject.get("access_token").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
