export function generateChatId(uid1: string, uid2: string): string {
    if (uid1.localeCompare(uid2) < 0) {
      return uid1 + '__' + uid2;
    } else {
      return uid2 + '__' + uid1;
    }
}