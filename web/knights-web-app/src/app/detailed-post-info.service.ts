import { Injectable } from '@angular/core';


//The Purpose of this 'service' class is to have unrelated components send data between one another.
//in this specific case, we want to send data from idea-list to detailed-post 
//the additional case will be when sending data of another user. It will be set in this service and then get by the other-profile component

//This decorator provides the ability to inject a service into a component. So the component can have access to the service
@Injectable({
  //since this is providedIn root level, then it is available for any class/component that asks for it
  providedIn: 'root'
})
export class DetailedPostInfoService {
  private sharedData: any;


  constructor() { }

   getData(): string {
    console.log('here is data: in in getData', this.sharedData);

    return this.sharedData;
  }

   async setData(value: string) {

    this.sharedData = value;
    console.log('here is data: in service', this.sharedData);

  }
}
