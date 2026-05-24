import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FeedbackDto, QuestionDto, QuizDto, QuizResultDto } from './models';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private readonly base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  startQuiz(): Observable<QuizDto> {
    return this.http.get<QuizDto>(`${this.base}/quiz/start`);
  }

  getCurrentQuestion(quizId: string): Observable<QuestionDto> {
    return this.http.get<QuestionDto>(`${this.base}/quiz/${quizId}/question`);
  }

  submitAnswer(quizId: string, answer: string): Observable<FeedbackDto> {
    return this.http.post<FeedbackDto>(`${this.base}/quiz/${quizId}/answer`, { answer });
  }

  getResult(quizId: string): Observable<QuizResultDto> {
    return this.http.get<QuizResultDto>(`${this.base}/quiz/${quizId}/result`);
  }
}
