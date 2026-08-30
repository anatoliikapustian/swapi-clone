import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { SortOption } from '../../core/api/resource-catalog';

export type SortDirection = 'asc' | 'desc';

export interface SortChange {
  field: string;
  direction: SortDirection;
}

@Component({
  selector: 'app-sort-select',
  template: `
    <div class="sort-select">
      <label class="sort-select__field">
        <span class="sort-select__label">Sort by</span>
        <select (change)="onFieldChange($event)">
          @for (option of options(); track option.value) {
            <option [value]="option.value" [selected]="option.value === field()">
              {{ option.label }}
            </option>
          }
        </select>
      </label>

      <button
        type="button"
        class="sort-select__direction"
        [attr.aria-label]="'Sort direction: ' + (direction() === 'asc' ? 'ascending' : 'descending')"
        (click)="toggleDirection()"
      >
        <span aria-hidden="true">{{ direction() === 'asc' ? '↑' : '↓' }}</span>
      </button>
    </div>
  `,
  styleUrl: './sort-select.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SortSelect {
  readonly options = input<SortOption[]>([]);
  readonly field = input('');
  readonly direction = input<SortDirection>('asc');
  readonly sortChange = output<SortChange>();

  protected onFieldChange(event: Event): void {
    this.sortChange.emit({
      field: (event.target as HTMLSelectElement).value,
      direction: this.direction(),
    });
  }

  protected toggleDirection(): void {
    this.sortChange.emit({
      field: this.field(),
      direction: this.direction() === 'asc' ? 'desc' : 'asc',
    });
  }
}
