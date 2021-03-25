package org.example;

import org.javasim.RestartException;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;
import org.javasim.streams.ExponentialStream;

public class TravelTo extends SimulationProcess {
    private Customer customer;
    private ExponentialStream InterArrivalTime = new ExponentialStream(1);

    public TravelTo(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void run() {
        while (!this.terminated()) {
            try {
                hold(0.15);
                while (SimulatorCore.tables <= 0) {
                    SimulatorCore.tableQueue.add(this);
                    this.suspendProcess();
                }
                SimulatorCore.tables--;
                new TravelToFoodOrDrink(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
                e.printStackTrace();
            } catch (RestartException e) {
                e.printStackTrace();
            }
        }
    }
}
