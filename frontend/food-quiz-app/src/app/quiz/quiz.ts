import { Component, input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { QuizService } from '../core/quiz';
import { FeedbackDto, QuestionDto, QuizResultDto } from '../core/models';
import { QuestionCard } from './question-card/question-card';
import { Choices } from './choices/choices';
import { Feedback } from './feedback/feedback';
import { Score } from './score/score';

type Step = 'loading' | 'question' | 'feedback' | 'result';

@Component({
  selector: 'app-quiz',
  imports: [QuestionCard, Choices, Feedback, Score],
  templateUrl: './quiz.html',
  styleUrl: './quiz.scss',
})
export class Quiz implements OnInit {
  readonly quizId = input.required<string>();

  protected step = signal<Step>('loading');
  protected currentQuestion = signal<QuestionDto | null>(null);
  protected feedback = signal<FeedbackDto | null>(null);
  protected result = signal<QuizResultDto | null>(null);
  protected questionIndex = signal(0);

  constructor(private quizService: QuizService, private router: Router) {}

  ngOnInit(): void {
    this.loadQuestion();
  }

  protected onAnswer(answer: string): void {
    this.quizService.submitAnswer(this.quizId(), answer).subscribe({
      next: (fb) => {
        this.feedback.set(fb);
        this.step.set('feedback');
      },
    });
  }

  protected onNext(): void {
    const fb = this.feedback();
    if (fb?.hasNextQuestion) {
      this.questionIndex.update((n) => n + 1);
      this.loadQuestion();
    } else {
      this.quizService.getResult(this.quizId()).subscribe({
        next: (r) => {
          this.result.set(r);
          this.step.set('result');
        },
      });
    }
  }

  protected onRestart(): void {
    this.router.navigate(['/']);
  }

  private loadQuestion(): void {
    this.step.set('loading');
    this.quizService.getCurrentQuestion(this.quizId()).subscribe({
      next: (q) => {
        this.currentQuestion.set(q);
        this.feedback.set(null);
        this.step.set('question');
      },
    });
  }
}
