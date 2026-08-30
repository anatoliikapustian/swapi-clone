import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideZonelessChangeDetection } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { ListPage } from './list-page';

describe('ListPage', () => {
  let fixture: ComponentFixture<ListPage>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListPage],
      providers: [
        provideZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListPage);
    httpMock = TestBed.inject(HttpTestingController);

    fixture.componentRef.setInput('resource', 'films');
    fixture.componentRef.setInput('search', '');
    fixture.componentRef.setInput('page', '0');
    fixture.componentRef.setInput('size', '10');
    fixture.componentRef.setInput('sort', '');
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('renders a card per item from the resolved page', async () => {
    const req = httpMock.expectOne((r) => r.url === '/api/films');
    req.flush({
      content: [
        { id: 1, title: 'A New Hope', episodeId: 4, director: 'George Lucas', releaseDate: '1977-05-25' },
        { id: 2, title: 'The Empire Strikes Back', episodeId: 5, director: 'Irvin Kershner', releaseDate: '1980-05-17' },
      ],
      page: { size: 10, number: 0, totalElements: 2, totalPages: 1 },
    });
    await fixture.whenStable();
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll('app-resource-card');
    expect(cards.length).toBe(2);
  });

  it('resets the page and updates the search query param on search', () => {
    httpMock.expectOne((r) => r.url === '/api/films').flush({
      content: [],
      page: { size: 10, number: 0, totalElements: 0, totalPages: 0 },
    });

    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigate');
    (fixture.componentInstance as unknown as { onSearchChange(v: string): void }).onSearchChange('vader');

    expect(navigateSpy).toHaveBeenCalledWith(
      [],
      expect.objectContaining({ queryParams: { search: 'vader', page: null } }),
    );
  });

  it('disables pagination controls at both boundaries when there is a single page', async () => {
    const req = httpMock.expectOne((r) => r.url === '/api/films');
    req.flush({
      content: [{ id: 1, title: 'A New Hope' }],
      page: { size: 10, number: 0, totalElements: 1, totalPages: 1 },
    });
    await fixture.whenStable();
    fixture.detectChanges();

    const buttons: NodeListOf<HTMLButtonElement> =
      fixture.nativeElement.querySelectorAll('.paginator__nav');
    expect(buttons[0]!.disabled).toBe(true);
    expect(buttons[1]!.disabled).toBe(true);
  });

  it('shows the empty state when the search yields no results', async () => {
    // The initial fetch from beforeEach (search: '') is still pending — flush it first.
    httpMock
      .expectOne((r) => r.url === '/api/films')
      .flush({ content: [{ id: 1, title: 'A New Hope' }], page: { size: 10, number: 0, totalElements: 1, totalPages: 1 } });
    await fixture.whenStable();

    fixture.componentRef.setInput('search', 'zzz-no-match');
    fixture.detectChanges();

    const req = httpMock.expectOne((r) => r.url === '/api/films');
    req.flush({ content: [], page: { size: 10, number: 0, totalElements: 0, totalPages: 0 } });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('app-empty-state')).toBeTruthy();
  });

  it('never renders literal "undefined" in the search box when the query param goes stale', async () => {
    // Mirrors what the router's `unmatchedInputBehavior: 'undefinedIfStale'` does once a query
    // param that was previously set gets cleared: the input receives `undefined`, not `''`.
    httpMock
      .expectOne((r) => r.url === '/api/films')
      .flush({ content: [], page: { size: 10, number: 0, totalElements: 0, totalPages: 0 } });
    await fixture.whenStable();

    fixture.componentRef.setInput('search', undefined);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    const input: HTMLInputElement = fixture.nativeElement.querySelector('.search-field__input');
    expect(input.value).toBe('');
  });
});
