import { Component } from '@angular/core';
import { DetailedPostInfoService } from '../detailed-post-info.service';

@Component({
  selector: 'other-profile',
  templateUrl: './other-profile.component.html',
  styleUrls: ['./other-profile.component.css']
})
export class OtherProfileComponent {
  //this data will be initialized. This holds the data of a specific idea
  data: any;

  //this constructor id's and injection site for the service. setting detailedPost... as an instance of DetailedPost...
  constructor(private detailedPostInfoService: DetailedPostInfoService) {
  }

  ngOnInit(): void {
    this.getData();
  }
  getData(): any {
    this.data = this.detailedPostInfoService.getData();
    console.log("here is data in other profile component:", this.data);
  }
}
