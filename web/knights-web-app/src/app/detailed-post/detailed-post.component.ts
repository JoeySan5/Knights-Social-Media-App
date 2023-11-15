import { Component, OnInit } from '@angular/core';
import { DetailedPostInfoService } from '../detailed-post-info.service';


@Component({
  selector: 'detailed-post',
  templateUrl: './detailed-post.component.html',
  styleUrls: ['./detailed-post.component.css']
})
export class DetailedPostComponent implements OnInit{
  //this data will be initialized. This holds the data of a specific idea
  data: any;

  //this constructor id's and injection site for the service. setting detailedPost... as an instance of DetailedPost...
  constructor(private detailedPostInfoService: DetailedPostInfoService){
  }

  ngOnInit(): void {
      this.getData();
  }
  getData(): any{
    this.data = this.detailedPostInfoService.getData();
    console.log("here is data in detailed-post component:", this.data);
  }
}
