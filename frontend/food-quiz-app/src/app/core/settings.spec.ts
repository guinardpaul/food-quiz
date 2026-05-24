import { TestBed } from '@angular/core/testing';
import { SettingsService } from './settings';

describe('SettingsService', () => {
  let service: SettingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SettingsService);
  });

  it('should return 5 by default', () => {
    expect(service.questionCount()).toBe(5);
  });

  it('should update questionCount when setQuestionCount is called', () => {
    service.setQuestionCount(8);
    expect(service.questionCount()).toBe(8);
  });
});
