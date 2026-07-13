package stateManagement;

import service.CircuitBreaker;

public interface StateInterface {
    public void beforeExecution(CircuitBreaker cb);
    public void onSuccess(CircuitBreaker cb);
    public void onFailure(CircuitBreaker cb);
}