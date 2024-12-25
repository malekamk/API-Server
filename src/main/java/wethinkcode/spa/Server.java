package wethinkcode.spa;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import static wethinkcode.spa.spotifyapp.*;

public class Server {
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY;

    private final Javalin appServer;

    public Server() {
        appServer = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
            config.defaultContentType = "application/json";
        });

        this.appServer.get("/health", ctx -> {
            ctx.status(200).result(
                    "server: Running...");
        });

        this.appServer.post("/chat", Server::chatHandler);
        this.appServer.get("/api/TopSongs/{artistName}",context -> {
            String artistName = context.pathParam("artistName");
            String accessToken = getAccessToken(CLIENT_ID, CLIENT_SECRET); // Get the access token

            String artistId = searchForArtistId(artistName, accessToken); // Get the artist ID
            if (artistId != null) {
                JsonObject topTracksResponse = getTopTracks(artistId, accessToken);
                String jsonResponse = new Gson().toJson(topTracksResponse);

                context.json(jsonResponse); // Return the image URLs as JSON
            } else {
                context.status(HttpCode.NOT_FOUND).result("Artist not found");
            }
        });
        this.appServer.get("/api/Songs/{song}",context -> {
            String song = context.pathParam("song");
            String accessToken = getAccessToken(CLIENT_ID, CLIENT_SECRET);
            if (song != null) {


                JsonObject topTracksResponse = searchForSongs(song,accessToken);
                String jsonResponse = new Gson().toJson(topTracksResponse);

                context.json(jsonResponse); // Return the image URLs as JSON
            } else {
                context.status(HttpCode.NOT_FOUND).result("Song not found");
            }
        });

    }

    public static void main(String[] args) {
        Server server = new Server();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT","5050"));

        server.start(port);
    }

    public void start(int port) {
        this.appServer.start(port);

    }

    private static void chatHandler(Context context){
        try {
            // Parse the JSON body
            JSONObject body = new JSONObject(context.body());
            String message = body.getString("message");

            // Prepare API request
            JSONObject json = new JSONObject();
            json.put("contents", new JSONObject[]{
                    new JSONObject().put("parts", new JSONObject[]{new JSONObject().put("text", message)})
            });

            // Make the API request
            String botResponse = sendPostRequest(API_URL, json.toString()).replace("*","");
            System.out.println(botResponse+"received");

            context.status(HttpCode.OK);

            context.result(botResponse);
        } catch (Exception e) {
            context.status(500).json(new JSONObject().put("error", e.getMessage()));
        }
    }

    private static String sendPostRequest(String urlString, String jsonInputString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Send the JSON input
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Parse the response to extract the bot's message
        JSONObject responseJson = new JSONObject(response.toString());
        String botresponse =  responseJson.getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts").getJSONObject(0)
                .getString("text");

        return botresponse;
    }

    public void stop() {
        this.appServer.stop();
    }

    public int port() {
        return appServer.port();
    }
}
