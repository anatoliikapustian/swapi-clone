import { CanMatchFn } from '@angular/router';
import { isResourceKey } from './resource-catalog';

/** Falls through to the wildcard NotFoundPage route instead of firing a doomed request. */
export const resourceGuard: CanMatchFn = (_route, segments) => {
  const resourceSegment = segments[0]?.path;
  return isResourceKey(resourceSegment);
};
