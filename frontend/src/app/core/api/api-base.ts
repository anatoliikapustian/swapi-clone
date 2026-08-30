import { InjectionToken } from '@angular/core';

/**
 * Base URL prepended to every API request. Defaults to '' (relative), which
 * works through the dev proxy (proxy.conf.json) and in any same-origin deployment.
 */
export const API_BASE = new InjectionToken<string>('API_BASE', {
  providedIn: 'root',
  factory: () => '',
});
