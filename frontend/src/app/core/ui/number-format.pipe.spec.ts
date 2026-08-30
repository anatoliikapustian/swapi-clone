import { NumberFormatPipe } from './number-format.pipe';

describe('NumberFormatPipe', () => {
  const pipe = new NumberFormatPipe();

  it('formats a large numeric string with thousands separators', () => {
    expect(pipe.transform('1000000000')).toBe('1,000,000,000');
  });

  it('formats a plain number', () => {
    expect(pipe.transform(1234)).toBe('1,234');
  });

  it.each([[null], [undefined], [''], ['unknown'], ['n/a'], ['5.5 standard']])(
    'leaves non-numeric value %p untouched',
    (input) => {
      expect(pipe.transform(input as never)).toBe(input);
    },
  );
});
