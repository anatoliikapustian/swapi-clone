import { Routes } from '@angular/router';
import { resourceGuard } from './core/api/resource.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/films' },
  {
    path: ':resource',
    canMatch: [resourceGuard],
    loadComponent: () => import('./features/list/list-page').then((m) => m.ListPage),
  },
  {
    path: ':resource/new',
    canMatch: [resourceGuard],
    loadComponent: () => import('./features/entry-form/entry-form-page').then((m) => m.EntryFormPage),
  },
  {
    path: ':resource/:id/edit',
    canMatch: [resourceGuard],
    loadComponent: () => import('./features/entry-form/entry-form-page').then((m) => m.EntryFormPage),
  },
  {
    path: ':resource/:id',
    canMatch: [resourceGuard],
    loadComponent: () => import('./features/detail/detail-page').then((m) => m.DetailPage),
  },
  {
    path: '**',
    loadComponent: () => import('./features/not-found/not-found-page').then((m) => m.NotFoundPage),
  },
];
