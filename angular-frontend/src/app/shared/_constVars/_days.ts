export const getDays = [
  { key: 0, text: 'Sunday' },
  { key: 1, text: 'Monday' },
  { key: 2, text: 'Tuesday' },
  { key: 3, text: 'Wednesday' },
  { key: 4, text: 'Thursday' },
  { key: 5, text: 'Friday' },
  { key: 6, text: 'Saturday' },
];

export const getDaysOfWeekWords = (dayOFWeek: number) => {
  return getDays.filter(x => x.key === dayOFWeek)[0].text;
}

export const getDaysOfWeekNumbers = (dayOFWeek: string) => {
  return getDays.filter(x => x.text === dayOFWeek)[0].key;
}

export const getFlagImg = (category: string) => {
  return `../../../../assets/${category.toLowerCase()}.jpg`;
}
