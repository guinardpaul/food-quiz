import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SettingsService } from '../core/settings';
import { Config } from './config';

describe('Config', () => {
  let fixture: ComponentFixture<Config>;
  let settings: SettingsService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Config],
      providers: [provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(Config);
    settings = TestBed.inject(SettingsService);
    fixture.detectChanges();
  });

  it('should show current questionCount from settings', () => {
    const input: HTMLInputElement = fixture.nativeElement.querySelector('input[type="number"]');
    expect(Number(input.value)).toBe(5);
  });

  it('should disable save button when count is out of range', async () => {
    const input: HTMLInputElement = fixture.nativeElement.querySelector('input[type="number"]');
    input.value = '25';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn--primary');
    expect(btn.disabled).toBe(true);
  });

  it('should call setQuestionCount and navigate on save', async () => {
    const input: HTMLInputElement = fixture.nativeElement.querySelector('input[type="number"]');
    input.value = '8';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();

    const btn: HTMLButtonElement = fixture.nativeElement.querySelector('.btn--primary');
    btn.click();
    fixture.detectChanges();

    expect(settings.questionCount()).toBe(8);
  });
});
