package org.foi.nwtis.karsimuno.rest.klijenti;

import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.karsimuno.podaci.MeteoPodaci;
import org.foi.nwtis.karsimuno.podaci.MeteoPrognoza;

/**
 *
 * @author Karlo
 */
public class OWMKlijent {

    Client client;
    String apiKey;
    OWMRESTHelper helper;
    
    public OWMKlijent(String apiKey) {
        this.apiKey = apiKey;
        helper = new OWMRESTHelper(apiKey);
        client = ClientBuilder.newClient();
    }

    public MeteoPrognoza[] getWeatherForecast(int id, String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Forecast_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);

        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);

        System.out.println(odgovor);

        JsonReader reader = Json.createReader(new StringReader(odgovor));

        JsonObject jo1 = reader.readObject();
        JsonArray ja = jo1.getJsonArray("list");
        MeteoPrognoza[] mp = new MeteoPrognoza[ja.size()];

        for (int i = 0; i < ja.size(); i++) {
            JsonObject jo = ja.getJsonObject(i);
            Date day = new Date(jo.getJsonNumber("dt").longValueExact() * 1000);

            MeteoPodaci meteoPodatak = new MeteoPodaci();
            meteoPodatak.setSunRise(new Date());
            meteoPodatak.setSunSet(new Date());

            meteoPodatak.setTemperatureValue(new Double(jo.getJsonObject("main").getJsonNumber("temp").doubleValue()).floatValue());
            meteoPodatak.setTemperatureMin(new Double(jo.getJsonObject("main").getJsonNumber("temp_min").doubleValue()).floatValue());
            meteoPodatak.setTemperatureMax(new Double(jo.getJsonObject("main").getJsonNumber("temp_max").doubleValue()).floatValue());
            meteoPodatak.setTemperatureUnit("°C");

            meteoPodatak.setHumidityValue(new Double(jo.getJsonObject("main").getJsonNumber("humidity").doubleValue()).floatValue());
            meteoPodatak.setHumidityUnit("%");

            meteoPodatak.setPressureValue(new Double(jo.getJsonObject("main").getJsonNumber("pressure").doubleValue()).floatValue());
            meteoPodatak.setPressureUnit("hPa");

            meteoPodatak.setWindSpeedValue(new Double(jo.getJsonObject("wind").getJsonNumber("speed").doubleValue()).floatValue());
            meteoPodatak.setWindSpeedName("");

            meteoPodatak.setWindDirectionValue(0.0f);
            meteoPodatak.setWindDirectionCode("");
            meteoPodatak.setWindDirectionName("");

            meteoPodatak.setCloudsValue(jo.getJsonObject("clouds").getInt("all"));
            meteoPodatak.setCloudsName(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            meteoPodatak.setPrecipitationMode("");

            meteoPodatak.setWeatherNumber(jo.getJsonArray("weather").getJsonObject(0).getInt("id"));
            meteoPodatak.setWeatherValue(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            meteoPodatak.setWeatherIcon(jo.getJsonArray("weather").getJsonObject(0).getString("icon"));
            mp[i] = new MeteoPrognoza(id, day.getDay(), meteoPodatak);
        }

        return mp;
    }

    public MeteoPodaci getRealTimeWeather(String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Current_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);

        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JsonReader reader = Json.createReader(new StringReader(odgovor));

            JsonObject jo = reader.readObject();

            MeteoPodaci mp = new MeteoPodaci();
            mp.setSunRise(new Date(jo.getJsonObject("sys").getJsonNumber("sunrise").bigDecimalValue().longValue() * 1000));
            mp.setSunSet(new Date(jo.getJsonObject("sys").getJsonNumber("sunset").bigDecimalValue().longValue() * 1000));

            mp.setTemperatureValue(new Double(jo.getJsonObject("main").getJsonNumber("temp").doubleValue()).floatValue());
            mp.setTemperatureMin(new Double(jo.getJsonObject("main").getJsonNumber("temp_min").doubleValue()).floatValue());
            mp.setTemperatureMax(new Double(jo.getJsonObject("main").getJsonNumber("temp_max").doubleValue()).floatValue());
            mp.setTemperatureUnit("celsius");

            mp.setHumidityValue(new Double(jo.getJsonObject("main").getJsonNumber("humidity").doubleValue()).floatValue());
            mp.setHumidityUnit("%");

            mp.setPressureValue(new Double(jo.getJsonObject("main").getJsonNumber("pressure").doubleValue()).floatValue());
            mp.setPressureUnit("hPa");

            mp.setWindSpeedValue(new Double(jo.getJsonObject("wind").getJsonNumber("speed").doubleValue()).floatValue());
            mp.setWindSpeedName("");

            mp.setWindDirectionValue(new Double(jo.getJsonObject("wind").getJsonNumber("deg").doubleValue()).floatValue());
            mp.setWindDirectionCode("");
            mp.setWindDirectionName("");

            mp.setCloudsValue(jo.getJsonObject("clouds").getInt("all"));
            mp.setCloudsName(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            mp.setPrecipitationMode("");

            mp.setWeatherNumber(jo.getJsonArray("weather").getJsonObject(0).getInt("id"));
            mp.setWeatherValue(jo.getJsonArray("weather").getJsonObject(0).getString("description"));
            mp.setWeatherIcon(jo.getJsonArray("weather").getJsonObject(0).getString("icon"));

            mp.setLastUpdate(new Date(jo.getJsonNumber("dt").bigDecimalValue().longValue() * 1000));
            return mp;

        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
