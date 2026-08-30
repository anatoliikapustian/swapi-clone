import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formats a numeric-looking string with thousands separators, e.g. "1000000000" → "1,000,000,000".
 * Leaves non-numeric values (null, "unknown", "n/a", free text) untouched so `ValuePipe`
 * can handle the placeholder afterwards — chain as `value | numberFormat | value`.
 */
@Pipe({ name: 'numberFormat' })
export class NumberFormatPipe implements PipeTransform {
  transform(value: string | number | null | undefined): string | number | null | undefined {
    if (value === null || value === undefined || value === '') {
      return value;
    }
    const numeric = Number(value);
    if (!Number.isFinite(numeric)) {
      return value;
    }
    return numeric.toLocaleString('en-US');
  }
}
