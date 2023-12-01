import { TestBed } from '@angular/core/testing';

import { DetailedPostInfoService } from './detailed-post-info.service';

describe('DetailedPostInfoService', () => {
  let service: DetailedPostInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DetailedPostInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
