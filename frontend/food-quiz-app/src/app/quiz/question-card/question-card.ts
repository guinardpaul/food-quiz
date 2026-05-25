import { Component, computed, input, signal } from '@angular/core';
import { QuestionDto } from '../../core/models';

@Component({
  selector: 'app-question-card',
  imports: [],
  templateUrl: './question-card.html',
  styleUrl: './question-card.scss',
})
export class QuestionCard {
  readonly question = input.required<QuestionDto>();
  readonly index = input<number>(0);

  protected imageLoaded = signal(false);
  protected imageError = signal(false);

  protected isRealPhoto = computed(() =>
    this.question().imageUrl?.includes('/assets/questions/real/') ?? false
  );
}
