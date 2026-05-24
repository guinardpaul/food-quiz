import { Component, input, output } from '@angular/core';
import { FeedbackDto } from '../../core/models';

@Component({
  selector: 'app-feedback',
  imports: [],
  templateUrl: './feedback.html',
  styleUrl: './feedback.scss',
})
export class Feedback {
  readonly feedback = input.required<FeedbackDto>();
  readonly next = output<void>();

  protected get impactLabel(): string {
    const map: Record<string, string> = {
      LOW: '🟢 Faible impact glycémique',
      MEDIUM: '🟡 Impact glycémique modéré',
      HIGH: '🔴 Fort impact glycémique',
    };
    return map[this.feedback().glycemicImpact] ?? '';
  }
}
