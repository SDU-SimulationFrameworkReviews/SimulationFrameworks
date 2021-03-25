package org.example;

import org.javasim.RestartException;
import org.javasim.Scheduler;
import org.javasim.SimulationException;
import org.javasim.SimulationProcess;

public class Drinking extends SimulationProcess {
    private Customer customer;

    public Drinking(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void run() {
        while (!this.terminated()) {
            try {
                hold(1);
                System.out.println("c" + customer.getId() + " org.example.Drinking " + Scheduler.currentTime());
                int branch = (int) (Math.random() * (1 - 10)) + 10;
                if (branch > 4) {
                } else {
                }
                new LeaveTable(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
                e.printStackTrace();
            } catch (RestartException e) {
                e.printStackTrace();
            }
        }
    }
}
