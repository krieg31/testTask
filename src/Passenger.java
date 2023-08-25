import java.math.BigDecimal;
import java.math.RoundingMode;

class Passenger
{
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