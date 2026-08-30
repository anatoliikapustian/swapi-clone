import { provideZonelessChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideZonelessChangeDetection(), provideRouter([])],
    }).compileComponents();
  });

  it('creates the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('renders a nav link for each of the six resources', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const links = fixture.nativeElement.querySelectorAll('.primary-nav__link');
    expect(links.length).toBe(6);
  });
});
