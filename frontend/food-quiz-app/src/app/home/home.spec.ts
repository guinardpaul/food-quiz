import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SettingsService } from '../core/settings';
import { Home } from './home';

describe('Home', () => {
  let fixture: ComponentFixture<Home>;
  let settings: SettingsService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    fixture = TestBed.createComponent(Home);
    settings = TestBed.inject(SettingsService);
    fixture.detectChanges();
  });

  it('should show default count in start button', () => {
    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn--primary');
    expect(btn.textContent).toContain('5 questions');
  });

  it('should reflect updated count in start button', () => {
    settings.setQuestionCount(8);
    fixture.detectChanges();
    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn--primary');
    expect(btn.textContent).toContain('8 questions');
  });
});
