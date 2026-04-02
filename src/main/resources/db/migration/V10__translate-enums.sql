UPDATE accounts SET type = 'CHECKING_ACCOUNT' WHERE type = 'CONTA_CORRENTE';
UPDATE accounts SET type = 'INVESTMENTS' WHERE type = 'INVESTIMENTOS';
UPDATE accounts SET type = 'SAVINGS_ACCOUNT' WHERE type = 'POUPANCA';
UPDATE accounts SET type = 'WALLET' WHERE type = 'CARTEIRA';

UPDATE revenues SET category = 'OTHERS' WHERE category = 'OUTROS';
UPDATE revenues SET category = 'SALARY' WHERE category = 'SALARIO';
UPDATE revenues SET category = 'INVESTMENTS' WHERE category = 'INVESTIMENTOS';

UPDATE expenses SET payment_method = 'CASH' WHERE payment_method = 'DINHEIRO';
UPDATE expenses SET payment_method = 'CREDIT_CARD' WHERE payment_method = 'CREDITO';
UPDATE expenses SET payment_method = 'DEBIT_CARD' WHERE payment_method = 'DEBITO';
UPDATE expenses SET payment_method = 'BANK_SLIP' WHERE payment_method = 'BOLETO';

UPDATE expenses SET category = 'OTHERS' WHERE category = 'OUTROS';
UPDATE expenses SET category = 'INVESTMENTS' WHERE category = 'INVESTIMENTOS';
UPDATE expenses SET category = 'HOUSING' WHERE category = 'MORADIA';
UPDATE expenses SET category = 'TRANSPORT' WHERE category = 'TRANSPORTE';
UPDATE expenses SET category = 'FOOD' WHERE category = 'ALIMENTACAO';
UPDATE expenses SET category = 'GROCERIES' WHERE category = 'SUPERMERCADO';
UPDATE expenses SET category = 'HEALTH' WHERE category = 'SAUDE';
UPDATE expenses SET category = 'CLOTHING' WHERE category = 'ROUPAS';
UPDATE expenses SET category = 'APPEARANCE' WHERE category = 'APARENCIA';
UPDATE expenses SET category = 'TECHNOLOGY' WHERE category = 'TECNOLOGIA';
UPDATE expenses SET category = 'SERVICES' WHERE category = 'SERVICOS';
UPDATE expenses SET category = 'DEBTS' WHERE category = 'DIVIDAS';
