import { TestBed } from '@angular/core/testing';

import { TrainingClassesService } from './training-classes.service';

describe('TrainingClassesService', () => {
  let service: TrainingClassesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrainingClassesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
