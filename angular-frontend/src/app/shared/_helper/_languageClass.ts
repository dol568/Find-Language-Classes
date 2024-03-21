import { ValidatorFn, Validators } from '@angular/forms';

export const timeToNumber = (time: string) => {
  if (time != null) return parseInt(time.replace(/:/g, ''), 10);
  return null;
};

export const patternWithMessage = (pattern: string | RegExp, message: string): ValidatorFn => {
  const delegateFn = Validators.pattern(pattern);
  return (control) => (delegateFn(control) === null ? null : { message });
};
