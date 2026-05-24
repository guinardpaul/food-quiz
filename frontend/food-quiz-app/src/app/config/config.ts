import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { SettingsService } from '../core/settings';

@Component({
  selector: 'app-config',
  imports: [FormsModule, RouterLink],
  templateUrl: './config.html',
  styleUrl: './config.scss',
})
export class Config {
  protected settings = inject(SettingsService);
  private router = inject(Router);

  protected count = signal(this.settings.questionCount());

  protected get isValid(): boolean {
    return this.count() >= 1 && this.count() <= 20;
  }

  save(): void {
    if (!this.isValid) return;
    this.settings.setQuestionCount(this.count());
    this.router.navigate(['/']);
  }
}
