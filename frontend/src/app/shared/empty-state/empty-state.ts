import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  template: `
    <div class="empty-state" role="status">
      <svg class="empty-state__icon" viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" stroke-width="1.5" />
        <line x1="16.2" y1="16.2" x2="21" y2="21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </svg>
      <p class="empty-state__title">{{ title() }}</p>
      @if (detail()) {
        <p class="empty-state__detail">{{ detail() }}</p>
      }
    </div>
  `,
  styleUrl: './empty-state.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmptyState {
  readonly title = input('Nothing here yet.');
  readonly detail = input<string | null>(null);
}
