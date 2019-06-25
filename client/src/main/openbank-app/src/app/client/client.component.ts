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
