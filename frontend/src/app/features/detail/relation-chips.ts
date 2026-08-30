import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AnyResourceResponse } from '../../core/api/models';
import { RelationFieldDef } from '../../core/api/resource-catalog';
import { ParsedResourceUrl, parseResourceUrl } from '../../core/api/resource-url';
import { RelationChip } from './relation-chip';

interface ResolvedRelation {
  url: string;
  parsed: ParsedResourceUrl;
}

@Component({
  selector: 'app-relation-chips',
  imports: [RouterLink, RelationChip],
  templateUrl: './relation-chips.html',
  styleUrl: './relation-chips.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelationChips {
  readonly relationFields = input.required<RelationFieldDef[]>();
  readonly item = input.required<AnyResourceResponse>();

  protected resolve(relation: RelationFieldDef): ResolvedRelation[] {
    const record = this.item() as unknown as Record<string, unknown>;
    const raw = record[relation.key];
    const urls = relation.multi
      ? Array.isArray(raw)
        ? (raw as string[])
        : []
      : typeof raw === 'string' && raw
        ? [raw]
        : [];

    return urls
      .map((url) => {
        const parsed = parseResourceUrl(url);
        return parsed ? { url, parsed } : null;
      })
      .filter((entry): entry is ResolvedRelation => entry !== null);
  }
}
