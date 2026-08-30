import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AnyResourceResponse } from '../../core/api/models';
import { NumberFormatPipe } from '../../core/ui/number-format.pipe';
import { ValuePipe } from '../../core/ui/value.pipe';
import { ResourceCatalogEntry } from '../../core/api/resource-catalog';

@Component({
  selector: 'app-resource-card',
  imports: [RouterLink, ValuePipe, NumberFormatPipe],
  templateUrl: './resource-card.html',
  styleUrl: './resource-card.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResourceCard {
  readonly item = input.required<AnyResourceResponse>();
  readonly catalog = input.required<ResourceCatalogEntry>();
  readonly index = input(0);

  protected readonly id = computed(() => this.item().id);
  protected readonly link = computed(() => ['/', this.catalog().key, this.id()]);
  protected readonly transitionName = computed(
    () => `card-title-${this.catalog().key}-${this.id()}`,
  );

  /** Stagger delay index, capped so a long page's tail doesn't crawl in. */
  protected readonly staggerIndex = computed(() => Math.min(this.index(), 11));

  protected fieldValue(key: string): string | number | null {
    const record = this.item() as unknown as Record<string, unknown>;
    return (record[key] ?? null) as string | number | null;
  }
}
