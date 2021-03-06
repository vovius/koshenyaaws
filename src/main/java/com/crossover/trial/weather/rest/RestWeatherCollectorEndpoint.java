package com.crossover.trial.weather.rest;

import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Component
@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class);

    /** shared gson json to object factory */
    private final Gson gson = new Gson();

    @Autowired
    private RestWeatherQueryEndpoint restWeatherQueryEndpoint;

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK)
                .entity("ready").build();
    }

    @Override
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
        try {
            addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response getAirports() {
        Set<String> retval = restWeatherQueryEndpoint.getAirportData().stream().map(entry -> entry.getIata()).collect(Collectors.toSet());
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    @Override
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = restWeatherQueryEndpoint.findAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }


    @Override
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }
    //
    // Internal support methods
    //

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        int airportDataIdx = restWeatherQueryEndpoint.getAirportDataIdx(iataCode);
        AtmosphericInformation ai = restWeatherQueryEndpoint.getAtmosphericInformation().get(airportDataIdx);
        updateAtmosphericInformation(ai, pointType, dp);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp the actual data point
     */
    private void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException {
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());
        if (dptype == null)
            throw new IllegalStateException("couldn't update atmospheric data");

        switch (dptype) {
            case WIND :
                if (dp.getMean() >= 0) {
                    ai.setWind(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;

            case TEMPERATURE:
                if (dp.getMean() >= -50 && dp.getMean() < 100) {
                    ai.setTemperature(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;

            case HUMIDTY:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setHumidity(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;

            case PRESSURE:
                if (dp.getMean() >= 650 && dp.getMean() < 800) {
                    ai.setPressure(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;

            case CLOUDCOVER:
                if (dp.getMean() >= 0 && dp.getMean() < 100) {
                    ai.setCloudCover(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;

            case PRECIPITATION:
                if (dp.getMean() >=0 && dp.getMean() < 100) {
                    ai.setPrecipitation(dp);
                    ai.setLastUpdateTime(System.currentTimeMillis());
                }
                break;
        }

    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     */
    private AirportData addAirport(String iataCode, double latitude, double longitude) {
        AirportData ad = new AirportData();
        restWeatherQueryEndpoint.getAirportData().add(ad);

        AtmosphericInformation ai = new AtmosphericInformation();
        restWeatherQueryEndpoint.getAtmosphericInformation().add(ai);
        ad.setIata(iataCode);
        ad.setLatitude(latitude);
        ad.setLatitude(longitude);
        return ad;
    }
}
