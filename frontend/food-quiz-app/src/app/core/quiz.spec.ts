import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { QuizService } from './quiz';

describe('QuizService', () => {
  let service: QuizService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(QuizService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should call startQuiz with default count of 5', () => {
    service.startQuiz().subscribe();
    const req = httpTesting.expectOne((r) => r.url.includes('/quiz/start'));
    expect(req.request.params.get('count')).toBe('5');
    req.flush({ quizId: 'test-id' });
  });

  it('should call startQuiz with provided count', () => {
    service.startQuiz(10).subscribe();
    const req = httpTesting.expectOne((r) => r.url.includes('/quiz/start'));
    expect(req.request.params.get('count')).toBe('10');
    req.flush({ quizId: 'test-id' });
  });
});
