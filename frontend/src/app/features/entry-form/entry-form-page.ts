import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { API_BASE } from '../../core/api/api-base';
import { EntryApi } from '../../core/api/entry-api';
import { AnyResourceResponse, ProblemDetail, ResourceCreateRequestMap, ResourceKey } from '../../core/api/models';
import { FormFieldDef, RelationFieldDef, catalogFor } from '../../core/api/resource-catalog';
import { parseResourceUrl } from '../../core/api/resource-url';
import { itemResource } from '../../core/api/swapi-client';
import { EmptyState } from '../../shared/empty-state/empty-state';
import { ErrorState } from '../../shared/error-state/error-state';
import { RelationOption, RelationPicker } from './relation-picker';

@Component({
  selector: 'app-entry-form-page',
  imports: [FormsModule, RouterLink, RelationPicker, ErrorState, EmptyState],
  templateUrl: './entry-form-page.html',
  styleUrl: './entry-form-page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EntryFormPage {
  // Bound from the route params via withComponentInputBinding(). `id` is present only on the
  // `/:resource/:id/edit` route — its absence is how create vs. edit mode is distinguished.
  readonly resource = input.required<string>();
  readonly id = input<string>();

  private readonly api = inject(EntryApi);
  private readonly http = inject(HttpClient);
  private readonly apiBase = inject(API_BASE);
  private readonly router = inject(Router);

  protected readonly catalog = computed(() => catalogFor(this.resource() as ResourceKey));
  protected readonly isEdit = computed(() => !!this.id());
  protected readonly idNum = computed(() => (this.id() ? Number(this.id()) : null));
  protected readonly writableRelations = computed(() => this.catalog().relationFields.filter((r) => r.writable));
  protected readonly titleFieldKey = computed(() => this.catalog().titleField);

  protected readonly itemState = itemResource(() =>
    this.isEdit() ? { resource: this.resource() as ResourceKey, id: this.idNum()! } : undefined,
  );

  protected readonly isNotFound = computed(() => {
    const err = this.itemState.error();
    return err instanceof HttpErrorResponse && err.status === 404;
  });

  protected readonly loadProblem = computed<ProblemDetail | null>(() => {
    const err = this.itemState.error();
    if (!err || this.isNotFound()) {
      return null;
    }
    return this.toProblem(err);
  });

  protected readonly formModel = signal<Record<string, unknown>>({});
  protected readonly relationModel = signal<Record<string, RelationOption[]>>({});
  protected readonly submitting = signal(false);
  protected readonly submitError = signal<ProblemDetail | null>(null);

  private readonly populated = signal(false);

  protected readonly isValid = computed(() => {
    const value = this.formModel()[this.titleFieldKey()];
    return typeof value === 'string' && value.trim().length > 0;
  });

  constructor() {
    effect(() => {
      const resource = this.resource();
      if (!this.isEdit()) {
        this.populated.set(true);
        this.formModel.set({});
        this.relationModel.set({});
        return;
      }
      const item = this.itemState.value();
      if (!item || this.populated()) {
        return;
      }
      this.populated.set(true);
      this.populateForm(resource as ResourceKey, item);
    });
  }

  protected setField(key: string, value: unknown): void {
    this.formModel.update((model) => ({ ...model, [key]: value }));
  }

  protected onRelationChange(key: string, options: RelationOption[]): void {
    this.relationModel.update((model) => ({ ...model, [key]: options }));
  }

  protected async submit(): Promise<void> {
    if (this.submitting() || !this.isValid()) {
      return;
    }
    this.submitting.set(true);
    this.submitError.set(null);

    const resource = this.resource() as ResourceKey;
    const body = this.buildBody() as unknown as ResourceCreateRequestMap[ResourceKey];
    const request$ = this.isEdit() ? this.api.update(resource, this.idNum()!, body) : this.api.create(resource, body);

    try {
      const saved = await firstValueFrom(request$);
      await this.router.navigate(['/', resource, saved.id]);
    } catch (err) {
      this.submitError.set(this.toProblem(err));
    } finally {
      this.submitting.set(false);
    }
  }

  private buildBody(): Record<string, unknown> {
    const body: Record<string, unknown> = {};
    for (const field of this.catalog().formFields) {
      body[field.key] = this.normalizeScalar(field, this.formModel()[field.key]);
    }
    for (const relation of this.writableRelations()) {
      const options = this.relationModel()[relation.key] ?? [];
      body[relation.key] = relation.multi ? options.map((o) => o.id) : (options[0]?.id ?? null);
    }
    return body;
  }

  private normalizeScalar(field: FormFieldDef, raw: unknown): string | number | null {
    if (field.type === 'number') {
      if (raw === '' || raw === null || raw === undefined) {
        return null;
      }
      const num = Number(raw);
      return Number.isNaN(num) ? null : num;
    }
    const str = raw === null || raw === undefined ? '' : String(raw).trim();
    if (field.required) {
      return str;
    }
    return str === '' ? null : str;
  }

  private populateForm(resource: ResourceKey, item: AnyResourceResponse): void {
    const record = item as unknown as Record<string, unknown>;

    const scalars: Record<string, unknown> = {};
    for (const field of catalogFor(resource).formFields) {
      scalars[field.key] = record[field.key] ?? '';
    }
    this.formModel.set(scalars);

    for (const relation of this.writableRelations()) {
      this.resolveRelation(relation, record);
    }
  }

  private resolveRelation(relation: RelationFieldDef, record: Record<string, unknown>): void {
    const raw = record[relation.key];
    const urls = relation.multi
      ? Array.isArray(raw)
        ? (raw as string[])
        : []
      : typeof raw === 'string' && raw
        ? [raw]
        : [];
    const ids = urls
      .map((url) => parseResourceUrl(url)?.id)
      .filter((relId): relId is number => relId !== undefined && relId !== null);

    if (ids.length === 0) {
      return;
    }

    void this.resolveOptions(relation.target, ids).then((options) => {
      this.relationModel.update((model) => ({ ...model, [relation.key]: options }));
    });
  }

  private async resolveOptions(target: ResourceKey, ids: number[]): Promise<RelationOption[]> {
    const targetCatalog = catalogFor(target);
    const items = await Promise.all(
      ids.map((relId) =>
        firstValueFrom(
          this.http.get<Record<string, unknown>>(`${this.apiBase}${targetCatalog.endpoint}/${relId}`),
        ).catch(() => null),
      ),
    );
    return items
      .map((item, i) =>
        item ? { id: ids[i]!, label: String(item[targetCatalog.titleField] ?? `#${ids[i]}`) } : null,
      )
      .filter((option): option is RelationOption => option !== null);
  }

  private toProblem(err: unknown): ProblemDetail {
    if (err instanceof HttpErrorResponse && err.error && typeof err.error === 'object') {
      return err.error as ProblemDetail;
    }
    return { title: 'Request failed', detail: 'The request could not be completed.' };
  }
}
