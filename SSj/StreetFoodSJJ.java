package umontreal.ssj;

import umontreal.ssj.simevents.*;
import umontreal.ssj.rng.*;
import umontreal.ssj.randvar.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class StreetFoodSJJ {

    RandomVariateGen arrivalGenerator;
    RandomVariateGen pouringGenerator;
    RandomVariateGen drinkingGenerator;
    RandomVariateGen checkoutGenerator;

    RandomVariateGen waitPizzaGenerator;
    RandomVariateGen eatPizzaGenerator;

    RandomVariateGen waitChineseGenerator;
    RandomVariateGen eatChineseGenerator;

    RandomVariateGen waitBurgerGenerator;
    RandomVariateGen eatBurgerGenerator;

    int customerArrivals = 0; // Number of customers
    int numberOfCustomers = 0;
    int numberLeft = 0;

    ArrayList<Customer> tablesInUse = new ArrayList();
    LinkedList<TravelTo> tablesQue = new LinkedList();

    ArrayList<Customer> drinkWorkersInUse = new ArrayList();
    LinkedList<TravelToDrink> drinksQue = new LinkedList();

    ArrayList<Customer> checkoutWorkersInUse = new ArrayList();
    LinkedList<TravelToCheckout> checkoutQue = new LinkedList();

    ArrayList<Customer> pizzaWorkersInUse = new ArrayList();
    LinkedList<TravelToPizza> pizzaQue = new LinkedList();

    ArrayList<Customer> chineseWorkersInUse = new ArrayList();
    LinkedList<TravelToChinese> chineseQue = new LinkedList();

    ArrayList<Customer> burgerWorkersInUse = new ArrayList();
    LinkedList<TravelToBurger> burgerQue = new LinkedList();

    public StreetFoodSJJ() {
        arrivalGenerator = new ExponentialGen(new MRG32k3a(), 1);
        pouringGenerator = new UniformGen(new MRG32k3a(), 15, 45);
        drinkingGenerator = new UniformGen(new MRG32k3a(), 15, 45);
        checkoutGenerator = new UniformGen(new MRG32k3a(), 15, 45);

        waitPizzaGenerator = new UniformGen(new MRG32k3a(), 15, 45);
        eatPizzaGenerator = new UniformGen(new MRG32k3a(), 15, 45);

        waitChineseGenerator = new UniformGen(new MRG32k3a(), 15, 45);
        eatChineseGenerator = new UniformGen(new MRG32k3a(), 15, 45);

        waitBurgerGenerator = new UniformGen(new MRG32k3a(), 15, 45);
        eatBurgerGenerator = new UniformGen(new MRG32k3a(), 15, 45);
    }

    class Arrival extends Event {
        public void actions() {
            customerArrivals++;
            Customer customer = new Customer(customerArrivals, sim.time());
            //System.out.println(customer.toString());
            // When a customer arrives they take 15 time units to move to the que or found a table
            new TravelTo(customer).schedule(15);

            // If all customers has arrived don't schedule further customer arrivals
            if(customerArrivals < numberOfCustomers) {
                new Arrival().schedule(arrivalGenerator.nextDouble() * 20);
            }
        }
    }

    class TravelTo extends CustomerEvent {

        public TravelTo(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrived at tableQue", sim);
            if(tablesInUse.size() >= 100) {
                // Customer much join a que until a table becomes available
                tablesQue.addLast(this);
                //this.getCustomer().print("Que for table, tableQueLength: "+tablesQue.size(), sim);
            } else {
                TableGranted("Directly");
            }
        }

        public void TableGranted(String origin) {
            //this.getCustomer().print("Received table "+origin, sim);
            tablesInUse.add(this.getCustomer());
            //this.getCustomer().print("Travel to FoodOrDrink", sim);
            new TravelToFoodOrDrink(this.getCustomer()).schedule(1.5);
        }
    }

    class TravelToFoodOrDrink extends CustomerEvent {

        public TravelToFoodOrDrink(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrived at FoodOrDrink", sim);
            int branch = GetRandomIntBetween(1, 10);
            if(branch > 4) { // 60% chance
                //this.getCustomer().print("Travel to foodStandChoice", sim);
                new TravelToFoodStandChoice(this.getCustomer()).schedule(1.5);
            } else { // 40% chance
                //this.getCustomer().print("Travel to drink", sim);
                new TravelToDrink(this.getCustomer()).schedule(1.5);
            }
        }
    }

    class TravelToDrink extends CustomerEvent {

        public TravelToDrink(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrived at drink", sim);
            if(drinkWorkersInUse.size() >= 3) {
                //this.getCustomer().print("Que for Drink", sim);
                drinksQue.addLast(this);
            } else {
                DrinkWorkerGranted("- Directly");
            }
        }

        public void DrinkWorkerGranted(String origin) {
            drinkWorkersInUse.add(this.getCustomer());
            //this.getCustomer().print("Drink worker begin serving " + origin , sim);
            new PouringDrink(this.getCustomer()).schedule(pouringGenerator.nextDouble());
        }
    }

    class PouringDrink extends CustomerEvent {

        public PouringDrink(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            // Decide on only a drink or also get food
            //this.getCustomer().print("Received Drink", sim);
            //this.getCustomer().print("Decide Only drink or?", sim);
            new DecideOnlyDrinkOr(this.getCustomer()).schedule(1.5);
            // We have been served return the drinkWorker resource
            drinkWorkersInUse.remove(this.getCustomer());
            if(drinksQue.size() > 0) {
                TravelToDrink firstInQue = drinksQue.getFirst();
                drinksQue.remove(firstInQue);
                firstInQue.DrinkWorkerGranted("- From que");
            }
        }
    }

    class DecideOnlyDrinkOr extends CustomerEvent {

        public DecideOnlyDrinkOr(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            int branch = GetRandomIntBetween(1,10);
            if(branch < 6) {
                //this.getCustomer().print("Travel to food stand choice", sim);
                new TravelToFoodStandChoice(this.getCustomer()).schedule(1.5);
            } else {
                //this.getCustomer().print("Enjoy drink only", sim);
                new OrderAgainOrLeave(this.getCustomer()).schedule(drinkingGenerator.nextDouble());
            }
        }
    }

    class TravelToFoodStandChoice extends CustomerEvent {

        public TravelToFoodStandChoice(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            int branch = GetRandomIntBetween(1, 100);
            //this.getCustomer().print("Arrived at FoodStandChoice", sim);
            if(branch < 34) {
                //this.getCustomer().print("Travel to pizza", sim);
                new TravelToPizza(this.getCustomer()).schedule(1.5);
            } else if (branch < 67) {
                //this.getCustomer().print("Travel to Chinese", sim);
                new TravelToChinese(this.getCustomer()).schedule(1.5);
            } else {
                //this.getCustomer().print("Travel to burger", sim);
                new TravelToBurger(this.getCustomer()).schedule(1.5);
            }
        }
    }

    class TravelToPizza extends CustomerEvent {

        public TravelToPizza(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrived at pizza", sim);
            if(pizzaWorkersInUse.size() >= 3) {
                pizzaQue.addLast(this);
                //this.getCustomer().print("Que for pizza - Que length" + pizzaQue.size(), sim);
            } else {
                PizzaWorkerGranted("Directly");
            }
        }

        public void PizzaWorkerGranted(String origin) {
            //this.getCustomer().print("Pizza man began serving you " + origin, sim);
            pizzaWorkersInUse.add(this.getCustomer());
            new ReceivePizza(this.getCustomer()).schedule(waitPizzaGenerator.nextDouble());
        }
    }

    class ReceivePizza extends CustomerEvent {

        public ReceivePizza(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Received pizza - started eating", sim);
            new OrderAgainOrLeave(this.getCustomer()).schedule(eatPizzaGenerator.nextDouble());
            pizzaWorkersInUse.remove(this.getCustomer());
            if(pizzaQue.size() > 0) {
                TravelToPizza nextInPizzaQue = pizzaQue.getFirst();
                pizzaQue.remove(nextInPizzaQue);
                nextInPizzaQue.PizzaWorkerGranted("- From que");
            }
        }
    }

    class TravelToChinese extends CustomerEvent {

        public TravelToChinese(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrive at chinese", sim);
            if(chineseWorkersInUse.size() >= 3) {
                chineseQue.addLast(this);
                //this.getCustomer().print("Que for chinese - Que length " + chineseQue.size(), sim);
            } else {
                ChineseWorkerGranted("- Directly");
            }
        }

        public void ChineseWorkerGranted(String origin) {
            //this.getCustomer().print("Chinese man began serving you", sim);
            chineseWorkersInUse.add(this.getCustomer());
            new ReceiveChinese(this.getCustomer()).schedule(waitChineseGenerator.nextDouble());
        }
    }

    class ReceiveChinese extends CustomerEvent {

        public ReceiveChinese(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Received Chinese - started eating", sim);
            new OrderAgainOrLeave(this.getCustomer()).schedule(eatChineseGenerator.nextDouble());
            chineseWorkersInUse.remove(this.getCustomer());
            if(chineseQue.size() > 0) {
                TravelToChinese nextInChineseQue = chineseQue.getFirst();
                chineseQue.remove(nextInChineseQue);
                nextInChineseQue.ChineseWorkerGranted("- From que");
            }
        }
    }

    class TravelToBurger extends CustomerEvent {

        public TravelToBurger(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrived at burger", sim);
            if(burgerWorkersInUse.size() >= 3) {
                burgerQue.addLast(this);
                //this.getCustomer().print("Que for burger - Que length " + burgerQue.size(), sim);
            } else {
                BurgerWorkerGranted("- Directly");
            }
        }

        public void BurgerWorkerGranted(String origin) {
            //this.getCustomer().print("Burger man began serving you", sim);
            burgerWorkersInUse.add(this.getCustomer());
            new ReceiveBurger(this.getCustomer()).schedule(waitBurgerGenerator.nextDouble());
        }
    }

    class ReceiveBurger extends CustomerEvent {

        public ReceiveBurger(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Received burger - started eating", sim);
            new OrderAgainOrLeave(this.getCustomer()).schedule(eatBurgerGenerator.nextDouble());
            burgerWorkersInUse.remove(this.getCustomer());
            if(burgerQue.size() > 0) {
                TravelToBurger nextInBurgerQue = burgerQue.getFirst();
                burgerQue.remove(nextInBurgerQue);
                nextInBurgerQue.BurgerWorkerGranted("- From que");
            }
        }
    }

    class OrderAgainOrLeave extends CustomerEvent {

        public OrderAgainOrLeave(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Done eating and or drinking ", sim);
            int branch = GetRandomIntBetween(1, 10);
            if(branch < 4) { // 30% chance
                //this.getCustomer().print("Order again travel to food or drink", sim);
                new TravelToFoodOrDrink(this.getCustomer()).schedule(1.5);
            } else {
                //this.getCustomer().print("Leaving table", sim);
                new LeaveTable(this.getCustomer()).schedule(1.5);
            }
        }
    }

    class LeaveTable extends CustomerEvent {

        public LeaveTable(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Travel to checkout", sim);
            new TravelToCheckout(this.getCustomer()).schedule(1.5);
            // We have been served return the drinkWorker resource
            tablesInUse.remove(this.getCustomer());
            if(tablesQue.size() > 0) {
                TravelTo firstTableRequestInQue = tablesQue.getFirst();
                tablesQue.remove(firstTableRequestInQue);
                firstTableRequestInQue.TableGranted("from que");
            }
        }
    }

    class TravelToCheckout extends CustomerEvent {

        public TravelToCheckout(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Arrived at checkout", sim);
            if(checkoutWorkersInUse.size() > 3) {
                //this.getCustomer().print("Que for checkout", sim);
                checkoutQue.addLast(this);
            } else {
                CashierGranted();
            }
        }

        public void CashierGranted() {
            //this.getCustomer().print("Checkout started", sim);
            checkoutWorkersInUse.add(this.getCustomer());
            new LeaveCheckout(this.getCustomer()).schedule(checkoutGenerator.nextDouble());
        }
    }

    class LeaveCheckout extends CustomerEvent {

        public LeaveCheckout(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Checkout completed", sim);
            checkoutWorkersInUse.remove(this.getCustomer());
            if(checkoutQue.size() > 0) {
                TravelToCheckout firstInCheckoutQue = checkoutQue.getFirst();
                checkoutQue.remove(firstInCheckoutQue);
                firstInCheckoutQue.CashierGranted();
            }

            new Leaving(this.getCustomer()).schedule(0.5);
        }
    }

    class Leaving extends CustomerEvent {

        public Leaving(Customer customer) {
            super(customer);
        }

        @Override
        public void actions() {
            //this.getCustomer().print("Leaving", sim);
            // The flow is done
            numberLeft++;
        }
    }

    abstract class CustomerEvent extends Event {
        private Customer customer;

        public CustomerEvent(Customer customer) {
            this.customer = customer;
        }

        public Customer getCustomer() {
            return customer;
        }
    }

    class Customer {

        private final int id;
        private final double arrivalTime;

        public Customer(int id, double arrivalTime) {

            this.id = id;
            this.arrivalTime = arrivalTime;
        }

        public int getId() {
            return id;
        }

        public double getArrivalTime() {
            return arrivalTime;
        }

        public void print(String msg, Simulator sim) {
            System.out.println("Customer"+id + " - " + msg + " :Time " + sim.time());
        }

        @Override
        public String toString() {
            return "Customer" + id + " - Arrived at: " + arrivalTime;
        }
    }

    public void Simulate(int numberOfCustomers) {
        Sim.init();

        // Reset customerArrivals to 0 and set total number of customers for this simulation
        this.customerArrivals = 0;
        this.numberOfCustomers = numberOfCustomers;

        // Schedule the first customer arrival
        new Arrival().schedule(arrivalGenerator.nextDouble());
        // With one even in the event que we are ready to begin the simulation
        Sim.start();
        System.out.println("Total customers left: " + numberLeft);
    }

    public int GetRandomIntBetween(int lower, int upper) {
        return (int) (Math.random() * (upper - lower)) + lower;
    }

    public static void main (String[] args) {
        System.out.println("Running SSJ StreetFood Simulation with " + args[0] + " customers");
        StreetFoodSJJ streetFood = new StreetFoodSJJ();
        streetFood.Simulate(Integer.parseInt(args[0]));
    }
}
