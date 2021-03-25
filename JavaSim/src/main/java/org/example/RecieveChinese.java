package org.example;

import org.javasim.*;
import org.javasim.streams.UniformStream;

import java.io.IOException;

public class RecieveChinese extends SimulationProcess {
    private UniformStream chineseMakingTime = new UniformStream(0.15, 0.45);
    public Customer customer;

    public RecieveChinese(Customer customer) {
        this.customer = customer;
    }

    public void run() {
        while (!terminated()) {
            try {
                hold(chineseMakingTime.getNumber());
                SimulatorCore.chineseWorkers++;
                if (!SimulatorCore.chineseQueue.isEmpty()) {
                    SimulatorCore.chineseQueue.pop().activate();
                }
                new OrderAgainOrLeave(this.customer).activate();
                this.terminate();
            } catch (SimulationException e) {
            } catch (RestartException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
