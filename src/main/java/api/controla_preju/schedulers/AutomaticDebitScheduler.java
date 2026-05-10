package api.controla_preju.schedulers;

import api.controla_preju.services.ExpenseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutomaticDebitScheduler {

    private final ExpenseService expenseService;

    public AutomaticDebitScheduler(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Scheduled(fixedDelay = 3600000) // Executa a cada 1 hora (3600000 ms)
    public void runAutomaticDebit() {
        expenseService.processAutomaticDebits();
    }

}
