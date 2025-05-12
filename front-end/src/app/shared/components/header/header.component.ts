import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/service/auth.service';
import { UserRole } from '../../../model/enum/user-role.enum';

@Component({
  selector: 'app-header',
  imports: [CommonModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  private authService = inject(AuthService);
  showLogoutModal = false;
  isAdmin = false;

  @Output() searchEvent = new EventEmitter<void>();
  @Output() toggleSidebarEvent = new EventEmitter<void>();
  sidebarCollapsed: boolean = false;

  get role(): UserRole | null {
    return this.authService.getRole();
  }

  onButtonSearch() {
    this.searchEvent.emit();
  }

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
