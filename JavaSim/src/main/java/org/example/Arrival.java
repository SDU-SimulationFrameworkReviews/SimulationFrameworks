package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class Arrival extends SimulationProcess {
    private ExponentialStream InterArrivalTime = new ExponentialStream(1);
    public Customer customer;

    public void run() {
        while (!this.terminated()) {
            try {
                hold(0.1);
                SimulatorCore.customerArrivals++;
                customer = new Customer(SimulatorCore.customerArrivals, Scheduler.currentTime());
                new TravelTo(customer).activate();
                if (SimulatorCore.customerArrivals >= SimulatorCore.totalNumberOfCustomers) {
                    this.terminate();
                }
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
