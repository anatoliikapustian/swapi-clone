import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { ProblemDetail } from '../../core/api/models';

@Component({
  selector: 'app-error-state',
  template: `
    <div class="error-state" role="alert">
      <svg class="error-state__icon" viewBox="0 0 24 24" aria-hidden="true">
        <path
          d="M12 3.5 21.5 20h-19L12 3.5Z"
          fill="none"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linejoin="round"
        />
        <line x1="12" y1="9.5" x2="12" y2="14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
        <circle cx="12" cy="16.75" r="1" fill="currentColor" />
      </svg>
      <p class="error-state__title">{{ title() }}</p>
      <p class="error-state__detail">{{ detail() }}</p>
      <button type="button" class="error-state__retry" (click)="retry.emit()">Try again</button>
    </div>
  `,
  styleUrl: './error-state.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorState {
  readonly problem = input<ProblemDetail | null>(null);
  readonly retry = output<void>();

  protected title(): string {
    return this.problem()?.title ?? 'Something went wrong';
  }

  protected detail(): string {
    return this.problem()?.detail ?? 'The request could not be completed. Please try again.';
  }
}
