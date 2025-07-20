package tn.bfpme.services;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import tn.bfpme.controllers.RHC.AttributionSoldeController;

public class LeaveBalanceService extends ScheduledService<Void> {
    private final AttributionSoldeController controller;

    public LeaveBalanceService(AttributionSoldeController controller) {
        this.controller = controller;
        setPeriod(Duration.hours(24)); // Schedule to run every 24 hours
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                controller.incrementLeaveBalances();
                return null;
            }
        };
    }
}

