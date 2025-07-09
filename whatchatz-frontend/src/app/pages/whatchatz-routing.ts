import { Routes } from "@angular/router";
import { AuthGuard } from "../core/guards/auth.guard";


export const routes: Routes = [
    {
        path: 'chat',
        loadComponent: () => import('./chat/chat.component').then(m => m.ChatComponent),
        canActivate: [AuthGuard]
    },
    {
        path: 'chat/:id',
        loadComponent: () => import('./chat/chat.component').then(m => m.ChatComponent),
        canActivate: [AuthGuard]
    },
    {
        path: 'user',
        loadComponent: () => import('./user/user.component').then(m => m.UserComponent)
    }
];