import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'base64Decode'
})
export class Base64DecodePipe implements PipeTransform {
  transform(value: string): string {
    try {
      return atob(value);
    } catch (e) {
      console.error('Error decoding base64 string', e);
      return value; 
    }
  }
}