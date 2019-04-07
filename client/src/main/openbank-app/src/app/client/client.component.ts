import {Component, OnInit} from '@angular/core';
import {RestService} from "../service/rest.service";
import {HttpClient} from "@angular/common/http";
import Client = ClientNamespace.Client;

@Component({
  selector: 'app-client',
  templateUrl: './client.component.html',
  styleUrls: ['./client.component.scss'],
  providers: [RestService, HttpClient]
})
export class ClientComponent implements OnInit {
  client: Client;
  personIdentification: string;

  constructor(private restService: RestService) {
  }

  ngOnInit() {
    this.client = null;
    this.personIdentification = "";
  }

  getClientInformation(): void {
    this.restService.getClientInformation(this.personIdentification).subscribe(
      client => {
        this.client = client;
      },
      error => {
        console.log(error);
      },
      () => {
        console.log("Client information succesful");
      }
    );
  }

}
