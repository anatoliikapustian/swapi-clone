import { Pipe, PipeTransform } from '@angular/core';

const PLACEHOLDER = '—'; // em dash

const SENTINELS = new Set(['unknown', 'n/a']);

/** Renders null / "" / "unknown" / "n/a" uniformly as an em-dash placeholder. */
@Pipe({ name: 'value' })
export class ValuePipe implements PipeTransform {
  transform(value: string | number | null | undefined): string {
    if (value === null || value === undefined) {
      return PLACEHOLDER;
    }
    const stringValue = String(value).trim();
    if (stringValue === '' || SENTINELS.has(stringValue.toLowerCase())) {
      return PLACEHOLDER;
    }
    return stringValue;
  }
}
