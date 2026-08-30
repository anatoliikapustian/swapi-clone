import { ValuePipe } from './value.pipe';

describe('ValuePipe', () => {
  const pipe = new ValuePipe();

  it.each([
    [null, '—'],
    [undefined, '—'],
    ['', '—'],
    ['unknown', '—'],
    ['UNKNOWN', '—'],
    ['n/a', '—'],
    ['N/A', '—'],
    ['Luke Skywalker', 'Luke Skywalker'],
    [172, '172'],
    [0, '0'],
  ])('transforms %p to %p', (input, expected) => {
    expect(pipe.transform(input as never)).toBe(expected);
  });
});
