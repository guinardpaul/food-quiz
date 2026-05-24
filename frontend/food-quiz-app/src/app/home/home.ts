import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { QuizService } from '../core/quiz';
import { SettingsService } from '../core/settings';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  protected loading = signal(false);
  protected error = signal<string | null>(null);

  private quizService = inject(QuizService);
  private router = inject(Router);
  protected settings = inject(SettingsService);

  start(): void {
    this.loading.set(true);
    this.error.set(null);
    this.quizService.startQuiz(this.settings.questionCount()).subscribe({
      next: (quiz) => this.router.navigate(['/quiz', quiz.quizId]),
      error: () => {
        this.loading.set(false);
        this.error.set('Impossible de démarrer le quiz. Réessaie.');
      },
    });
  }
}
