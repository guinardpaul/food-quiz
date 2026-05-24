import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SettingsService {
  private readonly _questionCount = signal(5);

  readonly questionCount = this._questionCount.asReadonly();

  setQuestionCount(count: number): void {
    this._questionCount.set(count);
  }
}
