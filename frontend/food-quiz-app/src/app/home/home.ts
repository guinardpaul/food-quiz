import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { QuizService } from '../core/quiz';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  protected loading = signal(false);
  protected error = signal<string | null>(null);

  constructor(private quizService: QuizService, private router: Router) {}

  start(): void {
    this.loading.set(true);
    this.error.set(null);
    this.quizService.startQuiz().subscribe({
      next: (quiz) => this.router.navigate(['/quiz', quiz.quizId]),
      error: () => {
        this.loading.set(false);
        this.error.set('Impossible de démarrer le quiz. Réessaie.');
      },
    });
  }
}
