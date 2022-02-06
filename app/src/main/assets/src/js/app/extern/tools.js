export function copyToClipboard(text) {
  Java_InterfaceTools.copyToClipboardWithTimeout(text, -1);
}

export function copyToClipboardWithTimeout(text, timeout) {
  Java_InterfaceTools.copyToClipboardWithTimeout(text, timeout);
}

export function clearClipboard(){
  Java_InterfaceTools.clearClipboard();
}