declare module ClientNamespace {

  export class Person {
    personIdentification: number;
    firstName: string;
    lastName: string;
    mail: string;
  }

  export class AccountTransactionList {
    transactionType: TransactionType;
    message: string;
  }

  export class AccountList {
    balance: number;
    accountTransactionList: AccountTransactionList[];
  }

  export class ClientType {
    type: Type;
    specialOffers: string;
    rating: number;
    premiumRating: number;
    country: string;
  }

  export class Client {
    person: Person;
    accountList: AccountList[];
    clientType: ClientType;
  }

  export enum Type {
    REGULAR,
    PREMIUM,
    FOREIGN
  }

  export enum TransactionType {
    DEPOSIT,
    WITHDRAWAL
  }
}
