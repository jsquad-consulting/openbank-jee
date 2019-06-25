/*
 * Copyright 2019 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.jsquad.ejb;

import se.jsquad.entity.Account;
import se.jsquad.entity.AccountTransaction;
import se.jsquad.entity.TransactionType;
import se.jsquad.interceptor.LoggerInterceptor;
import se.jsquad.qualifier.Log;
import se.jsquad.repository.ClientRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.RollbackException;
import java.util.logging.Logger;

@Stateless
@LoggerInterceptor
public class AccountTransactionEJB {
    @Inject @Log
    private Logger logger;

    @Inject
    private ClientRepository clientRepository;

    public void transferValueFromAccountToAccount(long value, String fromAccountNumber, String toAccountNumber) {
        Account fromAccount = clientRepository.getAccountByNumber(fromAccountNumber);
        Account toAccount = clientRepository.getAccountByNumber(toAccountNumber);

        if(fromAccount != null && toAccount != null && fromAccount != toAccount) {
            transferValueFromAccountToAccount(value, fromAccount, toAccount);
        } else {
            throw new RollbackException("Invalid bank account");
        }
    }

    private void transferValueFromAccountToAccount(long value, Account fromAccount, Account toAccount) {
        if (value > 0 && value <= fromAccount.getBalance()) {
            fromAccount.setBalance(fromAccount.getBalance()-value);

            AccountTransaction fromAccountTransaction = new AccountTransaction();
            fromAccountTransaction.setTransactionType(TransactionType.WITHDRAWAL);
            fromAccountTransaction.setMessage(value + " has been withdrawn from the account.");
            fromAccountTransaction.setAccount(fromAccount);

            clientRepository.getEntityManager().persist(fromAccountTransaction);

            fromAccount.getAccountTransactionSet().add(fromAccountTransaction);

            toAccount.setBalance(toAccount.getBalance() + value);

            AccountTransaction toAccountTransaction = new AccountTransaction();
            toAccountTransaction.setMessage(value + " has been deposited to the account.");
            toAccountTransaction.setTransactionType(TransactionType.DEPOSIT);
            toAccountTransaction.setAccount(toAccount);

            clientRepository.getEntityManager().persist(toAccountTransaction);

            toAccount.getAccountTransactionSet().add(toAccountTransaction);
        } else {
            throw new RollbackException("Withdrawal value can't be greater then the account balance.");
        }
    }
}
