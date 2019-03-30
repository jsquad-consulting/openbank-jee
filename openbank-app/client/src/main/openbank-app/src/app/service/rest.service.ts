import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Constants} from "../constants/constants";
import {Observable} from "rxjs";
import Client = ClientNamespace.Client;

@Injectable()
export class RestService {

  constructor(private httpClient: HttpClient) { }

  getClientInformation(personIdentification: string): Observable<Client> {
    return this.httpClient.get<Client>(Constants.LOCALHOST + Constants.GET_CLIENT_INFORMATION
      + personIdentification);
  }
}
