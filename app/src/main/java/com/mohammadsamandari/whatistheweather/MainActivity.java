package com.mohammadsamandari.whatistheweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    public class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... cityName) {
            Log.i("Lord", "doInBackground begin");

            //  Getting the City Name and Converting the right url to download
            String myUrlAsString = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName[0] + "&appid=740f4053ef70a0b0281155f7e2c9e8ce";

            //  Creating Url and HttpConnection Vaiables.
            URL url;
            HttpURLConnection connection;
            String result = "";

            //  Defining Url and Download Process
            try {
                Log.i("Lord", "Connection begin");

                url = new URL(myUrlAsString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
                InputStreamReader reader = new InputStreamReader(stream);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                connection.disconnect();
                Log.i("Lord", "Connection Ended - Returning Result");
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Lord", String.valueOf(e));
                return "MalformedURLException";
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Lord", String.valueOf(e));
                return "IOException";
            }
        }

        @Override
        protected void onPostExecute(String weatherInfo) {
            Log.i("Lord", "onPostExecute Begin");
            super.onPostExecute(weatherInfo);
            try {
                //Converting result To Json and extracting Parts of it.
                JSONObject myJSON = new JSONObject(weatherInfo);

                //  Checking if the City is found:
                int resultCod = Integer.parseInt(myJSON.getString("cod"));
                if (resultCod == 200) {
                    Log.i("Lord", "resultCod Is 200 - Continuing . . .");

                    //Getting The Information We Need.
                    String weatherExtracted = myJSON.getString("weather");
                    String sysExtracted = myJSON.getString("sys");
                    String cityName = myJSON.getString("name");

                    //  Getting Weather Information out of the Json File.
                    JSONArray weatherJsonArray = new JSONArray(weatherExtracted);
                    JSONObject weatherMainJson = weatherJsonArray.getJSONObject(0);
                    String weatherMain = weatherMainJson.getString("main");
                    String weatherDescription = weatherMainJson.getString("description");

                    //  Getting Country Name out of Json File.
                    JSONObject sysJson = new JSONObject(sysExtracted);
                    String countryName = sysJson.getString("country");

                    //Creating a Map object to pass to UI
                    Map weatherInfoMap = new HashMap();
                    weatherInfoMap.put("countryName", countryName);
                    weatherInfoMap.put("cityName", cityName);
                    weatherInfoMap.put("weatherMain", weatherMain);
                    weatherInfoMap.put("weatherDescription", weatherDescription);

                    Log.i("Lord", "Json Function Ended - Updating ui Function");
                    //Update UI
                    updateUi(weatherInfoMap);
                } else if (resultCod != 200) {
                    Log.i("Lord", "resultCod Is 404 - Continuing . . .");
                    String message = myJSON.getString("message");

                    updateUiWithError(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Lord", String.valueOf(e));
                updateUiWithError("City Not Found - Please Try Again.");
            }

            Button btnSearch=findViewById(R.id.btnSearch);
            btnSearch.setEnabled(true);
        }
    }

    private void updateUiWithError(String message) {
        TextView txtWeather = findViewById(R.id.txtWeather);
        txtWeather.setText(message);
        Log.i("Lord", "updateUiWithError Ended");
    }

    private void updateUi(Map weatherInfoMap) {
        TextView txtWeather = findViewById(R.id.txtWeather);
        String weatherInfo = "";
        weatherInfo += weatherInfoMap.get("cityName");
        weatherInfo += " - ";
        weatherInfo += weatherInfoMap.get("countryName");
        weatherInfo += "\n";
        weatherInfo += weatherInfoMap.get("weatherMain");
        weatherInfo += "\n";
        weatherInfo += weatherInfoMap.get("weatherDescription");
        txtWeather.setText(weatherInfo);
        Log.i("Lord", "updateUi Ended");
    }

    public void btnSearchFunction(View view) {
        view.setEnabled(false);

        TextView txtWeather = findViewById(R.id.txtWeather);
        txtWeather.setText("Searching . . . Please Wait!");

        EditText edtCity = findViewById(R.id.edtCityName);

        GetWeatherTask getWeatherTask = new GetWeatherTask();
        getWeatherTask.execute(edtCity.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
