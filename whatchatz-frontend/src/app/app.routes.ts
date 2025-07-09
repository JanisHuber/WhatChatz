import { Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', redirectTo: '/whatchatz', pathMatch: 'full' },
    {
        path: 'whatchatz',
        loadChildren: () => import('./pages/whatchatz-routing').then(m => m.routes)
    },
    { path: '**', redirectTo: '/whatchatz' }
];
