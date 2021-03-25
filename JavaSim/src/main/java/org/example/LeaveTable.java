package org.example;

import org.javasim.*;
import org.javasim.streams.ExponentialStream;

public class LeaveTable extends SimulationProcess {
    private ExponentialStream InterArrivalTime;
    public Customer customer;

    public LeaveTable(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                SimulatorCore.tables++;
                if (!SimulatorCore.tableQueue.isEmpty()) {
                    SimulatorCore.tableQueue.pop().activate();
                }
                new TravelToCheckout(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
