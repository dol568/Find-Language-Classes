import { Component, EventEmitter, InputSignal, Output, effect, input } from '@angular/core';
import { IComment } from '../../../../../shared/_models/ILanguageClass';
import { IPage } from '../../../../../shared/_models/IPage';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pager.component.html',
  styleUrl: './pager.component.scss',
})
export class PagerComponent {
  comments: InputSignal<IPage<IComment>> = input.required<IPage<IComment>>();
  @Output() pageChanged = new EventEmitter<number>();
  page = input.required<number>();

  getArrayData() {
    return Array.from({ length: this.comments()?.totalPages }, (v, i) => i + 1);
  }

  onPagerChange(page: number) {
    if (page >= 0 && page < this.comments()?.totalPages && page !== this.page()) {
      console.log(page)
      this.pageChanged.emit(page);
    }
  }
}
