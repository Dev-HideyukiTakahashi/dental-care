import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  @Output() toggleSidebarEvent = new EventEmitter<void>();
  sidebarCollapsed: boolean = false;

  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
    this.toggleSidebarEvent.emit();
  }
}
