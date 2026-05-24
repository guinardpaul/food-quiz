import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Quiz } from './quiz/quiz';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'quiz/:quizId', component: Quiz },
  { path: '**', redirectTo: '' },
];
