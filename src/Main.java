import com.google.gson.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    public static void main(String[] args) throws ParseException {

        //создаю "Водителя", использую класс клиента, т.к. создавать
        //отдельный класс в данном случае излишне, у него id = 0

        Passenger Driver = Passenger.CreateClient(0);
        Driver.PrintClient();

        //создаю массив из 5 клиентов, id начинаются с 1
        Passenger[] arrayOfPassengers = new Passenger[5];
        for (int i = 1; i < arrayOfPassengers.length; i++)
        {
            arrayOfPassengers[i] = Passenger.CreateClient(i);
            arrayOfPassengers[i].PrintClient();
        }

        //В дальнейшем идут вычислительные работы с ипользованием бесплатного API openrouteservice





        //Вычисление пути от точки А водителя до точки А пассажира  НАЧАЛО//

        //Ввожу стартовую точку водителя и стартовые точки пассажиров
        Client client = ClientBuilder.newClient();

        Entity<String> DriverAPassA = Entity.json("{\"locations\":[[" +
                Driver.cooridinateA1 + "," + Driver.cooridinateA2 + "],[" +
                arrayOfPassengers[1].cooridinateA1 + "," + arrayOfPassengers[1].cooridinateA2 + "],[" +
                arrayOfPassengers[2].cooridinateA1 + "," + arrayOfPassengers[2].cooridinateA2 + "],[" +
                arrayOfPassengers[3].cooridinateA1 + "," + arrayOfPassengers[3].cooridinateA2 + "],[" +
                arrayOfPassengers[4].cooridinateA1 + "," + arrayOfPassengers[4].cooridinateA2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");

        //Отправляется запрос в API
        Response response = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(DriverAPassA);

        //Читаю файл

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.readEntity(String.class));
        JSONObject jsonObj = (JSONObject) obj;
        JSONArray durationsFromAtoA = (JSONArray) jsonObj.get("durations");

        //API выдает таблицу данных, мне нужна всего 1 строка, так что урезаю остальную часть
        durationsFromAtoA = (JSONArray) durationsFromAtoA.get(0);

        System.out.println("durationsFromDriverAtoPassengerA = " + durationsFromAtoA);
        //В значении durationsFromAtoA хранятся длительность поездки от точки А водителя ко всем пассажирам


        //Вычисление пути от точки А водителя до точки А пассажира  КОНЕЦ//




        //Теперь необходимо вычислить длительность всех маршрутов пассажиров, т.е. точка А пассажира - точка Б пассажира

        //Вычисление пути пассажира 1  НАЧАЛО//

        Entity<String> PassAPassB1 = Entity.json("{\"locations\":[[" +
                arrayOfPassengers[1].cooridinateA1 + "," + arrayOfPassengers[1].cooridinateA2 + "],[" +
                arrayOfPassengers[1].cooridinateB1 + "," + arrayOfPassengers[1].cooridinateB2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");

        Response response1 = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(PassAPassB1);

        JSONParser parser1 = new JSONParser();
        Object obj1 = parser1.parse(response1.readEntity(String.class));
        JSONObject jsonObj1 = (JSONObject) obj1;
        JSONArray durationsFromPassAtoPassB1 = (JSONArray) jsonObj1.get("durations");
        durationsFromPassAtoPassB1 = (JSONArray) durationsFromPassAtoPassB1.get(0);

        System.out.println("durationsFromPassAtoPassB1 = " + durationsFromPassAtoPassB1);

        //Вычисление пути пассажира 1  КОНЕЦ//







        //Вычисление пути пассажира 2  НАЧАЛО//

        Entity<String> PassAPassB2 = Entity.json("{\"locations\":[[" +
                arrayOfPassengers[2].cooridinateA1 + "," + arrayOfPassengers[2].cooridinateA2 + "],[" +
                arrayOfPassengers[2].cooridinateB1 + "," + arrayOfPassengers[2].cooridinateB2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");

        Response response2 = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(PassAPassB2);

        JSONParser parser2 = new JSONParser();
        Object obj2 = parser2.parse(response2.readEntity(String.class));
        JSONObject jsonObj2 = (JSONObject) obj2;
        JSONArray durationsFromPassAtoPassB2 = (JSONArray) jsonObj2.get("durations");
        durationsFromPassAtoPassB2 = (JSONArray) durationsFromPassAtoPassB2.get(0);

        System.out.println("durationsFromPassAtoPassB2 = " + durationsFromPassAtoPassB2);

        //Вычисление пути пассажира 2  КОНЕЦ//







        //Вычисление пути пассажира 3  НАЧАЛО//

        Entity<String> PassAPassB3 = Entity.json("{\"locations\":[[" +
                arrayOfPassengers[3].cooridinateA1 + "," + arrayOfPassengers[3].cooridinateA2 + "],[" +
                arrayOfPassengers[3].cooridinateB1 + "," + arrayOfPassengers[3].cooridinateB2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");

        Response response3 = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(PassAPassB3);

        JSONParser parser3 = new JSONParser();
        Object obj3 = parser3.parse(response3.readEntity(String.class));
        JSONObject jsonObj3 = (JSONObject) obj3;
        JSONArray durationsFromPassAtoPassB3 = (JSONArray) jsonObj3.get("durations");
        durationsFromPassAtoPassB3 = (JSONArray) durationsFromPassAtoPassB3.get(0);

        System.out.println("durationsFromPassAtoPassB3 = " + durationsFromPassAtoPassB3);

        //Вычисление пути пассажира 3  КОНЕЦ//






        //Вычисление пути пассажира 4  НАЧАЛО//

        Entity<String> PassAPassB4 = Entity.json("{\"locations\":[[" +
                arrayOfPassengers[4].cooridinateA1 + "," + arrayOfPassengers[4].cooridinateA2 + "],[" +
                arrayOfPassengers[4].cooridinateB1 + "," + arrayOfPassengers[4].cooridinateB2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");

        Response response4 = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(PassAPassB4);

        JSONParser parser4 = new JSONParser();
        Object obj4 = parser4.parse(response4.readEntity(String.class));
        JSONObject jsonObj4 = (JSONObject) obj4;
        JSONArray durationsFromPassAtoPassB4 = (JSONArray) jsonObj4.get("durations");
        durationsFromPassAtoPassB4 = (JSONArray) durationsFromPassAtoPassB4.get(0);

        System.out.println("durationsFromPassAtoPassB4 = " + durationsFromPassAtoPassB4);

        //Вычисление пути пассажира 4  КОНЕЦ//


        //Теперь осталось высчитать путь от точки Б клиентов в точку Б водителя//




        //Вычисление пути от точки Б водителя до точки Б пассажира  НАЧАЛО//

        Entity<String> DriverBPassB = Entity.json("{\"locations\":[[" +
                Driver.cooridinateB1 + "," + Driver.cooridinateB2 + "],[" +
                arrayOfPassengers[1].cooridinateB1 + "," + arrayOfPassengers[1].cooridinateB2 + "],[" +
                arrayOfPassengers[2].cooridinateB1 + "," + arrayOfPassengers[2].cooridinateB2 + "],[" +
                arrayOfPassengers[3].cooridinateB1 + "," + arrayOfPassengers[3].cooridinateB2 + "],[" +
                arrayOfPassengers[4].cooridinateB1 + "," + arrayOfPassengers[4].cooridinateB2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");

        Response response5 = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(DriverBPassB);

        JSONParser parser5 = new JSONParser();
        Object obj5 = parser5.parse(response5.readEntity(String.class));
        JSONObject jsonObj5 = (JSONObject) obj5;
        JSONArray durationsFromDriverBtoPassB = (JSONArray) jsonObj5.get("durations");
        durationsFromDriverBtoPassB = (JSONArray) durationsFromDriverBtoPassB.get(0);

        System.out.println("durationsFromDriverBtoPassengerB = " + durationsFromDriverBtoPassB);

        //Вычисление пути от точки Б водителя до точки Б пассажира  КОНЕЦ//


        //Вычисление длительности пути для всех маршрутов и поиск лучшего
        //Задаю значение по-умолчанию для лучшего пути
        int optimalrout = 1;
        double optimalduration = (double) durationsFromAtoA.get(1) + (double) durationsFromPassAtoPassB1.get(1) + (double) durationsFromDriverBtoPassB.get(1);

        for (int i = 1; i < durationsFromDriverBtoPassB.size(); i++) {
            double sum = (double) durationsFromAtoA.get(i) + (double) durationsFromPassAtoPassB1.get(1) + (double) durationsFromDriverBtoPassB.get(i);
            System.out.println("duration of route " + i + " = " + sum);
            if (sum<optimalduration) {
                optimalrout=i;
                optimalduration=sum;
            }
        }
        System.out.println("optimal route - " + optimalrout + ", duration - " + optimalduration + " seconds");

        /*
        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + prettyPrintUsingGson(response.readEntity(String.class)));
        */
    }


    //Это для чтобы вывод JSONa был приятнее
    public static String prettyPrintUsingGson(String uglyJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJson);
        String prettyJsonString = gson.toJson(jsonElement);
        return prettyJsonString;
    }

}