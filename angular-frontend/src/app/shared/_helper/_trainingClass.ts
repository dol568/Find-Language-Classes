export const timeToNumber = (time: string) => {
    if (time != null) return parseInt(time.replace(/:/g, ''), 10);
    return null;
}
