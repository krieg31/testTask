import static java.lang.Math.abs;

class Client
{
    public String name;
    public int id;
    public int cooridinateA1;
    public int cooridinateA2;
    public int cooridinateB1;
    public int cooridinateB2;

    public Client(String name, int id, int cooridinateA1, int cooridinateA2, int cooridinateB1, int cooridinateB2)
    {
        this.name = name;
        this.id = id;
        this.cooridinateA1 = cooridinateA1;
        this.cooridinateA2 = cooridinateA2;
        this.cooridinateB1 = cooridinateB1;
        this.cooridinateB2 = cooridinateB2;
    }
    public static Client CreateClient(int ClientId)
    {
        String name = "Client"+ClientId;
        int id = ClientId;
        int coordinateA1 = getRandomNumber();
        int cooridinateA2 = getRandomNumber();
        int cooridinateB1 = getRandomNumber();
        int cooridinateB2 = getRandomNumber();

        return new Client(name , id , coordinateA1, cooridinateA2, cooridinateB1, cooridinateB2);
    }
    public Client PrintClient()
    {
        System.out.println("name = " + this.name);
        System.out.println("id = " + this.id);
        System.out.println("coordinateA1 = " + this.cooridinateA1);
        System.out.println("cooridinateA2 = " + this.cooridinateA2);
        System.out.println("cooridinateB1 = " + this.cooridinateB1);
        System.out.println("cooridinateB2 = " + this.cooridinateB2);

        return null;
    }
    public static int getRandomNumber() {
        int min = 0;
        int max = 100;
        return (int) ((Math.random() * (max - min)) + min);
    }
}


public class Main {
    public static int CalculateDistance(int cooridinateA1, int cooridinateA2,
                                        int cooridinateA3, int cooridinateA4,
                                        int cooridinateB1, int cooridinateB2,
                                        int cooridinateB3, int cooridinateB4)
    {
        //так-как в нашем случае нельзя "ходить" по диагонали,
        //количество ходов для перехода от одной ячейки к другой вычисляется очень просто
        int x,y,x1,y1, x0, y0;

        // x+y - количество шагов от места старта водителя до местонахождения клиента
        x = abs(cooridinateA1-cooridinateA3);
        y = abs(cooridinateA2-cooridinateA4);

        // x0+y0 - количество шагов от местонахождения клиента до его точки назначения
        x0 = abs(cooridinateA1-cooridinateB1);
        y0 = abs(cooridinateA2-cooridinateB2);

        // x1+y1 - количество шагов от высадки пассажира до конечной точки водителя
        x1 = abs(cooridinateB1-cooridinateB3);
        y1 = abs(cooridinateB2-cooridinateB4);

        // суммируем общее количество шагов для прохождения маршрута
        return(x+y+x1+y1+x0+y0);
    }

    public static void main(String[] args) {

        //создаю "Водителя", использую класс клиента, т.к. создавать
        //отдельный класс в данном случае излишне, у него id = 0

        Client Driver = Client.CreateClient(0);

        //создаю массив из 5 клиентов, id начинаются с 1
        Client[] ArrayOfClients = new Client[5];
        for (int i = 1; i < ArrayOfClients.length; i++)
        {
            ArrayOfClients[i] = Client.CreateClient(i);
        }
        //создаю контейнер для хранения количества шагов; 200 - максимальное число для "карты" 100х100
        int optimalDistance=200;

        //создаю конейнер для хранения id клиента, с ближайшим маршрутом
        //значение по-умолчанию если клиент всего один
        int optimalClientId = 1;


        for (int i = 1; i < ArrayOfClients.length; i++)
        {
            int temp = CalculateDistance(
                    ArrayOfClients[i].cooridinateA1, ArrayOfClients[i].cooridinateA2,
                    Driver.cooridinateA1, Driver.cooridinateA2,
                    ArrayOfClients[i].cooridinateB1, ArrayOfClients[i].cooridinateB2,
                    Driver.cooridinateB1, Driver.cooridinateB2);

            if (optimalDistance>= temp)
            {
                optimalDistance = temp;
                optimalClientId = ArrayOfClients[i].id;
            }
        }
        System.out.println("Водитель : ");
        Driver.PrintClient();
            System.out.println();
            System.out.println();

        System.out.println("Клиент с попутным маршрутом : ");
        ArrayOfClients[optimalClientId].PrintClient();
            System.out.println();
            System.out.println();

        System.out.println("Суммарная дистанция = " + optimalDistance);
    }
}