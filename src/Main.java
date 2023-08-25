import com.google.gson.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

class Passenger
{
    private static final DecimalFormat df = new DecimalFormat("0.000");
    public String name;
    public int id;
    public double cooridinateA1, cooridinateA2, cooridinateB1, cooridinateB2;
    public Passenger(String name, int id, double cooridinateA1, double cooridinateA2, double cooridinateB1, double cooridinateB2)
    {
        this.name = name;
        this.id = id;
        this.cooridinateA1 = cooridinateA1;
        this.cooridinateA2 = cooridinateA2;
        this.cooridinateB1 = cooridinateB1;
        this.cooridinateB2 = cooridinateB2;
    }
    public static Passenger CreateClient(int ClientId)
    {
        String name = "Client"+ClientId;
        int id = ClientId;
        double coordinateA1 = getRandomEcoordinate();
        double cooridinateA2 = getRandomNcoordinate();
        double cooridinateB1 = getRandomEcoordinate();
        double cooridinateB2 = getRandomNcoordinate();

        return new Passenger(name , id , coordinateA1, cooridinateA2, cooridinateB1, cooridinateB2);
    }
    public Passenger PrintClient()
    {
        System.out.println("name = " + this.name);
        System.out.println("id = " + this.id);
        System.out.println("coordinateA1 = " + this.cooridinateA1);
        System.out.println("cooridinateA2 = " + this.cooridinateA2);
        System.out.println("cooridinateB1 = " + this.cooridinateB1);
        System.out.println("cooridinateB2 = " + this.cooridinateB2);

        return null;
    }
    public static double getRandomNcoordinate() {
        double min = 62.028;
        double max = 62.038;
        double coordinate = ((Math.random() * (max - min)) + min);
        BigDecimal rounded = new BigDecimal(coordinate);
        BigDecimal rounded1 = rounded.setScale(7, RoundingMode.HALF_UP);
        return rounded1.doubleValue();
    }
    public static double getRandomEcoordinate() {
        double min = 129.727;
        double max = 129.762;
        double coordinate = ((Math.random() * (max - min)) + min);
        BigDecimal rounded = new BigDecimal(coordinate);
        BigDecimal rounded1 = rounded.setScale(7, RoundingMode.HALF_UP);
        return rounded1.doubleValue();
    }
}


public class Main {
    public static void main(String[] args) {

        //создаю "Водителя", использую класс клиента, т.к. создавать
        //отдельный класс в данном случае излишне, у него id = 0

        Passenger Driver = Passenger.CreateClient(0);

        //создаю массив из 5 клиентов, id начинаются с 1
        Passenger[] arrayOfPassengers = new Passenger[5];
        for (int i = 1; i < arrayOfPassengers.length; i++)
        {
            arrayOfPassengers[i] = Passenger.CreateClient(i);
        }

        //Использую бесплатное API openrouteservice

        /**/
        Client client = ClientBuilder.newClient();
        Entity<String> payload = Entity.json("{\"locations\":[[9.70093,48.477473],[9.207916,49.153868],[37.573242,55.801281],[115.663757,38.106467]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");
        Response response = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(payload);

        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + prettyPrintUsingGson(response.readEntity(String.class)));

    }


    //Это для чтобы вывод JSONa был приятнее
    public static String prettyPrintUsingGson(String uglyJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJson);
        String prettyJsonString = gson.toJson(jsonElement);
        return prettyJsonString;
    }
}