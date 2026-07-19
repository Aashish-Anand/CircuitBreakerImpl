import java.time.Duration;

import config.CircuitBreakerConfig;
import exception.CircuitBreakerOpenException;
import service.CircuitBreaker;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        CircuitBreaker breaker =
                new CircuitBreaker(
                        new CircuitBreakerConfig(
                                3,
                                Duration.ofSeconds(5)
                        )
                );

        // String response = breaker.execute(() -> {

        //     // Simulate remote API

        //     return "Success";
        // });

        // System.out.println(response);

        RemoteService service = new RemoteService();

        for (int i = 1; i <= 30; i++) {

            try {

                String response = breaker.execute(service::call);

                System.out.printf("[%d] SUCCESS | State=%s | Response=%s%n",
                        i,
                        breaker.stateManagement.name(),
                        response);

            } catch (CircuitBreakerOpenException ex) {

                System.out.printf("[%d] BLOCKED | State=%s%n",
                        i,
                        breaker.stateManagement.name());

            } catch (Exception ex) {

                System.out.printf("[%d] FAILURE | State=%s | %s%n",
                        i,
                        breaker.stateManagement.name(),
                        ex.getMessage());

            }

            Thread.sleep(1000);
        }
    }
}