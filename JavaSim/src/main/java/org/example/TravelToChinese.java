package org.example;

import org.javasim.*;

public class TravelToChinese extends SimulationProcess {
    public Customer customer;

    public TravelToChinese(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(0.15);
                while (SimulatorCore.chineseWorkers <= 0) {
                    SimulatorCore.chineseQueue.add(this);
                    this.suspendProcess();
                }
                SimulatorCore.chineseWorkers--;
                new RecieveChinese(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            }
        }
    }
}
