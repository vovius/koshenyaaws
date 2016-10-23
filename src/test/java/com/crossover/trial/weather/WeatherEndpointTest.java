package com.crossover.trial.weather;

import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.rest.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.rest.RestWeatherQueryEndpoint;
import com.crossover.trial.weather.rest.WeatherCollectorEndpoint;
import com.crossover.trial.weather.rest.WeatherQueryEndpoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class WeatherEndpointTest {

    private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();

    private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();

    private Gson _gson = new Gson();

    private DataPoint _dp;
    @Before
    public void setUp() throws Exception {
        RestWeatherQueryEndpoint.init();
        _dp = new DataPoint.Builder()
                .count(10).first(10).median(20).last(30).mean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
        _query.weather("BOS", "0").getEntity();
    }

    @Test
    public void testPing() throws Exception {
        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), _dp);
    }

    @Test
    public void testGetNearby() throws Exception {
        // check datasize response
        _update.updateWeather("JFK", "wind", _gson.toJson(_dp));
        _dp.setMean(40);
        _update.updateWeather("EWR", "wind", _gson.toJson(_dp));
        _dp.setMean(30);
        _update.updateWeather("LGA", "wind", _gson.toJson(_dp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
        assertEquals(3, ais.size());
    }

    @Test
    public void testUpdate() throws Exception {

        DataPoint windDp = new DataPoint.Builder()
                .count(10).first(10).median(20).last(30).mean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
        _query.weather("BOS", "0").getEntity();

        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint cloudCoverDp = new DataPoint.Builder()
                .count(4).first(10).median(60).last(100).mean(50).build();
        _update.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), windDp);
        assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
    }

}