package api.controla_preju.schedulers;

import api.controla_preju.services.ExpenseService;
import api.controla_preju.services.RevenueService;
import api.controla_preju.services.TransferService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PendingTransactionsScheduler {

    private final ExpenseService expenseService;
    private final RevenueService revenueService;
    private final TransferService transferService;

    public PendingTransactionsScheduler(ExpenseService expenseService, RevenueService revenueService, TransferService transferService) {
        this.expenseService = expenseService;
        this.revenueService = revenueService;
        this.transferService = transferService;
    }

    @Scheduled(fixedDelay = 3600000)
    public void runPendingTransactions() {
        expenseService.processAutomaticDebits();
        revenueService.processAutomaticRevenues();
        transferService.processAutomaticTransfers();
    }

}