import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';

@Component({
  selector: 'app-opening-crawl',
  template: `
    <div class="opening-crawl">
      <div class="opening-crawl__viewport" [class.opening-crawl__viewport--playing]="playing()">
        <p class="opening-crawl__text">{{ text() }}</p>
      </div>
      <button
        type="button"
        class="opening-crawl__control"
        [attr.aria-pressed]="playing()"
        (click)="toggle()"
      >
        <span aria-hidden="true">{{ playing() ? '❚❚' : '▶' }}</span>
        {{ playing() ? 'Pause crawl' : 'Play crawl' }}
      </button>
    </div>
  `,
  styleUrl: './opening-crawl.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpeningCrawl {
  readonly text = input<string | null>(null);
  protected readonly playing = signal(false);

  protected toggle(): void {
    this.playing.update((value) => !value);
  }
}
