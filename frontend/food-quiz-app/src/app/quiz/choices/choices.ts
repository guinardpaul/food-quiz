import { Component, input, output, signal } from '@angular/core';

@Component({
  selector: 'app-choices',
  imports: [],
  templateUrl: './choices.html',
  styleUrl: './choices.scss',
})
export class Choices {
  readonly choices = input.required<string[]>();
  readonly answered = output<string>();

  protected selected = signal<string | null>(null);

  select(choice: string): void {
    if (this.selected()) return;
    this.selected.set(choice);
    this.answered.emit(choice);
  }
}
