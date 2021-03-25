package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class TravelToFoodOrDrink extends SimulationProcess {
    private ExponentialStream InterArrivalTime = new ExponentialStream(1);
    public Customer customer;
    public TravelTo to;

    public TravelToFoodOrDrink(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                if (SimulatorCore.getBranch() > 4) {
                    new TravelToFoodStandChoice(this.customer).activate();


                } else {
                    new TravelToDrink(this.customer).activate();
                }
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
