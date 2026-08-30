import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_BASE } from './api-base';
import { ResourceCreateRequestMap, ResourceKey, ResourceResponseMap } from './models';
import { catalogFor } from './resource-catalog';

/** Imperative create/update calls — mutations aren't a fit for the reactive `httpResource` factories. */
@Injectable({ providedIn: 'root' })
export class EntryApi {
  private readonly http = inject(HttpClient);
  private readonly apiBase = inject(API_BASE);

  create<K extends ResourceKey>(resource: K, body: ResourceCreateRequestMap[K]) {
    return this.http.post<ResourceResponseMap[K]>(`${this.apiBase}${catalogFor(resource).endpoint}`, body);
  }

  update<K extends ResourceKey>(resource: K, id: number, body: ResourceCreateRequestMap[K]) {
    return this.http.put<ResourceResponseMap[K]>(`${this.apiBase}${catalogFor(resource).endpoint}/${id}`, body);
  }
}
