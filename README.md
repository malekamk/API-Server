# 🎶 Music Chatbot API

This project implements a **Music  and Chatbot** using **Javalin**, **Spotify API**, and **Google Gemini API** to interact with users and provide music-related information such as top songs by an artist and song details. It includes features like chat functionality with a bot, and integration with the **Spotify API** to fetch top songs of an artist and song search functionality.

The backend is containerized using **Docker** and deployed on **Render**, ensuring that the API is always available online.


# Deployment on Render

The backend is deployed on Render for high availability. You can access the deployed version of the API at:

    Deployed API URL: https://portfolio-a1v7.onrender.com/

## Features
- **/chat**: A chat endpoint where users can send a message, and the bot will respond using **Google Gemini API**.
- **/api/TopSongs/{artistName}**: Get the top songs for a given artist by their name using the **Spotify API**.
- **/api/Songs/{song}**: Search for a specific song and get details about it from **Spotify**.
- **Health Check**: A simple endpoint (`/health`) to check if the server is running.

## Technologies Used
- **Javalin**: A lightweight web framework for building the API server.
- **Spotify API**: For accessing music-related data like top tracks of artists.
- **Google Gemini API**: For chat functionality powered by AI.
- **JSON**: For request and response formats.
- **Gson**: For handling JSON serialization and deserialization.
- **Docker**: For containerizing the backend.
- **Render**: For deploying the application to ensure high availability.

### Prerequisites
- **Java 11 or higher**
- **Spotify Developer API Key**: You'll need to create an account on the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard/applications) and get your **Client ID** and **Client Secret**.
- **Google Gemini API Key**: Sign up for **Google Gemini API** and obtain an API key.

### Steps
