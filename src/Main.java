import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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

        //В дальнейшем идут вычислительные работы с ипользованием API openrouteservice

        //Вычисление пути от точки А водителя до точки А пассажира НАЧАЛО//

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
        //в первом значении хранится время пути с точки само к себе, т.е. всегда 0.
        durationsFromAtoA.remove(0);
        System.out.println("durationsFromDriverAtoPassengerA = " + durationsFromAtoA);
        //В значении durationsFromAtoA хранятся длительность поездки от точки А водителя ко всем пассажирам


        //Вычисление пути от точки А водителя до точки А пассажира КОНЕЦ//

        //Теперь необходимо вычислить длительность всех маршрутов пассажиров, т.е. точка А пассажира - точка Б пассажира

        List<Entity<String>> PassengersAtoBList = new ArrayList<>();
        List<Response> responsesPassengerList = new ArrayList<>();
        List<Object> objectsList = new ArrayList<>();
        List<JSONObject> JSONObjectPassengerAtoPassengerBList = new ArrayList<>();
        List<JSONArray> JSONArrayPassengerAtoPassengerBList = new ArrayList<>();
        List durationFromPassengerAtoPassangerB = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Entity<String> responseAtoBatI = Entity.json("{\"locations\":[[" +
                    arrayOfPassengers[i+1].cooridinateA1 + "," + arrayOfPassengers[i+1].cooridinateA2 + "],[" +
                    arrayOfPassengers[i+1].cooridinateB1 + "," + arrayOfPassengers[i+1].cooridinateB2 + "]]," +
                    "\"metrics\":[\"duration\",\"distance\"]}");
            PassengersAtoBList.add(i, responseAtoBatI);

            Response responseAtI = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                    .request()
                    .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                    .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .post(PassengersAtoBList.get(i));;
            responsesPassengerList.add(i, responseAtI);

            objectsList.add(i,parser.parse(responseAtI.readEntity(String.class)));
            JSONObjectPassengerAtoPassengerBList.add(i,(JSONObject) objectsList.get(i));
            JSONArrayPassengerAtoPassengerBList.add(i, (JSONArray) JSONObjectPassengerAtoPassengerBList.get(i).get("durations"));
            JSONArray temp = (JSONArray) JSONArrayPassengerAtoPassengerBList.get(i).get(0);
            durationFromPassengerAtoPassangerB.add(i,temp.get(1));
            System.out.println("durationsFromPassAtoPassB " + i + " = " + durationFromPassengerAtoPassangerB.get(i));
        }

        //Теперь осталось высчитать путь от точки Б клиентов в точку Б водителя//

        //Вычисление пути от точки Б водителя до точки Б пассажира НАЧАЛО//

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

        Object obj5 = parser.parse(response5.readEntity(String.class));
        JSONObject jsonObj5 = (JSONObject) obj5;
        JSONArray durationsFromDriverBtoPassB = (JSONArray) jsonObj5.get("durations");
        durationsFromDriverBtoPassB = (JSONArray) durationsFromDriverBtoPassB.get(0);
        //в первом значении хранится время пути с точки само к себе, т.е. всегда 0.
        durationsFromDriverBtoPassB.remove(0);
        System.out.println("durationsFromDriverBtoPassengerB = " + durationsFromDriverBtoPassB);
        //Вычисление пути от точки Б водителя до точки Б пассажира КОНЕЦ//

        Entity<String> DriverADriverB = Entity.json("{\"locations\":[[" +
                Driver.cooridinateA1 + "," + Driver.cooridinateA2 + "],[" +
                Driver.cooridinateB1 + "," + Driver.cooridinateB2 + "]]," +
                "\"metrics\":[\"duration\",\"distance\"]}");
        Response response6 = client.target("https://api.openrouteservice.org/v2/matrix/driving-car")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248789dcd5238624851bb77937a910c3660")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(DriverADriverB);

        Object obj6 = parser.parse(response6.readEntity(String.class));
        JSONObject jsonObj6 = (JSONObject) obj6;
        JSONArray durationsFromDriverAtoDriverB = (JSONArray) jsonObj6.get("durations");
        durationsFromDriverAtoDriverB = (JSONArray) durationsFromDriverAtoDriverB.get(0);
        //в первом значении хранится время пути с точки само к себе, т.е. всегда 0.
        durationsFromDriverAtoDriverB.remove(0);
        System.out.println("durationsFromDriverAtoDriverB = " + durationsFromDriverAtoDriverB);
        //Вычисление пути от точки Б водителя до точки Б пассажира КОНЕЦ//

        //Вычисление длительности пути для всех маршрутов и поиск лучшего
        int optimumRoute=0;
        double optimumDuration=(double) durationsFromAtoA.get(0)+
                (double) durationFromPassengerAtoPassangerB.get(0)+
                (double) durationsFromDriverBtoPassB.get(0);
        for (int i = 0; i < durationFromPassengerAtoPassangerB.size(); i++) {
            double routesum=(double) durationsFromAtoA.get(i)+
                    (double) durationFromPassengerAtoPassangerB.get(i)+
                    (double) durationsFromDriverBtoPassB.get(i);
            System.out.println("route to client " + (i+1) + " = "+routesum);
            if (optimumDuration>routesum){
                optimumDuration=routesum;
                optimumRoute=i;
            }
        }

        System.out.println("Time to travel without passenger - " + durationsFromDriverAtoDriverB.get(0) + " seconds");
        System.out.println("Optimum client - " + (optimumRoute+1) + ", duration of full road with passenger - " + optimumDuration +" seconds");
        System.out.println("Extra time - wasted on passenger " + (optimumDuration - (double) durationsFromDriverAtoDriverB.get(0)) + " seconds");
    }
}