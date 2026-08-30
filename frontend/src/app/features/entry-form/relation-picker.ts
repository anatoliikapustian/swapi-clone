import { ChangeDetectionStrategy, Component, computed, input, output, signal } from '@angular/core';
import { ResourceKey } from '../../core/api/models';
import { catalogFor } from '../../core/api/resource-catalog';
import { listResource } from '../../core/api/swapi-client';

export interface RelationOption {
  id: number;
  label: string;
}

const DEBOUNCE_MS = 300;

/** Search-to-select combobox for a writable relation field; backs both single and multi selection. */
@Component({
  selector: 'app-relation-picker',
  templateUrl: './relation-picker.html',
  styleUrl: './relation-picker.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelationPicker {
  readonly target = input.required<ResourceKey>();
  readonly label = input.required<string>();
  readonly multi = input(true);
  readonly selected = input<RelationOption[]>([]);
  readonly selectedChange = output<RelationOption[]>();

  protected readonly catalog = computed(() => catalogFor(this.target()));
  protected readonly draft = signal('');
  protected readonly query = signal('');
  protected readonly open = signal(false);
  private debounceHandle?: ReturnType<typeof setTimeout>;

  protected readonly searchState = listResource(() =>
    this.open()
      ? { resource: this.target(), params: { search: this.query(), page: 0, size: 8, sort: [] } }
      : undefined,
  );

  protected readonly results = computed<RelationOption[]>(() => {
    const selectedIds = new Set(this.selected().map((option) => option.id));
    const items = this.searchState.value()?.content ?? [];
    return items
      .map((item) => this.toOption(item as unknown as Record<string, unknown>))
      .filter((option) => !selectedIds.has(option.id));
  });

  protected onFocus(): void {
    this.open.set(true);
  }

  protected onInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.draft.set(value);
    this.open.set(true);
    clearTimeout(this.debounceHandle);
    this.debounceHandle = setTimeout(() => this.query.set(value), DEBOUNCE_MS);
  }

  protected onBlur(): void {
    // Deferred so a pointerdown on an option (below) registers before the list closes.
    setTimeout(() => this.open.set(false), 150);
  }

  protected select(option: RelationOption): void {
    this.selectedChange.emit(this.multi() ? [...this.selected(), option] : [option]);
    this.draft.set('');
    this.query.set('');
    this.open.set(false);
  }

  protected remove(id: number): void {
    this.selectedChange.emit(this.selected().filter((option) => option.id !== id));
  }

  private toOption(item: Record<string, unknown>): RelationOption {
    const id = Number(item['id']);
    const label = String(item[this.catalog().titleField] ?? `#${id}`);
    return { id, label };
  }
}
