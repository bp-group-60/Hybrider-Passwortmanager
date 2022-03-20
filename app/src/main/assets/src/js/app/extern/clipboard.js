export function copyToClipboard(text) {
  let timeout = -1;
  Java_InterfaceClipboard.copyToClipboardWithTimeout(text, timeout);
}

export function copyToClipboardWithTimeout(text, timeout) {
  Java_InterfaceClipboard.copyToClipboardWithTimeout(text, timeout);
}

export function clearClipboard(){
  Java_InterfaceClipboard.clearClipboard();
}