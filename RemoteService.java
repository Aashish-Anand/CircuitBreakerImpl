import java.util.concurrent.ThreadLocalRandom;

public class RemoteService {

    public String call() {

        // Simulate network latency
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // 70% success rate
        if (ThreadLocalRandom.current().nextInt(10) < 7) {
            return "API Response";
        }

        throw new RuntimeException("Remote service failed");
    }
}