import { httpResource, HttpResourceRef } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_BASE } from './api-base';
import { PageResponse, ResourceKey, ResourceResponseMap } from './models';
import { catalogFor } from './resource-catalog';

export interface ListParams {
  search: string;
  page: number;
  size: number;
  /** e.g. ['name,asc'] — repeatable server-side sort param. */
  sort: string[];
}

export interface ListRequest<K extends ResourceKey> {
  resource: K;
  params: ListParams;
}

export interface ItemRequest<K extends ResourceKey> {
  resource: K;
  id: number;
}

/** Reactive paged-list fetch: re-runs whenever the request signal's return value changes. */
export function listResource<K extends ResourceKey>(
  request: () => ListRequest<K> | undefined,
): HttpResourceRef<PageResponse<ResourceResponseMap[K]> | undefined> {
  const apiBase = inject(API_BASE);

  return httpResource<PageResponse<ResourceResponseMap[K]>>(() => {
    const req = request();
    if (!req) {
      return undefined;
    }

    const { resource, params } = req;
    const search: Record<string, string | readonly string[]> = {
      page: String(params.page),
      size: String(params.size),
    };
    if (params.search.trim()) {
      search['search'] = params.search.trim();
    }
    if (params.sort.length > 0) {
      search['sort'] = params.sort;
    }

    return {
      url: `${apiBase}${catalogFor(resource).endpoint}`,
      params: search,
    };
  });
}

/** Reactive single-item fetch. */
export function itemResource<K extends ResourceKey>(
  request: () => ItemRequest<K> | undefined,
): HttpResourceRef<ResourceResponseMap[K] | undefined> {
  const apiBase = inject(API_BASE);

  return httpResource<ResourceResponseMap[K]>(() => {
    const req = request();
    if (!req) {
      return undefined;
    }
    return { url: `${apiBase}${catalogFor(req.resource).endpoint}/${req.id}` };
  });
}

/** Untyped single-item fetch by absolute or relative URL — used to resolve relation chip names. */
export function urlResource(
  request: () => string | undefined,
): HttpResourceRef<{ name?: string; title?: string } | undefined> {
  return httpResource<{ name?: string; title?: string }>(() => {
    const url = request();
    return url ? { url } : undefined;
  });
}
