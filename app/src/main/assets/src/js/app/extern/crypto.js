export function generateKey(plainUserPassword, salt) {
  return Java_InterfaceCrypto.generateKey(plainUserPassword, salt);
}

export function hashPassword(plainUserPassword, salt) {
  return Java_InterfaceCrypto.hashPassword(plainUserPassword, salt);
}