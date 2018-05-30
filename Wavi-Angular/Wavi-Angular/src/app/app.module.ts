import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {WindowRef} from "./WindowRef";
import {AppStreaming} from "./app.streaming";

@NgModule({
  declarations: [
    AppComponent,
    AppStreaming
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [WindowRef],
  bootstrap: [AppComponent]
  //bootstrap: [AppStreaming]
})
export class AppModule { }
