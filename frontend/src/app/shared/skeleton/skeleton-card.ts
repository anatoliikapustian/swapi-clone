import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-skeleton-card',
  template: `
    <div class="skeleton-card" aria-hidden="true">
      <div class="skeleton-card__line skeleton-card__line--title"></div>
      <div class="skeleton-card__line skeleton-card__line--sub"></div>
      <div class="skeleton-card__line skeleton-card__line--sub short"></div>
    </div>
  `,
  styleUrl: './skeleton-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SkeletonCard {}
