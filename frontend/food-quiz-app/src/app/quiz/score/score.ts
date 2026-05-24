import { Component, computed, input, output } from '@angular/core';
import { QuizResultDto } from '../../core/models';

@Component({
  selector: 'app-score',
  imports: [],
  templateUrl: './score.html',
  styleUrl: './score.scss',
})
export class Score {
  readonly result = input.required<QuizResultDto>();
  readonly restart = output<void>();

  protected emoji = computed(() => {
    const s = this.result().score;
    if (s >= 80) return '🏆';
    if (s >= 50) return '👍';
    return '💪';
  });

  protected message = computed(() => {
    const s = this.result().score;
    if (s >= 80) return 'Excellent ! Tu maîtrises bien les glucides.';
    if (s >= 50) return 'Pas mal ! Continue à t\'entraîner.';
    return 'Ne te décourage pas, tu progresseras !';
  });
}
