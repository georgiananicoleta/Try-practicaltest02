package ro.pub.cs.systems.eim.practicaltest02;

import android.provider.SyncStateContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {
    Socket socket;
    ServerThread serverThread;
    HashMap<String, WeatherForecastInformation> data;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    public synchronized void setData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public synchronized HashMap<String, WeatherForecastInformation> getData() {
        return data;
    }

    public void run() {
        if (socket == null) {
            Log.e("TAG", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("TAG", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("TAG", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String city = bufferedReader.readLine();
            String informationType = bufferedReader.readLine();
            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e("TAG", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            HashMap<String, WeatherForecastInformation> data = serverThread.getData();
            WeatherForecastInformation responseData = null;
            String result = null;
            WeatherForecastInformation weatherForecastInformation = null;
            if (data.containsKey(city)) {
                Log.i("TAG", "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherForecastInformation = data.get(city);
            } else {
                Log.i("TAG", "[COMMUNICATION THREAD] Getting the information from the webservice...");

                HttpClient httpClient = new DefaultHttpClient();
                Log.v("HELLO", city);
                HttpGet httpPost = new HttpGet("https://samples.openweathermap.org/data/2.5/weather?" + "q=" + city + "&" + "appid=" + "b6907d289e10d714a6e88b30761fae22");
                List<NameValuePair> params = new ArrayList<>();
                //params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
                //UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                //httpPost.setEntity(urlEncodedFormEntity);
                Log.v("HELLO", "saaaaaaaaluuuut");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e("TAG", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);
                Elements elements = element.getElementsByTag("body");
                /*for (Element script: elements) {
                    String scriptData = script.data();
                    if (scriptData.contains(Constants.SEARCH_KEY)) {
                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                        scriptData = scriptData.substring(position);
                        JSONObject content = new JSONObject(scriptData);
                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
                        String condition = currentObservation.getString(Constants.CONDITION);
                        String pressure = currentObservation.getString(Constants.PRESSURE);
                        String humidity = currentObservation.getString(SyncStateContract.Constants.HUMIDITY);
                        weatherForecastInformation = new WeatherForecastInformation(
                                temperature, windSpeed, condition, pressure, humidity
                        );
                        serverThread.setData(city, weatherForecastInformation);
                        break;
                    }
                }*/
                Log.d("Tag elements", elements.text());
                JSONObject jsonData = new JSONObject(elements.text());
                JSONObject querryData = jsonData.getJSONObject("main");
                Log.d("temp", querryData.getString("temp"));
                responseData = new WeatherForecastInformation(querryData.getString("temp"), querryData.getString("humidity"));
                serverThread.setData(city, responseData);
            }
            /*if (weatherForecastInformation == null) {
                Log.e("TAG", "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }*/
            result = null;
            /*switch(informationType) {
                case SyncStateContract.Constants.ALL:
                    result = weatherForecastInformation.toString();
                    break;
                case SyncStateContract.Constants.TEMPERATURE:
                    result = weatherForecastInformation.getTemperature();
                    break;
                case Constants.WIND_SPEED:
                    result = weatherForecastInformation.getWindSpeed();
                    break;
                case Constants.CONDITION:
                    result = weatherForecastInformation.getCondition();
                    break;
                case Constants.HUMIDITY:
                    result = weatherForecastInformation.getHumidity();
                    break;
                case Constants.PRESSURE:
                    result = weatherForecastInformation.getPressure();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }*/
            if (informationType.equals("temperature")) {
                // we set just the temperature
                result = responseData.queryResponse1;

            } else

            if (informationType.equals("humidity")) {
                result = responseData.queryResponse2;
            }


            // Send the data to the client
            printWriter.println(result);
            printWriter.flush();
            printWriter.println(responseData.queryResponse1);
            printWriter.flush();

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("TAG", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } /*catch (JSONException jsonException) {
            Log.e("TAG", "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("TAG", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }*/
    }
}
