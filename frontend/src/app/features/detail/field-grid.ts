import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { AnyResourceResponse } from '../../core/api/models';
import { DetailSection } from '../../core/api/resource-catalog';
import { NumberFormatPipe } from '../../core/ui/number-format.pipe';
import { ValuePipe } from '../../core/ui/value.pipe';

@Component({
  selector: 'app-field-grid',
  imports: [ValuePipe, NumberFormatPipe],
  templateUrl: './field-grid.html',
  styleUrl: './field-grid.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FieldGrid {
  readonly sections = input.required<DetailSection[]>();
  readonly item = input.required<AnyResourceResponse>();

  protected fieldValue(key: string): string | number | null {
    const record = this.item() as unknown as Record<string, unknown>;
    return (record[key] ?? null) as string | number | null;
  }
}
