import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';

const PAGE_SIZES = [10, 20, 50] as const;

@Component({
  selector: 'app-paginator',
  template: `
    <nav class="paginator" aria-label="Pagination">
      <button
        type="button"
        class="paginator__nav"
        [disabled]="page() <= 0"
        (click)="pageChange.emit(page() - 1)"
      >
        <span aria-hidden="true">‹</span> Prev
      </button>

      <span class="paginator__status">
        Page {{ totalPages() === 0 ? 0 : page() + 1 }} of {{ totalPages() }}
      </span>

      <button
        type="button"
        class="paginator__nav"
        [disabled]="isLastPage()"
        (click)="pageChange.emit(page() + 1)"
      >
        Next <span aria-hidden="true">›</span>
      </button>

      <label class="paginator__size">
        <span class="paginator__size-label">Per page</span>
        <select (change)="onSizeChange($event)">
          @for (option of pageSizes; track option) {
            <option [value]="option" [selected]="option === size()">{{ option }}</option>
          }
        </select>
      </label>
    </nav>
  `,
  styleUrl: './paginator.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Paginator {
  readonly page = input(0);
  readonly totalPages = input(0);
  readonly size = input(10);

  readonly pageChange = output<number>();
  readonly sizeChange = output<number>();

  protected readonly pageSizes = PAGE_SIZES;
  protected readonly isLastPage = computed(() => this.page() >= this.totalPages() - 1);

  protected onSizeChange(event: Event): void {
    this.sizeChange.emit(Number((event.target as HTMLSelectElement).value));
  }
}
