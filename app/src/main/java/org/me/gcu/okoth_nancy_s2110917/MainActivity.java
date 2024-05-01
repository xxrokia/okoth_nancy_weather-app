//Student Name:Nancy Okoth
//Student ID: S2110917

package org.me.gcu.okoth_nancy_s2110917;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SearchView searchBar;
    private TextView currentLocationLabel;
    private TextView currentDateLabel;
    private TextView currentTemperatureLabel;
    private TextView currentWeatherLabel;
    private TextView weatherDescription;
    private TextView maxTemperature;
    private TextView minTemperature;
    private TextView windDirection;
    private TextView humidity;
    private TextView pressure;
    private TextView windSpeed;
    private TextView visibility;
    private TextView pollution;
    private TextView uvRisk;

    private Handler uiHandler = new Handler();

    // Mapping of location names to their weather API URLs
    private static final Map<String, String> LOCATION_URLS = new HashMap<>();
    static {
        LOCATION_URLS.put("Glasgow", "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2648579");
        LOCATION_URLS.put("London", "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643743");
        LOCATION_URLS.put("New York", "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/5128581");
        LOCATION_URLS.put("Oman", "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/287286");
        LOCATION_URLS.put("Mauritius", "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/934154");
        LOCATION_URLS.put("Bangladesh", "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/1185241"); // Corrected URL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        searchBar = findViewById(R.id.searchBar);
        currentLocationLabel = findViewById(R.id.currentLocationLabel);
        currentDateLabel = findViewById(R.id.currentDateLabel);
        currentTemperatureLabel = findViewById(R.id.currentTemperatureLabel);
        currentWeatherLabel = findViewById(R.id.currentWeatherLabel);
        weatherDescription = findViewById(R.id.weatherDescription);
        maxTemperature = findViewById(R.id.maxTemperature);
        minTemperature = findViewById(R.id.minTemperature);
        windDirection = findViewById(R.id.windDirection);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        windSpeed = findViewById(R.id.windSpeed);
        visibility = findViewById(R.id.visibility);
        pollution = findViewById(R.id.pollution);
        uvRisk = findViewById(R.id.uvRisk);



        // Set the query hint for the SearchView
        searchBar.setQueryHint("Enter location");

        // Add query listener to SearchView
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearchQuery(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false; // Optional: implement live suggestions
            }
        });
    }

    private void handleSearchQuery(String query) {
        if (LOCATION_URLS.containsKey(query)) {
            String url = LOCATION_URLS.get(query);
            fetchWeatherData(url);
        } else {
            updateWeatherDisplay("Location not found. Please enter a predefined location.");
        }
    }

    private void fetchWeatherData(String urlSource) {
        new Thread(() -> {
            try {
                String rawXmlData = fetchRawXmlData(urlSource);
                if (rawXmlData != null) {
                    String parsedWeatherData = parseWeatherXmlData(rawXmlData);
                    updateWeatherDisplay(parsedWeatherData);
                }
            } catch (Exception e) {
                Log.e("WeatherFetchError", "Error fetching weather data", e);
                updateWeatherDisplay("Error fetching weather data.");
            }
        }).start();
    }

    private String fetchRawXmlData(String urlSource) throws IOException {
        URL weatherUrl = new URL(urlSource);
        URLConnection connection = weatherUrl.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder data = new StringBuilder();
        String inputLine;

        while ((inputLine = reader.readLine()) != null) {
            data.append(inputLine).append("\n");
        }

        reader.close();
        return data.toString();
    }

    private String parseWeatherXmlData(String rawXmlData) {
        StringBuilder parsedData = new StringBuilder();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new InputStreamReader(new java.io.ByteArrayInputStream(rawXmlData.getBytes())));

            int eventType = parser.getEventType();

            String location = "";
            String date = "";
            String temperature = "";
            String weatherCondition = "";
            String maxTemp = "";
            String minTemp = "";
            String windDir = "";
            String humid = "";
            String press = "";
            String windSp = "";
            String visib = "";
            String poll = "";
            String uvR = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case "title":
                            location = parser.nextText();
                            break;
                        case "description":
                            // You can parse the description to extract various weather attributes
                            weatherCondition = parser.nextText();
                            break;
                        case "pubDate":
                            date = parser.nextText();
                            break;
                        // Further parsing based on your data structure
                    }
                }

                eventType = parser.next();
            }

            parsedData.append("Location: ").append(location).append("\n");
            parsedData.append("Date: ").append(date).append("\n");
            parsedData.append("Weather: ").append(weatherCondition).append("\n");
            // Add other parsed data elements to this parsedData

        } catch (Exception e) {
            Log.e("XmlParseError", "Error parsing weather data", e);
            parsedData.append("Error parsing weather data.");
        }

        return parsedData.toString();
    }

    private void updateWeatherDisplay(String parsedWeatherData) {
        uiHandler.post(() -> {
            currentWeatherLabel.setText(parsedWeatherData); // Example of setting data to a TextView
            // Use other TextViews to represent various parts of the parsed data
        });
    }

    public TextView getCurrentLocationLabel() {
        return currentLocationLabel;
    }

    public void setCurrentLocationLabel(TextView currentLocationLabel) {
        this.currentLocationLabel = currentLocationLabel;
    }

    public TextView getCurrentDateLabel() {
        return currentDateLabel;
    }

    public void setCurrentDateLabel(TextView currentDateLabel) {
        this.currentDateLabel = currentDateLabel;
    }
}
