package stateManagement;

import models.State;
import service.CircuitBreaker;

public interface StateInterface {
    public State name();
    public void beforeExecution(CircuitBreaker cb);
    public void onSuccess(CircuitBreaker cb);
    public void onFailure(CircuitBreaker cb);
}