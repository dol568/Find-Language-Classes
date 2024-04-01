import {
  Component,
  EventEmitter,
  InputSignal,
  Output,
  Signal,
  computed,
  input,
} from '@angular/core';
import { IComment } from '../../../../../shared/_models/ILanguageClass';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pager.component.html',
  styleUrl: './pager.component.scss',
})
export class PagerComponent {
  @Output() pageChanged: EventEmitter<number> = new EventEmitter<number>();
  comments: InputSignal<IComment[]> = input.required<IComment[]>();
  page: InputSignal<number> = input.required<number>();
  itemsPerPage: InputSignal<number> = input.required<number>();
  paginationLength: Signal<number> = computed(() =>
    Math.ceil(this.comments()?.length / this.itemsPerPage())
  );
  getArrayData: Signal<number[]> = computed(() =>
    Array.from({ length: this.paginationLength() }, (v, i) => i + 1)
  );

  public onPagerChange(page: number): void {
    if (page > 0 && page <= this.paginationLength() && page !== this.page()) {
      this.pageChanged.emit(page);
    }
  }
}
