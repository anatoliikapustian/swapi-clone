import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ResourceKey } from '../../core/api/models';
import { urlResource } from '../../core/api/swapi-client';

/** Resolves a relation's display name lazily — instantiated only once scrolled into view. */
@Component({
  selector: 'app-relation-chip',
  imports: [RouterLink],
  template: `
    <a class="relation-chip" [routerLink]="link()">
      @if (nameState.isLoading()) {
        <span class="relation-chip__skeleton" aria-hidden="true"></span>
      } @else {
        {{ displayName() }}
      }
    </a>
  `,
  styleUrl: './relation-chip.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelationChip {
  readonly url = input.required<string>();
  readonly resource = input.required<ResourceKey>();
  readonly id = input.required<number>();

  protected readonly link = computed(() => ['/', this.resource(), this.id()]);
  protected readonly nameState = urlResource(() => this.url());

  protected readonly displayName = computed(() => {
    const value = this.nameState.value();
    return value?.title || value?.name || `#${this.id()}`;
  });
}
