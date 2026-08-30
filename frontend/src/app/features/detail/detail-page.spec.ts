import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { DetailPage } from './detail-page';

describe('DetailPage', () => {
  let fixture: ComponentFixture<DetailPage>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailPage],
      providers: [
        provideZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailPage);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('renders relations as router links produced by parseResourceUrl', async () => {
    fixture.componentRef.setInput('resource', 'films');
    fixture.componentRef.setInput('id', '1');
    fixture.detectChanges();

    const req = httpMock.expectOne((r) => r.url === '/api/films/1');
    req.flush({
      id: 1,
      title: 'A New Hope',
      episodeId: 4,
      openingCrawl: 'It is a period of civil war…',
      director: 'George Lucas',
      producer: 'Gary Kurtz',
      releaseDate: '1977-05-25',
      characters: ['http://localhost:8080/api/people/1'],
      planets: [],
      species: [],
      starships: [],
      vehicles: [],
      url: 'http://localhost:8080/api/films/1',
      created: '2024-01-01T00:00:00Z',
      edited: '2024-01-01T00:00:00Z',
    });
    await fixture.whenStable();
    fixture.detectChanges();

    // The chip's own httpResource is deferred `on viewport`, which never fires in
    // jsdom — so it's still showing the eager placeholder link, which is exactly
    // what should be visible before the chip resolves into view.
    const placeholder = (fixture.nativeElement as HTMLElement).querySelector<HTMLAnchorElement>(
      'a.relation-chip--placeholder',
    );
    expect(placeholder).toBeTruthy();
    expect(placeholder!.getAttribute('href')).toBe('/people/1');
  });

  it('renders a distinct not-found state on 404', async () => {
    fixture.componentRef.setInput('resource', 'films');
    fixture.componentRef.setInput('id', '999');
    fixture.detectChanges();

    const req = httpMock.expectOne((r) => r.url === '/api/films/999');
    req.flush(null, { status: 404, statusText: 'Not Found' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('app-empty-state')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('app-error-state')).toBeFalsy();
  });
});
