import { Component } from '@angular/core';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent {
  selectedFile: File | undefined;
  fileData: string | undefined;

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64 = reader.result as string;
        const fileData = {
          fileName: file.name,
          fileType: file.type,
          base64File: base64.split(',')[1]
        };
        this.fileData = JSON.stringify(fileData, null, 2);
        console.log(this.fileData); // console json
      };
      reader.onerror = (error) => {
        console.error('Error reading file:', error);
      };
    }
  }
}