export function generateEncryptionKey(userPassword, salt) {
  return Java_InterfaceCrypto.generateEncryptionKey(userPassword, salt);
}

export function hashUserPassword(userPassword, salt) {
  return Java_InterfaceCrypto.hashUserPassword(userPassword, salt);
}