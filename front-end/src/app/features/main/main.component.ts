import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-main',
  imports: [RouterOutlet, HeaderComponent, SidebarComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
})
export class MainComponent {
  currentUser = 'Start Bootstrap';
  currentYear = new Date().getFullYear();
  sidebarCollapsed = false;

  toggleSidebar() {
    this.sidebarCollapsed = !this.sidebarCollapsed;
    const sidebar = document.getElementById('layoutSidenav_nav');
    const content = document.getElementById('layoutSidenav_content');

    if (this.sidebarCollapsed) {
      sidebar!.style.transform = 'translateX(-225px)';
      content!.style.marginLeft = '0';
    } else {
      sidebar!.style.transform = 'translateX(0)';
      content!.style.marginLeft = '225px';
    }

    sidebar!.style.pointerEvents = this.sidebarCollapsed ? 'none' : 'auto';
  }
}
