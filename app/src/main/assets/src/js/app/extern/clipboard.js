export function copyToClipboard(text) {
  let timeout = -1;
  Java_InterfaceTools.copyToClipboardWithTimeout(text, timeout);
}

export function copyToClipboardWithTimeout(text, timeout) {
  Java_InterfaceTools.copyToClipboardWithTimeout(text, timeout);
}

export function clearClipboard(){
  Java_InterfaceTools.clearClipboard();
}