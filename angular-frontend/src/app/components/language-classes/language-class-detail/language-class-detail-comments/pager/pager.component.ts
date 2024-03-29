import {
  Component,
  EventEmitter,
  InputSignal,
  Output,
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
  comments: InputSignal<IComment[]> = input.required<IComment[]>();
  @Output() pageChanged = new EventEmitter<number>();
  page = input.required<number>();
  getArrayData = computed(() =>
    Array.from({ length: Math.ceil(this.comments()?.length / 5) }, (v, i) => i + 1)
  );

  onPagerChange(page: number) {
    if (page > 0 && page <= Math.ceil(this.comments()?.length / 5) && page !== this.page()) {
      this.pageChanged.emit(page);
    }
  }
}
