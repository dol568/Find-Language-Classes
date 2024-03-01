import { AuthenticatePipe } from './authenticate.pipe';

describe('AuthenticatePipe', () => {
  it('create an instance', () => {
    const pipe = new AuthenticatePipe();
    expect(pipe).toBeTruthy();
  });
});
