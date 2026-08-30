import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catalogFor } from '../../core/api/resource-catalog';
import { listResource } from '../../core/api/swapi-client';
import { ProblemDetail, ResourceKey } from '../../core/api/models';
import { EmptyState } from '../../shared/empty-state/empty-state';
import { ErrorState } from '../../shared/error-state/error-state';
import { SkeletonCard } from '../../shared/skeleton/skeleton-card';
import { Paginator } from './paginator';
import { ResourceCard } from './resource-card';
import { SearchField } from './search-field';
import { SortChange, SortDirection, SortSelect } from './sort-select';

@Component({
  selector: 'app-list-page',
  imports: [RouterLink, SearchField, SortSelect, Paginator, ResourceCard, SkeletonCard, EmptyState, ErrorState],
  templateUrl: './list-page.html',
  styleUrl: './list-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListPage {
  // Bound from the route/query params via withComponentInputBinding().
  readonly resource = input.required<string>();
  readonly search = input('');
  readonly page = input('0');
  readonly size = input('10');
  readonly sort = input('');

  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly catalog = computed(() => catalogFor(this.resource() as ResourceKey));

  // `search` can be `undefined` once the query param has been cleared (router's
  // `unmatchedInputBehavior: 'undefinedIfStale'` — see app.config.ts), even though its declared
  // default is `''`. Coalesce everywhere it's read so `undefined` never reaches the DOM.
  protected readonly searchTerm = computed(() => this.search() ?? '');

  protected readonly pageNum = computed(() => Number(this.page()) || 0);
  protected readonly sizeNum = computed(() => Number(this.size()) || 10);

  protected readonly sortField = computed(() => {
    const [field] = this.sort().split(',');
    return field || this.catalog().sortOptions[0]?.value || '';
  });
  protected readonly sortDirection = computed<SortDirection>(() =>
    this.sort().split(',')[1] === 'desc' ? 'desc' : 'asc',
  );

  protected readonly resourceState = listResource(() => ({
    resource: this.resource() as ResourceKey,
    params: {
      search: this.searchTerm(),
      page: this.pageNum(),
      size: this.sizeNum(),
      sort: this.sortField() ? [`${this.sortField()},${this.sortDirection()}`] : [],
    },
  }));

  protected readonly items = computed(() => this.resourceState.value()?.content ?? []);
  protected readonly totalPages = computed(() => this.resourceState.value()?.page.totalPages ?? 0);
  protected readonly totalElements = computed(
    () => this.resourceState.value()?.page.totalElements ?? 0,
  );

  protected readonly skeletonPlaceholders = computed(() =>
    Array.from({ length: Math.min(this.sizeNum(), 8) }, (_, i) => i),
  );

  protected readonly problem = computed<ProblemDetail | null>(() => {
    const err = this.resourceState.error();
    if (!err) {
      return null;
    }
    if (err instanceof HttpErrorResponse && err.error && typeof err.error === 'object') {
      return err.error as ProblemDetail;
    }
    return { title: 'Request failed', detail: 'The request could not be completed.' };
  });

  protected onSearchChange(value: string): void {
    this.navigate({ search: value || null, page: null });
  }

  protected onSortChange(change: SortChange): void {
    this.navigate({ sort: `${change.field},${change.direction}`, page: null });
  }

  protected onPageChange(page: number): void {
    this.navigate({ page: page === 0 ? null : page });
  }

  protected onSizeChange(size: number): void {
    this.navigate({ size: size === 10 ? null : size, page: null });
  }

  protected retry(): void {
    this.resourceState.reload();
  }

  private navigate(queryParams: Record<string, unknown>): void {
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams,
      queryParamsHandling: 'merge',
    });
  }
}
