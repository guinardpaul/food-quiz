import { Routes } from '@angular/router';
import { Config } from './config/config';
import { Home } from './home/home';
import { Quiz } from './quiz/quiz';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'config', component: Config },
  { path: 'quiz/:quizId', component: Quiz },
  { path: '**', redirectTo: '' },
];
