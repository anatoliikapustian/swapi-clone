import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProblemDetail, ResourceKey } from '../../core/api/models';
import { catalogFor } from '../../core/api/resource-catalog';
import { itemResource } from '../../core/api/swapi-client';
import { EmptyState } from '../../shared/empty-state/empty-state';
import { ErrorState } from '../../shared/error-state/error-state';
import { ValuePipe } from '../../core/ui/value.pipe';
import { FieldGrid } from './field-grid';
import { OpeningCrawl } from './opening-crawl';
import { RelationChips } from './relation-chips';

@Component({
  selector: 'app-detail-page',
  imports: [RouterLink, ValuePipe, FieldGrid, RelationChips, OpeningCrawl, ErrorState, EmptyState],
  templateUrl: './detail-page.html',
  styleUrl: './detail-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailPage {
  // Bound from the route params via withComponentInputBinding().
  readonly resource = input.required<string>();
  readonly id = input.required<string>();

  protected readonly catalog = computed(() => catalogFor(this.resource() as ResourceKey));
  protected readonly idNum = computed(() => Number(this.id()));

  protected readonly itemState = itemResource(() => ({
    resource: this.resource() as ResourceKey,
    id: this.idNum(),
  }));

  protected readonly isNotFound = computed(() => {
    const err = this.itemState.error();
    return err instanceof HttpErrorResponse && err.status === 404;
  });

  protected readonly problem = computed<ProblemDetail | null>(() => {
    const err = this.itemState.error();
    if (!err || this.isNotFound()) {
      return null;
    }
    if (err instanceof HttpErrorResponse && err.error && typeof err.error === 'object') {
      return err.error as ProblemDetail;
    }
    return { title: 'Request failed', detail: 'The request could not be completed.' };
  });

  protected readonly titleValue = computed(() => this.fieldValue(this.catalog().titleField));
  protected readonly crawlText = computed(() => {
    const value = this.fieldValue('openingCrawl');
    // The source SWAPI dataset stores line breaks as literal `\r\n` / `\n` escape
    // sequences rather than real newlines — unescape them for the crawl panel.
    return value === null ? null : String(value).replace(/\\r\\n|\\n/g, '\n');
  });
  protected readonly transitionName = computed(
    () => `card-title-${this.resource()}-${this.idNum()}`,
  );

  protected retry(): void {
    this.itemState.reload();
  }

  protected fieldValue(key: string): string | number | null {
    const item = this.itemState.value();
    if (!item) {
      return null;
    }
    const record = item as unknown as Record<string, unknown>;
    return (record[key] ?? null) as string | number | null;
  }
}
