import { ChangeDetectionStrategy, Component, effect, input, output, signal } from '@angular/core';

const DEBOUNCE_MS = 300;

@Component({
  selector: 'app-search-field',
  template: `
    <label class="search-field">
      <svg class="search-field__icon" viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" stroke-width="1.5" />
        <line x1="16.2" y1="16.2" x2="21" y2="21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
      </svg>
      <span class="search-field__label">Search {{ label() }}</span>
      <input
        type="search"
        class="search-field__input"
        [placeholder]="'Search ' + label() + '…'"
        [value]="draft()"
        (input)="onInput($event)"
      />
    </label>
  `,
  styleUrl: './search-field.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchField {
  readonly value = input('');
  readonly label = input('items');
  readonly searchChange = output<string>();

  protected readonly draft = signal('');
  private debounceHandle?: ReturnType<typeof setTimeout>;

  constructor() {
    effect(() => {
      // `value` can arrive as `undefined` when the router's query param binding considers it
      // stale (see app.config.ts) — coalesce so the native input's `value` property never gets
      // set to `undefined`, which the DOM stringifies to the literal text "undefined".
      this.draft.set(this.value() ?? '');
    });
  }

  protected onInput(event: Event): void {
    const next = (event.target as HTMLInputElement).value;
    this.draft.set(next);
    clearTimeout(this.debounceHandle);
    this.debounceHandle = setTimeout(() => this.searchChange.emit(next), DEBOUNCE_MS);
  }
}
