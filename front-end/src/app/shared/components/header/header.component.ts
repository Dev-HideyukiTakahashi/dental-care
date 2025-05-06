import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Output } from '@angular/core';
import { AuthService } from '../../../core/service/auth.service';

@Component({
  selector: 'app-header',
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  private authService = inject(AuthService);
  showLogoutModal = false;

  @Output() toggleSidebarEvent = new EventEmitter<void>();
  sidebarCollapsed: boolean = false;

  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
    this.toggleSidebarEvent.emit();
  }

  openLogoutModal() {
    this.showLogoutModal = true;
  }

  closeModal() {
    this.showLogoutModal = false;
  }

  logout() {
    this.authService.logout();
    this.closeModal();
  }
}
